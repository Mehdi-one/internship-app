package com.example.internshipapp.expense;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import com.example.internshipapp.common.enums.ExpenseDocumentType;

class ExpenseDocumentServiceTest {

    @TempDir
    Path storageDirectory;

    private ExpenseRepository expenseRepository;
    private ExpenseDocumentRepository documentRepository;
    private ExpenseDocumentService service;

    @BeforeEach
    void setUp() {
        expenseRepository = mock(ExpenseRepository.class);
        documentRepository = mock(ExpenseDocumentRepository.class);
        service = new ExpenseDocumentService(
                expenseRepository,
                documentRepository,
                storageDirectory.toString());
    }

    @Test
    void storesPdfWithMetadataOutsideDatabase() throws Exception {
        Expense expense = new Expense();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "facture-001.pdf",
                "application/pdf",
                "pdf-content".getBytes());
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(documentRepository.save(any(ExpenseDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.store(1L, ExpenseDocumentType.INVOICE, file);

        assertThat(response.documentType()).isEqualTo(ExpenseDocumentType.INVOICE);
        assertThat(response.originalFileName()).isEqualTo("facture-001.pdf");
        assertThat(response.fileSize()).isEqualTo(file.getSize());
        try (var files = Files.list(storageDirectory)) {
            assertThat(files).hasSize(1);
        }
        verify(documentRepository).save(any(ExpenseDocument.class));
    }

    @Test
    void rejectsUnsupportedFileType() {
        Expense expense = new Expense();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.exe",
                "application/octet-stream",
                new byte[] {1, 2, 3});
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> service.store(1L, ExpenseDocumentType.OTHER, file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only PDF, PNG and JPEG documents are allowed");

        verify(documentRepository, never()).save(any(ExpenseDocument.class));
    }
}
