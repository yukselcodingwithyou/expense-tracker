package com.expensetracker.service;

import com.expensetracker.domain.Attachment;
import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.domain.ReceiptData;
import com.expensetracker.domain.ReceiptItem;
import com.expensetracker.dto.ledger.CreateLedgerEntryRequest;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.dto.receipt.CreateExpenseFromReceiptRequest;
import com.expensetracker.repository.ReceiptDataRepository;
import com.expensetracker.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReceiptOCRService {

    private final FileUploadService fileUploadService;
    private final LedgerService ledgerService;
    private final CategoryService categoryService;
    private final ReceiptDataRepository receiptDataRepository;
    private final ITesseract tesseract;

    @Value("${app.ai.mock:true}")
    private boolean mockAIResponse;

    public ReceiptOCRService(FileUploadService fileUploadService, 
                           LedgerService ledgerService,
                           CategoryService categoryService,
                           ReceiptDataRepository receiptDataRepository) {
        this.fileUploadService = fileUploadService;
        this.ledgerService = ledgerService;
        this.categoryService = categoryService;
        this.receiptDataRepository = receiptDataRepository;
        
        // Initialize Tesseract
        this.tesseract = new Tesseract();
        // For production, set the data path to where tessdata is located
        // tesseract.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata/");
    }

    public ReceiptData processReceipt(MultipartFile receiptImage, String userId) {
        try {
            // 1. Upload file
            Attachment attachment = fileUploadService.uploadFile(receiptImage, "temp");
            
            // 2. Perform OCR
            String ocrText = performOCR(attachment);
            
            // 3. Parse with basic parsing (mock AI for now)
            ReceiptData receiptData = parseReceiptWithBasicLogic(ocrText);
            
            // 4. Set metadata
            receiptData.setUserId(userId);
            receiptData.setAttachmentId(attachment.getId());
            
            // 5. Enhance receipt data
            enhanceReceiptData(receiptData, userId);
            
            // 6. Save to repository
            return receiptDataRepository.save(receiptData);
            
        } catch (Exception e) {
            throw new RuntimeException("Receipt processing failed: " + e.getMessage(), e);
        }
    }

    private String performOCR(Attachment attachment) throws TesseractException, IOException {
        // Read the image file
        Path filePath = Paths.get(attachment.getStorageKey());
        BufferedImage image = ImageIO.read(filePath.toFile());
        
        // Perform OCR
        return tesseract.doOCR(image);
    }

    private ReceiptData parseReceiptWithBasicLogic(String ocrText) {
        ReceiptData receiptData = new ReceiptData();
        
        // Basic parsing logic using regex patterns
        receiptData.setStoreName(extractStoreName(ocrText));
        receiptData.setTotalAmount(extractTotalAmount(ocrText));
        receiptData.setDate(extractDate(ocrText));
        receiptData.setItems(extractItems(ocrText));
        receiptData.setConfidence(0.8); // Mock confidence score
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ocrText", ocrText);
        metadata.put("parseMethod", "basic");
        receiptData.setMetadata(metadata);
        
        return receiptData;
    }

    private String extractStoreName(String ocrText) {
        // Look for common store patterns in the first few lines
        String[] lines = ocrText.split("\\n");
        for (int i = 0; i < Math.min(3, lines.length); i++) {
            String line = lines[i].trim();
            if (line.length() > 3 && !line.matches(".*\\d.*")) {
                return line;
            }
        }
        return "Unknown Store";
    }

    private BigDecimal extractTotalAmount(String ocrText) {
        // Look for total amount patterns
        Pattern totalPattern = Pattern.compile("(?i)total[\\s:]*\\$?([0-9]+\\.?[0-9]*)");
        Matcher matcher = totalPattern.matcher(ocrText);
        
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                // Fall back to first decimal number found
            }
        }
        
        // Fallback: look for any decimal number
        Pattern amountPattern = Pattern.compile("\\$?([0-9]+\\.[0-9]{2})");
        matcher = amountPattern.matcher(ocrText);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        
        return BigDecimal.ZERO;
    }

    private LocalDateTime extractDate(String ocrText) {
        // For now, return current date
        // In a real implementation, this would parse date patterns from OCR text
        return LocalDateTime.now();
    }

    private List<ReceiptItem> extractItems(String ocrText) {
        List<ReceiptItem> items = new ArrayList<>();
        
        // Basic item extraction - look for lines with prices
        String[] lines = ocrText.split("\\n");
        Pattern itemPattern = Pattern.compile("(.+)\\s+\\$?([0-9]+\\.?[0-9]*)");
        
        for (String line : lines) {
            Matcher matcher = itemPattern.matcher(line.trim());
            if (matcher.find()) {
                String itemName = matcher.group(1).trim();
                String priceStr = matcher.group(2);
                
                try {
                    BigDecimal price = new BigDecimal(priceStr);
                    if (price.compareTo(BigDecimal.ZERO) > 0) {
                        ReceiptItem item = new ReceiptItem();
                        item.setName(itemName);
                        item.setPrice(price);
                        item.setQuantity(1);
                        items.add(item);
                    }
                } catch (NumberFormatException ignored) {
                    // Skip invalid prices
                }
            }
        }
        
        return items;
    }

    private void enhanceReceiptData(ReceiptData data, String userId) {
        // Suggest category based on store name
        String suggestedCategory = suggestCategoryByStoreName(data.getStoreName());
        data.setSuggestedCategory(suggestedCategory);
        
        // Validate amount format
        if (data.getTotalAmount() != null) {
            data.setTotalAmount(data.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
        }
    }

    private String suggestCategoryByStoreName(String storeName) {
        if (storeName == null) return null;
        
        String lowerStoreName = storeName.toLowerCase();
        
        // Basic category suggestions based on common store names
        if (lowerStoreName.contains("starbucks") || lowerStoreName.contains("coffee") || 
            lowerStoreName.contains("cafe")) {
            return "food-dining";
        } else if (lowerStoreName.contains("grocery") || lowerStoreName.contains("market") ||
                   lowerStoreName.contains("walmart") || lowerStoreName.contains("target")) {
            return "groceries";
        } else if (lowerStoreName.contains("gas") || lowerStoreName.contains("shell") ||
                   lowerStoreName.contains("exxon") || lowerStoreName.contains("bp")) {
            return "transportation";
        } else if (lowerStoreName.contains("pharmacy") || lowerStoreName.contains("cvs") ||
                   lowerStoreName.contains("walgreens")) {
            return "healthcare";
        }
        
        return "other";
    }

    public LedgerEntryResponse createExpenseFromReceipt(CreateExpenseFromReceiptRequest request, String userId) {
        // Create a CreateLedgerEntryRequest from receipt data
        CreateLedgerEntryRequest ledgerRequest = new CreateLedgerEntryRequest();
        ledgerRequest.setMemberId(userId);
        ledgerRequest.setType(LedgerEntry.TransactionType.EXPENSE);
        ledgerRequest.setAmountMinor(request.getAmount().multiply(new BigDecimal("100")).longValue());
        ledgerRequest.setCurrency("USD");
        ledgerRequest.setCategoryId(request.getCategoryId());
        ledgerRequest.setNotes(request.getDescription() != null ? request.getDescription() : 
                           "Receipt from " + request.getStoreName());
        ledgerRequest.setOccurredAt(request.getDate() != null ? 
                          request.getDate().atZone(java.time.ZoneId.systemDefault()).toInstant() :
                          java.time.Instant.now());
        
        // Create UserPrincipal for the service call
        UserPrincipal userPrincipal = new UserPrincipal(userId, "");
        
        return ledgerService.createEntry(userPrincipal, ledgerRequest);
    }

    public List<ReceiptData> getUserReceiptData(String userId) {
        return receiptDataRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public ReceiptData getReceiptData(String receiptDataId, String userId) {
        ReceiptData data = receiptDataRepository.findById(receiptDataId)
                .orElseThrow(() -> new RuntimeException("Receipt data not found"));
        
        if (!data.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return data;
    }
}