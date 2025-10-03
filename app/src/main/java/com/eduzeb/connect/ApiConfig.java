
package com.eduzeb.connect;

public class ApiConfig {
    
    // OpenAI ChatGPT API Configuration
    public static final String OPENAI_API_KEY = "YOUR_OPENAI_API_KEY_HERE";
    public static final String OPENAI_BASE_URL = "https://api.openai.com/v1/";
    public static final String OPENAI_CHAT_ENDPOINT = "chat/completions";
    
    // Google Places API (for location)
    public static final String GOOGLE_PLACES_API_KEY = "YOUR_GOOGLE_API_KEY_HERE";
    
    // South African Districts (can be expanded)
    public static final String[] SA_DISTRICTS = {
        "Tshwane Metropolitan", "Johannesburg Metropolitan", "Ekurhuleni Metropolitan",
        "Cape Town Metropolitan", "eThekwini Metropolitan", "Nelson Mandela Bay",
        "Mangaung Metropolitan", "Buffalo City Metropolitan", "Sedibeng"
    };
    
    // API Request Headers
    public static final String CONTENT_TYPE = "application/json";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";
}
