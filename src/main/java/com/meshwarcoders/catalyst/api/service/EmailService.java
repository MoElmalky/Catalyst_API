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

    @Value("${MAIL_USERNAME}")
    private String fromEmail;

    @Value("${MAIL_SENDER_KEY}")
    private String mailSenderApiKey;

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
