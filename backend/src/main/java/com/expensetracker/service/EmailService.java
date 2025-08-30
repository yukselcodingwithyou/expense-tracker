package com.expensetracker.service;

import com.expensetracker.domain.User;
import com.expensetracker.domain.Budget;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Value("${app.email.from:noreply@expensetracker.com}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    public void sendBudgetAlert(User user, Budget budget, double usagePercentage) {
        if (!emailEnabled) {
            return;
        }

        String subject = "Bütçe Uyarısı - " + budget.getName();
        String message = String.format(
            "Merhaba %s,\n\n" +
            "%s bütçenizin %%%d'ını kullandınız.\n" +
            "Detaylar için uygulamayı kontrol edin.\n\n" +
            "İyi günler!",
            user.getEmail(), budget.getName(), (int)usagePercentage
        );

        // For now, just log the email content
        // In a real implementation, this would use JavaMailSender
        System.out.println("EMAIL TO: " + user.getEmail());
        System.out.println("SUBJECT: " + subject);
        System.out.println("MESSAGE: " + message);
    }

    public void sendWeeklySummary(User user, String summaryContent) {
        if (!emailEnabled) {
            return;
        }

        String subject = "Haftalık Harcama Özeti";
        String message = String.format(
            "Merhaba %s,\n\n" +
            "Bu haftaki harcama özetiniz:\n\n" +
            "%s\n\n" +
            "Detaylı rapor için uygulamayı ziyaret edin.\n\n" +
            "İyi günler!",
            user.getEmail(), summaryContent
        );

        System.out.println("EMAIL TO: " + user.getEmail());
        System.out.println("SUBJECT: " + subject);
        System.out.println("MESSAGE: " + message);
    }

    public void sendMonthlyReport(User user, String reportContent) {
        if (!emailEnabled) {
            return;
        }

        String subject = "Aylık Finansal Rapor";
        String message = String.format(
            "Merhaba %s,\n\n" +
            "Bu ay finansal durumunuz:\n\n" +
            "%s\n\n" +
            "Detaylı analiz için uygulamayı kullanın.\n\n" +
            "İyi günler!",
            user.getEmail(), reportContent
        );

        System.out.println("EMAIL TO: " + user.getEmail());
        System.out.println("SUBJECT: " + subject);
        System.out.println("MESSAGE: " + message);
    }
}