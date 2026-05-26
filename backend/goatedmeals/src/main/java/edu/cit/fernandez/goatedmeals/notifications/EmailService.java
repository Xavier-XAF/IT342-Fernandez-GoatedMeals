package edu.cit.fernandez.goatedmeals.notifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public void sendOrderStatusEmail(String toEmail, String customerName, String mealName, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Goated Meals! - Delivery Update: " + newStatus);

        String body = "Hello " + customerName + ",\n\n"
                + "Great news! Your order for the " + mealName + " is now: " + newStatus + ".\n\n"
                + "Track your full delivery schedule on your dashboard.\n\n"
                + "Stay Goated,\nThe Kitchen Team";

        message.setText(body);

        // This executes the send command
        mailSender.send(message);
    }
}