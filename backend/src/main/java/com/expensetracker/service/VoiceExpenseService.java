package com.expensetracker.service;

import com.expensetracker.domain.ExpenseParseResult;
import com.expensetracker.domain.LedgerEntry;
import com.expensetracker.domain.VoiceExpenseData;
import com.expensetracker.dto.ledger.CreateLedgerEntryRequest;
import com.expensetracker.dto.ledger.LedgerEntryResponse;
import com.expensetracker.dto.voice.CreateExpenseFromVoiceRequest;
import com.expensetracker.dto.voice.VoiceExpenseRequest;
import com.expensetracker.repository.VoiceExpenseDataRepository;
import com.expensetracker.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoiceExpenseService {

    private final VoiceExpenseDataRepository voiceExpenseDataRepository;
    private final LedgerService ledgerService;
    private final CategoryService categoryService;

    public VoiceExpenseService(VoiceExpenseDataRepository voiceExpenseDataRepository,
                              LedgerService ledgerService,
                              CategoryService categoryService) {
        this.voiceExpenseDataRepository = voiceExpenseDataRepository;
        this.ledgerService = ledgerService;
        this.categoryService = categoryService;
    }

    public VoiceExpenseData processVoiceExpense(VoiceExpenseRequest request, String userId) {
        // Parse the spoken text
        ExpenseParseResult parseResult = parseVoiceExpense(request.getSpokenText(), request.getPreferredCurrency());
        
        // Create voice expense data
        VoiceExpenseData voiceData = new VoiceExpenseData();
        voiceData.setUserId(userId);
        voiceData.setOriginalText(request.getSpokenText());
        voiceData.setParseResult(parseResult);
        voiceData.setOverallConfidence(parseResult.getConfidence());
        
        // Generate suggestions for improvement
        List<String> suggestions = generateSuggestions(parseResult);
        voiceData.setSuggestions(suggestions);
        
        // Calculate field confidences
        Map<String, Double> fieldConfidences = calculateFieldConfidences(request.getSpokenText(), parseResult);
        voiceData.setFieldConfidences(fieldConfidences);
        
        return voiceExpenseDataRepository.save(voiceData);
    }

    private ExpenseParseResult parseVoiceExpense(String spokenText, String preferredCurrency) {
        ExpenseParseResult result = new ExpenseParseResult();
        
        // Extract amount
        Long amountMinor = extractAmountMinor(spokenText);
        result.setAmountMinor(amountMinor);
        
        // Extract or default currency
        String currency = extractCurrency(spokenText, preferredCurrency);
        result.setCurrency(currency);
        
        // Extract description
        String description = extractDescription(spokenText);
        result.setDescription(description);
        
        // Extract merchant
        String merchant = extractMerchant(spokenText);
        result.setMerchant(merchant);
        
        // Suggest category
        String categoryId = suggestCategory(description, merchant);
        result.setCategoryId(categoryId);
        
        // Set occurrence time (now for simplicity, could extract from text)
        result.setOccurredAt(Instant.now());
        
        // Calculate confidence based on how much we could extract
        double confidence = calculateConfidence(spokenText, result);
        result.setConfidence(confidence);
        
        return result;
    }

    private Long extractAmountMinor(String spokenText) {
        // Patterns to match various amount formats
        Pattern[] patterns = {
            Pattern.compile("(\\d+\\.\\d{2})\\s*(?:dollars?|usd|bucks?)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d+)\\s*(?:dollars?|usd|bucks?)\\s*(?:and\\s*)?(\\d+)\\s*cents?", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\$\\s*(\\d+\\.\\d{2})"),
            Pattern.compile("(\\d+\\.\\d{2})"),
            Pattern.compile("(\\d+)\\s*(?:dollars?|usd)", Pattern.CASE_INSENSITIVE)
        };
        
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(spokenText);
            if (matcher.find()) {
                try {
                    if (pattern.pattern().contains("cents")) {
                        // Handle "X dollars and Y cents" format
                        long dollars = Long.parseLong(matcher.group(1));
                        long cents = matcher.groupCount() > 1 ? Long.parseLong(matcher.group(2)) : 0;
                        return dollars * 100 + cents;
                    } else {
                        // Handle decimal format
                        double amount = Double.parseDouble(matcher.group(1));
                        return Math.round(amount * 100);
                    }
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }
        
        return 0L; // Default if no amount found
    }

    private String extractCurrency(String spokenText, String preferredCurrency) {
        String lowerText = spokenText.toLowerCase();
        
        if (lowerText.contains("dollar") || lowerText.contains("usd") || lowerText.contains("$")) {
            return "USD";
        } else if (lowerText.contains("euro") || lowerText.contains("eur")) {
            return "EUR";
        } else if (lowerText.contains("pound") || lowerText.contains("gbp")) {
            return "GBP";
        } else if (lowerText.contains("lira") || lowerText.contains("try")) {
            return "TRY";
        }
        
        return preferredCurrency; // Default to user's preference
    }

    private String extractDescription(String spokenText) {
        // Remove amount-related parts and extract the main expense description
        String cleaned = spokenText.toLowerCase();
        
        // Remove common phrases
        cleaned = cleaned.replaceAll("i spent|paid|cost me|bought|purchase", "");
        cleaned = cleaned.replaceAll("\\d+\\.?\\d*\\s*(?:dollars?|usd|euros?|pounds?|lira|bucks?)", "");
        cleaned = cleaned.replaceAll("\\$\\d+\\.?\\d*", "");
        cleaned = cleaned.replaceAll("\\bat\\b.*", ""); // Remove "at [store]" part
        cleaned = cleaned.replaceAll("\\bon\\b", ""); // Remove "on"
        
        // Common expense categories/items
        if (cleaned.contains("coffee")) return "Coffee";
        if (cleaned.contains("lunch")) return "Lunch";
        if (cleaned.contains("dinner")) return "Dinner";
        if (cleaned.contains("breakfast")) return "Breakfast";
        if (cleaned.contains("groceries") || cleaned.contains("grocery")) return "Groceries";
        if (cleaned.contains("gas") || cleaned.contains("fuel")) return "Gas";
        if (cleaned.contains("parking")) return "Parking";
        if (cleaned.contains("taxi") || cleaned.contains("uber") || cleaned.contains("ride")) return "Transportation";
        
        // Return cleaned up text or default
        cleaned = cleaned.trim().replaceAll("\\s+", " ");
        return cleaned.isEmpty() ? "Expense" : cleaned;
    }

    private String extractMerchant(String spokenText) {
        // Look for "at [merchant]" pattern
        Pattern merchantPattern = Pattern.compile("\\bat\\s+([a-zA-Z][a-zA-Z\\s&'.-]+?)(?:\\s|$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = merchantPattern.matcher(spokenText);
        
        if (matcher.find()) {
            String merchant = matcher.group(1).trim();
            // Clean up common trailing words
            merchant = merchant.replaceAll("\\s+(today|yesterday|this morning|this afternoon)$", "");
            return merchant;
        }
        
        return null;
    }

    private String suggestCategory(String description, String merchant) {
        String text = (description + " " + (merchant != null ? merchant : "")).toLowerCase();
        
        if (text.contains("coffee") || text.contains("starbucks") || text.contains("cafe")) {
            return "food-dining";
        } else if (text.contains("lunch") || text.contains("dinner") || text.contains("breakfast") || 
                   text.contains("restaurant") || text.contains("mcdonald")) {
            return "food-dining";
        } else if (text.contains("grocery") || text.contains("market") || text.contains("walmart") || 
                   text.contains("target")) {
            return "groceries";
        } else if (text.contains("gas") || text.contains("fuel") || text.contains("shell") || 
                   text.contains("exxon") || text.contains("bp")) {
            return "transportation";
        } else if (text.contains("taxi") || text.contains("uber") || text.contains("lyft") || 
                   text.contains("bus") || text.contains("train")) {
            return "transportation";
        } else if (text.contains("parking")) {
            return "transportation";
        }
        
        return "other";
    }

    private double calculateConfidence(String spokenText, ExpenseParseResult result) {
        double confidence = 0.0;
        
        // Amount confidence (most important)
        if (result.getAmountMinor() != null && result.getAmountMinor() > 0) {
            confidence += 0.4;
        }
        
        // Description confidence
        if (result.getDescription() != null && !result.getDescription().equals("Expense")) {
            confidence += 0.3;
        }
        
        // Category confidence
        if (result.getCategoryId() != null && !result.getCategoryId().equals("other")) {
            confidence += 0.2;
        }
        
        // Merchant confidence
        if (result.getMerchant() != null) {
            confidence += 0.1;
        }
        
        return confidence;
    }

    private List<String> generateSuggestions(ExpenseParseResult result) {
        List<String> suggestions = new ArrayList<>();
        
        if (result.getAmountMinor() == null || result.getAmountMinor() <= 0) {
            suggestions.add("Try including a clear amount like '$25.50' or '25 dollars'");
        }
        
        if (result.getDescription() == null || result.getDescription().equals("Expense")) {
            suggestions.add("Include what you bought, like 'coffee' or 'lunch'");
        }
        
        if (result.getMerchant() == null) {
            suggestions.add("Try including where you spent it, like 'at Starbucks'");
        }
        
        return suggestions;
    }

    private Map<String, Double> calculateFieldConfidences(String spokenText, ExpenseParseResult result) {
        Map<String, Double> confidences = new HashMap<>();
        
        // Amount confidence
        if (spokenText.toLowerCase().matches(".*\\d+\\.\\d{2}.*") || 
            spokenText.toLowerCase().matches(".*\\d+\\s*dollars?.*")) {
            confidences.put("amount", 0.9);
        } else {
            confidences.put("amount", 0.5);
        }
        
        // Description confidence
        confidences.put("description", result.getDescription().equals("Expense") ? 0.3 : 0.8);
        
        // Merchant confidence
        confidences.put("merchant", result.getMerchant() != null ? 0.8 : 0.0);
        
        // Category confidence
        confidences.put("category", result.getCategoryId().equals("other") ? 0.3 : 0.7);
        
        return confidences;
    }

    public LedgerEntryResponse createExpenseFromVoice(CreateExpenseFromVoiceRequest request, String userId) {
        // Create a CreateLedgerEntryRequest from voice data
        CreateLedgerEntryRequest ledgerRequest = new CreateLedgerEntryRequest();
        ledgerRequest.setMemberId(userId);
        ledgerRequest.setType(LedgerEntry.TransactionType.EXPENSE);
        ledgerRequest.setAmountMinor(request.getAmountMinor());
        ledgerRequest.setCurrency(request.getCurrency());
        ledgerRequest.setCategoryId(request.getCategoryId());
        ledgerRequest.setNotes(request.getDescription());
        ledgerRequest.setOccurredAt(Instant.now());
        
        // Create UserPrincipal for the service call
        UserPrincipal userPrincipal = new UserPrincipal(userId, "");
        
        return ledgerService.createEntry(userPrincipal, ledgerRequest);
    }

    public List<VoiceExpenseData> getUserVoiceExpenseData(String userId) {
        return voiceExpenseDataRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public VoiceExpenseData getVoiceExpenseData(String voiceExpenseDataId, String userId) {
        VoiceExpenseData data = voiceExpenseDataRepository.findById(voiceExpenseDataId)
                .orElseThrow(() -> new RuntimeException("Voice expense data not found"));
        
        if (!data.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }
        
        return data;
    }
}