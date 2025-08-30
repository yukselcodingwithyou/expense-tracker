package com.expensetracker.controller;

import com.expensetracker.domain.Attachment;
import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.FileUploadService;
import com.expensetracker.service.LedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Upload", description = "File upload and management APIs")
public class FileController {

    private final FileUploadService fileUploadService;
    private final LedgerService ledgerService;

    public FileController(FileUploadService fileUploadService, LedgerService ledgerService) {
        this.fileUploadService = fileUploadService;
        this.ledgerService = ledgerService;
    }

    @PostMapping("/upload/{ledgerEntryId}")
    @Operation(summary = "Upload file", description = "Upload a file attachment for a ledger entry")
    public ResponseEntity<Attachment> uploadFile(
            @PathVariable String ledgerEntryId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user) {
        
        // Validate that user has access to this ledger entry
        LedgerEntry entry = ledgerService.getEntryById(user.getId(), ledgerEntryId);
        
        Attachment attachment = fileUploadService.uploadFile(file, ledgerEntryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }

    @GetMapping("/{attachmentId}/url")
    @Operation(summary = "Get file URL", description = "Get a URL to access the file")
    public ResponseEntity<Map<String, String>> getFileUrl(
            @PathVariable String attachmentId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        String url = fileUploadService.getFileUrl(attachmentId);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/{attachmentId}/download")
    @Operation(summary = "Download file", description = "Download the file content")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String attachmentId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        byte[] fileContent = fileUploadService.getFileContent(attachmentId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @GetMapping("/ledger/{ledgerEntryId}")
    @Operation(summary = "Get attachments", description = "Get all attachments for a ledger entry")
    public ResponseEntity<List<Attachment>> getAttachments(
            @PathVariable String ledgerEntryId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        // Validate that user has access to this ledger entry
        LedgerEntry entry = ledgerService.getEntryById(user.getId(), ledgerEntryId);
        
        List<Attachment> attachments = fileUploadService.getAttachmentsByLedgerEntry(ledgerEntryId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{attachmentId}")
    @Operation(summary = "Delete attachment", description = "Delete a file attachment")
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable String attachmentId,
            @AuthenticationPrincipal UserPrincipal user) {
        
        fileUploadService.deleteAttachment(attachmentId);
        return ResponseEntity.ok().build();
    }
}