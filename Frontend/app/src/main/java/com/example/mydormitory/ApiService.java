package com.example.mydormitory;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
    
    private static final String TAG = "ApiService";
    // Для эмулятора Android используем 10.0.2.2 вместо localhost
    // Для реального устройства используйте IP адрес вашего компьютера в локальной сети
    private static final String BASE_URL = "http://10.0.2.2:3000";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private OkHttpClient client;
    private Gson gson;
    
    public ApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    // Интерфейс для обработки ответа от API
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }
    
    // Класс для данных входа
    public static class LoginRequest {
        private String phone_number;
        private String password;
        
        public LoginRequest(String phone_number, String password) {
            this.phone_number = phone_number;
            this.password = password;
        }
        
        public String getPhone_number() {
            return phone_number;
        }
        
        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    // Класс для ответа от API
    public static class LoginResponse {
        private boolean success;
        private String message;
        private String token;
        private User user;
        
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
    }
    
    // Метод для выполнения запроса входа
    public void login(String phoneNumber, String password, ApiCallback callback) {
        // TODO: ОТПРАВИТЬ ЗАПРОС НА БЭКЕНД
        // URL: http://10.0.2.2:3000/login
        // Метод: POST
        // Headers: Content-Type: application/json
        // Body: {"phone_number": "+79123456789", "password": "your_password"}
        
        LoginRequest loginRequest = new LoginRequest(phoneNumber, password);
        String json = gson.toJson(loginRequest);
        
        String fullUrl = BASE_URL + "/login";
        Log.d(TAG, "Making request to: " + fullUrl);
        Log.d(TAG, "Request body: " + json);
        
        // ЗАКОММЕНТИРОВАНО: Реальный HTTP запрос
        // RequestBody body = RequestBody.create(json, JSON);
        // Request request = new Request.Builder()
        //         .url(fullUrl)
        //         .post(body)
        //         .addHeader("Content-Type", "application/json")
        //         .build();
        // new LoginTask(callback).execute(request);
        
        // Эмуляция успешного ответа для демонстрации
        simulateLoginResponse(callback);
    }
    
    // Метод для эмуляции ответа от сервера (вместо реального запроса)
    private void simulateLoginResponse(ApiCallback callback) {
        // Эмулируем успешный ответ
        String mockResponse = "{\"success\": true, \"message\": \"Login successful\", \"token\": \"mock_token_123\", \"user\": {\"id\": 1, \"phone_number\": \"+79123456789\", \"name\": \"Тестовый Пользователь\"}}";
        callback.onSuccess(mockResponse);
    }
    
    // ЗАКОММЕНТИРОВАНО: AsyncTask для выполнения HTTP запроса в фоновом потоке
    /*
    // AsyncTask для выполнения HTTP запроса в фоновом потоке
    private class LoginTask extends AsyncTask<Request, Void, String> {
        private ApiCallback callback;
        private String errorMessage;
        
        public LoginTask(ApiCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(Request... requests) {
            try {
                Response response = client.newCall(requests[0]).execute();
                
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);
                    return responseBody;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    Log.e(TAG, "Error response: " + response.code() + " - " + errorBody);
                    errorMessage = "Ошибка сервера: " + response.code();
                    return null;
                }
                
            } catch (IOException e) {
                Log.e(TAG, "Network error: " + e.getMessage());
                errorMessage = "Ошибка сети: " + e.getMessage();
                return null;
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage());
                errorMessage = "Неожиданная ошибка: " + e.getMessage();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    // Парсим JSON ответ
                    LoginResponse loginResponse = gson.fromJson(result, LoginResponse.class);
                    
                    if (loginResponse.isSuccess()) {
                        callback.onSuccess(result);
                    } else {
                        callback.onError(loginResponse.getMessage() != null ? 
                                loginResponse.getMessage() : "Ошибка авторизации");
                    }
                } catch (JsonSyntaxException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    callback.onError("Ошибка обработки ответа сервера");
                }
            } else {
                callback.onError(errorMessage != null ? errorMessage : "Неизвестная ошибка");
            }
        }
    }
    */
}
