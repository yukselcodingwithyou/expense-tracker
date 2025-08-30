package com.expensetracker.service;

import com.expensetracker.domain.Notification;
import com.expensetracker.domain.User;
import com.expensetracker.domain.Budget;
import com.expensetracker.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final BudgetService budgetService;

    public NotificationService(NotificationRepository notificationRepository, 
                             EmailService emailService,
                             UserService userService,
                             BudgetService budgetService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.userService = userService;
        this.budgetService = budgetService;
    }

    public void createBudgetAlert(String userId, String budgetId, double usagePercentage) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFamilyId(familyId);
        notification.setType(Notification.NotificationType.BUDGET_ALERT);
        notification.setTitle("Bütçe Uyarısı");
        notification.setMessage("Bütçenizin %" + (int)usagePercentage + "'ını kullandınız");
        
        Map<String, Object> data = new HashMap<>();
        data.put("budgetId", budgetId);
        data.put("usagePercentage", usagePercentage);
        notification.setData(data);
        
        notificationRepository.save(notification);
        
        // Send email
        try {
            User user = userService.findById(userId);
            Budget budget = budgetService.getBudgetByFamilyAndId(familyId, budgetId);
            emailService.sendBudgetAlert(user, budget, usagePercentage);
            notification.setEmailSent(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            // Log error but don't fail the notification creation
            System.err.println("Failed to send budget alert email: " + e.getMessage());
        }
    }

    public void createBudgetExceededAlert(String userId, String budgetId, double usagePercentage) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFamilyId(familyId);
        notification.setType(Notification.NotificationType.BUDGET_EXCEEDED);
        notification.setTitle("Bütçe Aşıldı!");
        notification.setMessage("Bütçenizi %" + (int)usagePercentage + " oranında aştınız");
        
        Map<String, Object> data = new HashMap<>();
        data.put("budgetId", budgetId);
        data.put("usagePercentage", usagePercentage);
        notification.setData(data);
        
        notificationRepository.save(notification);
    }

    public void createWeeklySummary(String userId, String summaryContent) {
        String familyId = userService.getCurrentUserFamilyId(userId);
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setFamilyId(familyId);
        notification.setType(Notification.NotificationType.WEEKLY_SUMMARY);
        notification.setTitle("Haftalık Özet");
        notification.setMessage("Bu haftaki harcama özetiniz hazır");
        
        notificationRepository.save(notification);
        
        // Send email
        try {
            User user = userService.findById(userId);
            emailService.sendWeeklySummary(user, summaryContent);
            notification.setEmailSent(true);
            notificationRepository.save(notification);
        } catch (Exception e) {
            System.err.println("Failed to send weekly summary email: " + e.getMessage());
        }
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }
}