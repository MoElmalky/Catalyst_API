package com.meshwarcoders.catalyst.util;

public class EmailTemplates {
    public static String emailTemplate(String url, String username, String message, String button, String duration) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Action Required: Catalyst</title>
                    <style>
                        /* Reset & Base */
                        body {
                            margin: 0;
                            padding: 0;
                            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                            background-color: #f5f5f5;
                            color: #333333;
                        }
                        table {
                            border-spacing: 0;
                            border-collapse: collapse;
                            width: 100%%;
                        }
                        img {
                            border: 0;
                        }
                        /* Variables */
                        .bg-navy { background-color: #192A56; }
                        .bg-pink { background-color: #FC5185; }
                        .text-navy { color: #393E46; }
                        .text-white { color: #eeeeee; }
                        /* Layout */
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: #eeeeee;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                        }
                        .content {
                            padding: 40px 30px;
                            text-align: center;
                        }
                        .button-container {
                            margin: 30px 0;
                        }
                        .btn {
                            display: inline-block;
                            background-color: #192A56;
                            color: #ffffff;
                            text-decoration: none;
                            padding: 14px 28px;
                            border-radius: 50px;
                            font-weight: bold;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                        }
                        .footer {
                            background-color: #f5f5f5;
                            padding: 20px;
                            text-align: center;
                            font-size: 12px;
                            color: #999999;
                        }
                    </style>
                </head>
                <body>
                    <div style="padding: 20px;">
                        <div class="container">
                            <!-- Decor Header -->
                            <table role="presentation">
                                <tr><td class="bg-navy" style="height: 12px;"></td></tr>
                                <tr><td class="bg-pink" style="height: 8px;"></td></tr>
                            </table>
                            <!-- Main Content -->
                            <div class="content">
                                <h1 class="text-navy" style="margin-bottom: 20px;">Hello %s!</h1>
                                <p style="font-size: 16px; line-height: 1.5; color: #555;">
                                    %s
                                </p>
                                <p style="font-size: 16px; line-height: 1.5; color: #555;">This link is valid for %s.
                                </p>
                                <div class="button-container">
                                    <a href="%s" class="btn" style="color:#ffffff; text-decoration:none;">
                                        %s
                                    </a>
                                </div>
                                <p style="font-size: 14px; color: #888; margin-top: 30px;">
                                    If you didn't request this, you can safely ignore this email.
                                </p>
                            </div>
                            <!-- Decor Footer -->
                            <table role="presentation">
                                <tr><td class="bg-navy" style="height: 12px;"></td></tr>
                            </table>
                            <!-- Footer Info -->
                            <div class="footer">
                                © 2026 Catalyst. All rights reserved.
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(username, message, duration,  url, button);
    }
}
