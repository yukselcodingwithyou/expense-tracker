package com.expensetracker.service;

import com.expensetracker.domain.Attachment;
import com.expensetracker.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.file.upload.directory:./uploads}")
    private String uploadDirectory;

    @Value("${app.file.max-size:10485760}") // 10MB default
    private long maxFileSize;

    public FileUploadService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Attachment uploadFile(MultipartFile file, String ledgerEntryId) {
        validateFile(file);
        
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDirectory);
            Files.createDirectories(uploadPath);
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            
            // Save file to filesystem
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Create attachment record
            Attachment attachment = new Attachment();
            attachment.setLedgerEntryId(ledgerEntryId);
            attachment.setFilename(uniqueFilename);
            attachment.setOriginalFilename(originalFilename);
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setStorageKey(filePath.toString());
            
            return attachmentRepository.save(attachment);
            
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }

    public String getFileUrl(String attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        // For local file storage, return a relative URL
        // In a real implementation, this would generate a signed URL for MinIO
        return "/api/v1/files/" + attachmentId + "/download";
    }

    public byte[] getFileContent(String attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        try {
            Path filePath = Paths.get(attachment.getStorageKey());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + e.getMessage(), e);
        }
    }

    public List<Attachment> getAttachmentsByLedgerEntry(String ledgerEntryId) {
        return attachmentRepository.findByLedgerEntryId(ledgerEntryId);
    }

    public void deleteAttachment(String attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        
        try {
            // Delete file from filesystem
            Path filePath = Paths.get(attachment.getStorageKey());
            Files.deleteIfExists(filePath);
            
            // Delete attachment record
            attachmentRepository.deleteById(attachmentId);
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("File type not allowed");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return contentType.startsWith("image/") || 
               contentType.equals("application/pdf") ||
               contentType.startsWith("text/");
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}