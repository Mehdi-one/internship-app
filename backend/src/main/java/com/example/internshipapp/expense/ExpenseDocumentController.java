package com.example.internshipapp.expense;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.internshipapp.common.enums.ExpenseDocumentType;
import com.example.internshipapp.expense.ExpenseDocumentService.ExpenseDocumentDownload;
import com.example.internshipapp.expense.dto.ExpenseDocumentResponse;

@RestController
@RequestMapping("/api")
public class ExpenseDocumentController {

    private final ExpenseDocumentService documentService;

    public ExpenseDocumentController(ExpenseDocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/expenses/{expenseId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ExpenseDocumentResponse upload(
            @PathVariable Long expenseId,
            @RequestParam ExpenseDocumentType documentType,
            @RequestParam MultipartFile file) {
        return documentService.store(expenseId, documentType, file);
    }

    @GetMapping("/expenses/{expenseId}/documents")
    public List<ExpenseDocumentResponse> findByExpense(@PathVariable Long expenseId) {
        return documentService.findByExpense(expenseId);
    }

    @GetMapping("/expense-documents/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable Long documentId) {
        ExpenseDocumentDownload download = documentService.load(documentId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(download.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(download.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(download.resource());
    }
}
