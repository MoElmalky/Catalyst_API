package com.meshwarcoders.catalyst.api.service;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import com.sendgrid.*;

@Service
public class EmailService {

  private String fromEmail = "mostafazaki026@gmail.com";

  @Value("${MAIL_SENDER_KEY}")
  private String mailSenderApiKey;

  public void sendEmail(String to, String code) throws IOException {
    String htmlContent = """
        <!DOCTYPE html>
        <html>
          <head>
            <meta charset="UTF-8">
            <title>Password Reset Code</title>
            <style>
              body {
                background-color: #f4f6f8;
                font-family: Arial, sans-serif;
                color: #333;
                padding: 20px;
              }
              .container {
                background-color: #eeeeee;
                border-radius: 8px;
                padding: 25px;
                max-width: 500px;
                margin: 0 auto;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
              }
              h2 {
                color: #00ADB5;
              }
              .code {
                font-size: 24px;
                font-weight: bold;
                color: #00ADB5;
                margin: 20px 0;
              }
              .footer {
                margin-top: 30px;
                font-size: 13px;
                color: #777;
              }
            </style>
          </head>
          <body>
            <div class="container">
              <h2>Password Reset Request</h2>
              <p>Hello,</p>
              <p>We received a request to reset your password. Please use the verification code below to proceed:</p>

              <p class="code">%s</p>

              <p>This code will expire in <strong>5 minutes</strong>.</p>
              <p>If you didn’t request a password reset, please ignore this email.</p>

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
    } catch (IOException ex) {
      throw ex;
    }
  }
}