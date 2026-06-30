package com.example.internshipapp.expense;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.internshipapp.common.enums.ExpenseDocumentType;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.expense.dto.ExpenseDocumentResponse;

@Service
public class ExpenseDocumentService {

    private static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg");
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".png", ".jpg", ".jpeg");

    private final ExpenseRepository expenseRepository;
    private final ExpenseDocumentRepository documentRepository;
    private final Path storageDirectory;

    public ExpenseDocumentService(
            ExpenseRepository expenseRepository,
            ExpenseDocumentRepository documentRepository,
            @Value("${app.storage.expense-documents:./uploads/expense-documents}") String storagePath) {
        this.expenseRepository = expenseRepository;
        this.documentRepository = documentRepository;
        this.storageDirectory = Path.of(storagePath).toAbsolutePath().normalize();
    }

    @Transactional
    public ExpenseDocumentResponse store(Long expenseId, ExpenseDocumentType documentType, MultipartFile file) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        validateFile(file);

        String originalFileName = cleanFileName(file);
        String extension = fileExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + extension;
        Path destination = storageDirectory.resolve(storedFileName).normalize();
        if (!destination.getParent().equals(storageDirectory)) {
            throw new IllegalArgumentException("Invalid document name");
        }

        try {
            Files.createDirectories(storageDirectory);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            ExpenseDocument document = new ExpenseDocument();
            document.setExpense(expense);
            document.setDocumentType(documentType);
            document.setOriginalFileName(originalFileName);
            document.setStoredFileName(storedFileName);
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            return toResponse(documentRepository.save(document));
        } catch (IOException exception) {
            tryDelete(destination);
            throw new IllegalArgumentException("Could not store expense document", exception);
        } catch (RuntimeException exception) {
            tryDelete(destination);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ExpenseDocumentResponse> findByExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new ResourceNotFoundException("Expense not found");
        }
        return documentRepository.findByExpenseIdOrderByCreatedAtDesc(expenseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseDocumentDownload load(Long documentId) {
        ExpenseDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense document not found"));
        Path filePath = storageDirectory.resolve(document.getStoredFileName()).normalize();
        if (!filePath.getParent().equals(storageDirectory) || !Files.isRegularFile(filePath)) {
            throw new ResourceNotFoundException("Expense document file not found");
        }
        return new ExpenseDocumentDownload(
                new FileSystemResource(filePath),
                document.getOriginalFileName(),
                document.getContentType());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("A non-empty document is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Document size must not exceed 10 MB");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF, PNG and JPEG documents are allowed");
        }
        String fileName = cleanFileName(file);
        if (fileName.isBlank() || fileName.contains("..")) {
            throw new IllegalArgumentException("Invalid document name");
        }
        if (!ALLOWED_EXTENSIONS.contains(fileExtension(fileName))) {
            throw new IllegalArgumentException("Document extension must be PDF, PNG, JPG or JPEG");
        }
    }

    private String cleanFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        return StringUtils.cleanPath(originalFileName == null ? "" : originalFileName);
    }

    private String fileExtension(String fileName) {
        int separatorIndex = fileName.lastIndexOf('.');
        return separatorIndex < 0 ? "" : fileName.substring(separatorIndex).toLowerCase();
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // The database error remains the primary failure.
        }
    }

    private ExpenseDocumentResponse toResponse(ExpenseDocument document) {
        return new ExpenseDocumentResponse(
                document.getId(),
                document.getExpense().getId(),
                document.getDocumentType(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getCreatedAt());
    }

    public record ExpenseDocumentDownload(Resource resource, String fileName, String contentType) {
    }
}
