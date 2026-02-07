package com.meshwarcoders.catalyst.api.service;


import com.meshwarcoders.catalyst.api.model.common.AuthUser;
import com.meshwarcoders.catalyst.api.model.common.UserType;
import com.meshwarcoders.catalyst.util.EmailTemplates;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.sendgrid.*;

@Service
public class EmailService {

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    @Value("${MAIL_SENDER_KEY}")
    private String mailSenderApiKey;

    @Value("${app.email-frontend.base-url}")
    private String emailFrontendBaseUrl;

    public void sendRestPasswordEmail(AuthUser user, UserType userType, String token) {
        String url = String.format("%s/reset-password?userType=%s&token=%s", emailFrontendBaseUrl, userType, token);
        String message = "We received a request to reset your password. Click the button below to choose a new password.";
        String button = "Reset Password";
        String duration = "15 minutes";
        String htmlContent = EmailTemplates.emailTemplate(url, user.getUsername(), message, button, duration);
        String subject = "Catalyst: Reset Password";

        Content content = new Content("text/html", htmlContent);

        try {
            sendEmail(user.getEmail(), subject, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendEmailConfirmation(AuthUser user, UserType userType, String token) {

        String url = String.format("%s/confirm-email?userType=%s&token=%s", emailFrontendBaseUrl, userType, token);
        String message = "Thank you for signing up! Please confirm your email by clicking the button below.";
        String button = "Confirm Email";
        String duration = "2 hours";
        String htmlContent = EmailTemplates.emailTemplate(url, user.getUsername(), message, button, duration);
        String subject = "Catalyst: Confirm Email";

        Content content = new Content("text/html", htmlContent);

        try {
            sendEmail(user.getEmail(), subject, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendEmail(String to, String subject, Content content) throws IOException {
        //String subject = "Catalyst Password Reset Code";
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        //Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(mailSenderApiKey);
        Request request = new Request();

      try {
          request.setMethod(Method.POST);
          request.setEndpoint("mail/send");
          request.setBody(mail.build());
          Response response = sg.api(request);
          System.out.println("Email status code: " + response.getStatusCode());
          System.out.println("Email response body: " + response.getBody());
      } catch (IOException ex){
          throw ex;
      }
    }
}
