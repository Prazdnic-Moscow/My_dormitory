package com.example.mydormitory;

/**
 * Пример использования запроса к бекенду для авторизации
 * 
 * Этот класс демонстрирует, как работает запрос к localhost:3000/login
 * с отправкой phone_number и password в body запроса.
 */
public class LoginExample {
    
    /**
     * Пример JSON запроса, который отправляется на сервер:
     * 
     * POST http://localhost:3000/login
     * Content-Type: application/json
     * 
     * {
     *     "phone_number": "+79123456789",
     *     "password": "your_password"
     * }
     * 
     * Пример успешного ответа:
     * {
     *     "success": true,
     *     "message": "Login successful",
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "user": {
     *         "id": 1,
     *         "phone_number": "+79123456789",
     *         "name": "Иван Иванов"
     *     }
     * }
     * 
     * Пример ответа с ошибкой:
     * {
     *     "success": false,
     *     "message": "Invalid credentials"
     * }
     */
    
    public static void demonstrateUsage() {
        // Создаем экземпляр API сервиса
        ApiService apiService = new ApiService();
        
        // ЗАКОММЕНТИРОВАНО: Реальный запрос на авторизацию
        // Выполняем запрос на авторизацию
        // apiService.login("+79123456789", "password123", new ApiService.ApiCallback() {
        //     @Override
        //     public void onSuccess(String response) {
        //         System.out.println("Успешная авторизация: " + response);
        //         // Здесь можно обработать успешный ответ
        //         // Например, сохранить токен и перейти к следующему экрану
        //     }
        //     
        //     @Override
        //     public void onError(String error) {
        //         System.out.println("Ошибка авторизации: " + error);
        //         // Здесь можно обработать ошибку
        //         // Например, показать сообщение пользователю
        //     }
        // });
        
        // Демонстрация работы с эмулированным ответом
        System.out.println("Демонстрация API запроса для авторизации");
        System.out.println("URL: http://10.0.2.2:3000/login");
        System.out.println("Метод: POST");
        System.out.println("Body: {\"phone_number\": \"+79123456789\", \"password\": \"password123\"}");
    }
}
