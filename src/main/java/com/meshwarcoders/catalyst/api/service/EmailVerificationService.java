package com.meshwarcoders.catalyst.api.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

@Service
public class EmailVerificationService {

    // Using AbstractAPI free email verification
    private static final String API_KEY = "YOUR_ABSTRACT_API_KEY"; // Get free key from https://www.abstractapi.com/
    private static final String API_URL = "https://emailvalidation.abstractapi.com/v1/";

    public boolean isEmailValid(String email) {
        try {
            // Basic email format validation first
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return false;
            }

            // Check for disposable/temporary email domains
            if (isDisposableEmail(email)) {
                return false;
            }

            // For production, you should use a real email verification API
            // This is a simple implementation
            return verifyEmailWithAPI(email);
            
        } catch (Exception e) {
            System.err.println("Error verifying email: " + e.getMessage());
            return false;
        }
    }

    private boolean isDisposableEmail(String email) {
        String[] disposableDomains = {
            "tempmail.com", "throwaway.email", "guerrillamail.com", 
            "10minutemail.com", "mailinator.com", "trashmail.com",
            "yopmail.com", "temp-mail.org", "fakeinbox.com"
        };
        
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        
        for (String disposable : disposableDomains) {
            if (domain.equals(disposable)) {
                return true;
            }
        }
        return false;
    }

    private boolean verifyEmailWithAPI(String email) {
        try {
            // For testing purposes, return true for valid format emails
            // In production, implement actual API call
            
            // Example API implementation (uncomment and add your API key):
            /*
            String urlString = API_URL + "?api_key=" + API_KEY + "&email=" + email;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            JSONObject jsonResponse = new JSONObject(response.toString());
            
            return jsonResponse.getBoolean("is_valid_format") 
                && jsonResponse.getBoolean("is_smtp_valid")
                && !jsonResponse.getBoolean("is_disposable_email");
            */
            
            // Temporary implementation - validate format only
            return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
            
        } catch (Exception e) {
            System.err.println("API verification error: " + e.getMessage());
            return false;
        }
    }

    public String getEmailDomain(String email) {
        if (email == null || !email.contains("@")) {
            return null;
        }
        return email.substring(email.indexOf("@") + 1);
    }

    public boolean isBusinessEmail(String email) {
        String[] freeEmailProviders = {
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com",
            "live.com", "aol.com", "icloud.com", "mail.com"
        };
        
        String domain = getEmailDomain(email);
        if (domain == null) {
            return false;
        }
        
        for (String provider : freeEmailProviders) {
            if (domain.equalsIgnoreCase(provider)) {
                return false;
            }
        }
        return true;
    }
}