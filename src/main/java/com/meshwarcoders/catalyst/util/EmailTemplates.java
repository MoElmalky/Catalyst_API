package com.meshwarcoders.catalyst.util;

public class EmailTemplates {
    public static String passwordResetEmail(String code) {
        return """
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
                                
                      <div class="footer">
                        <p>Best regards,<br>Catalyst Team</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(code);
    }

    public static String emailConfirmationEmail(String confirmationLink) {
        return """
                <!DOCTYPE html>
                <html>
                  <head>
                    <meta charset=\"UTF-8\">
                    <title>Email Confirmation</title>
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
                      .btn {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #00ADB5;
                        color: #ffffff !important;
                        text-decoration: none;
                        border-radius: 4px;
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
                    <div class=\"container\">
                      <h2>Welcome to Catalyst!</h2>
                      <p>Hello,</p>
                      <p>Thank you for signing up. Please confirm your email address by clicking the button below:</p>

                      <p><a class=\"btn\" href=\"%s\">Confirm Email</a></p>

                      <p>This link will expire in <strong>24 hours</strong>.</p>
                      <p>If you didnt create an account, you can safely ignore this email.</p>

                      <div class=\"footer\">
                        <p>Best regards,<br>Catalyst Team</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(confirmationLink);
    }
}
