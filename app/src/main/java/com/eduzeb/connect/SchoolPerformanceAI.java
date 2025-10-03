
package com.eduzeb.connect;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SchoolPerformanceAI {
    
    private Context context;
    private static final String TAG = "SchoolPerformanceAI";
    
    public interface PerformanceCallback {
        void onPerformanceDataReceived(SchoolPerformance performance);
        void onError(String error);
    }
    
    public SchoolPerformanceAI(Context context) {
        this.context = context;
    }
    
    public void analyzeSchoolPerformance(String schoolName, String district, PerformanceCallback callback) {
        new FetchPerformanceTask(callback).execute(schoolName, district);
    }
    
    private class FetchPerformanceTask extends AsyncTask<String, Void, SchoolPerformance> {
        
        private PerformanceCallback callback;
        private String errorMessage;
        
        public FetchPerformanceTask(PerformanceCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected SchoolPerformance doInBackground(String... params) {
            String schoolName = params[0];
            String district = params[1];
            
            try {
                return fetchFromChatGPT(schoolName, district);
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "Error fetching performance data", e);
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(SchoolPerformance result) {
            if (result != null && callback != null) {
                callback.onPerformanceDataReceived(result);
            } else if (callback != null) {
                callback.onError(errorMessage != null ? errorMessage : "Failed to fetch data");
            }
        }
        
        private SchoolPerformance fetchFromChatGPT(String schoolName, String district) throws Exception {
            URL url = new URL(ApiConfig.OPENAI_BASE_URL + ApiConfig.OPENAI_CHAT_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", ApiConfig.CONTENT_TYPE);
            connection.setRequestProperty("Authorization", 
                    ApiConfig.AUTHORIZATION_PREFIX + ApiConfig.OPENAI_API_KEY);
            connection.setDoOutput(true);
            
            // Create ChatGPT prompt
            String prompt = createPerformancePrompt(schoolName, district);
            
            // Create JSON request
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-3.5-turbo");
            
            JSONArray messages = new JSONArray();
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);
            messages.put(message);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);
            
            // Send request
            OutputStream os = connection.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();
            os.close();
            
            // Read response
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                return parseResponse(response.toString(), schoolName);
            } else {
                throw new Exception("HTTP Error: " + responseCode);
            }
        }
        
        private String createPerformancePrompt(String schoolName, String district) {
            return "Analyze the academic performance of " + schoolName + " in " + district + 
                   ", South Africa. Provide the following information in JSON format:\n" +
                   "1. Overall performance rating (0-100)\n" +
                   "2. Pass rate percentage\n" +
                   "3. Bachelor pass rate\n" +
                   "4. Average subject performance\n" +
                   "5. School ranking in district\n" +
                   "6. Key strengths (max 3)\n" +
                   "7. Areas for improvement (max 2)\n" +
                   "8. Recent achievements\n\n" +
                   "Format: {\"rating\": 85, \"passRate\": 92, \"bachelorPass\": 78, " +
                   "\"subjectPerformance\": {...}, \"districtRank\": 5, " +
                   "\"strengths\": [...], \"improvements\": [...], \"achievements\": \"...\"}";
        }
        
        private SchoolPerformance parseResponse(String jsonResponse, String schoolName) {
            try {
                JSONObject response = new JSONObject(jsonResponse);
                JSONArray choices = response.getJSONArray("choices");
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                String content = message.getString("content");
                
                // Extract JSON from content
                int jsonStart = content.indexOf("{");
                int jsonEnd = content.lastIndexOf("}") + 1;
                
                if (jsonStart != -1 && jsonEnd > jsonStart) {
                    String jsonData = content.substring(jsonStart, jsonEnd);
                    JSONObject performanceData = new JSONObject(jsonData);
                    
                    return new SchoolPerformance(
                            schoolName,
                            performanceData.optInt("rating", 75),
                            performanceData.optDouble("passRate", 85.0),
                            performanceData.optDouble("bachelorPass", 65.0),
                            performanceData.optInt("districtRank", 0),
                            parseArray(performanceData.optJSONArray("strengths")),
                            parseArray(performanceData.optJSONArray("improvements")),
                            performanceData.optString("achievements", "N/A")
                    );
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing response", e);
            }
            
            // Return default if parsing fails
            return createDefaultPerformance(schoolName);
        }
        
        private String[] parseArray(JSONArray jsonArray) {
            if (jsonArray == null) return new String[0];
            
            String[] result = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                result[i] = jsonArray.optString(i, "");
            }
            return result;
        }
        
        private SchoolPerformance createDefaultPerformance(String schoolName) {
            return new SchoolPerformance(
                    schoolName,
                    75, // Default rating
                    85.0, // Default pass rate
                    65.0, // Default bachelor pass
                    0, // Unknown rank
                    new String[]{"Quality education", "Experienced teachers", "Good facilities"},
                    new String[]{"Data not available yet"},
                    "Performance data will be updated"
            );
        }
    }
}
