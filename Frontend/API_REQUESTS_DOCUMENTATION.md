# Документация API запросов для приложения MyDormitory

## Базовый URL
```
http://10.0.2.2:3000
```

## 1. Авторизация

### Вход в систему
- **URL:** `POST /login`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "phone_number": "+79123456789",
    "password": "your_password"
}
```
- **Ответ при успехе:**
```json
{
    "success": true,
    "message": "Login successful",
    "token": "jwt_token_here",
    "user": {
        "id": 1,
        "phone_number": "+79123456789",
        "name": "Иван Иванов"
    }
}
```
- **Ответ при ошибке:**
```json
{
    "success": false,
    "message": "Invalid credentials"
}
```

### Регистрация
- **URL:** `POST /register`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "phone_number": "+79123456789",
    "password": "password123",
    "first_name": "Иван",
    "last_name": "Иванов",
    "middle_name": "Иванович",
    "document": "base64_encoded_pdf_file"
}
```

## 2. Заявки на ремонт

### Отправка заявки на ремонт
- **URL:** `POST /repair-request`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "service_type": "Сантехник",
    "room_number": "123",
    "description": "Описание проблемы",
    "photos": ["base64_photo1", "base64_photo2"],
    "user_id": "current_user_id"
}
```

## 3. Бронирование прачечной

### Бронирование стиральной машины
- **URL:** `POST /laundry-booking`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "machine_number": 1,
    "date": "2024-01-15",
    "start_time": "10:00",
    "end_time": "12:00",
    "duration": 2.0,
    "user_id": "current_user_id"
}
```

### Получение доступных временных слотов
- **URL:** `GET /laundry-availability?machine=1&date=2024-01-15`

### Получение активных бронирований пользователя
- **URL:** `GET /user-laundry-bookings`

### Отмена бронирования
- **URL:** `DELETE /laundry-booking/{booking_id}`

## 4. Бронирование спортзала

### Бронирование спортзала
- **URL:** `POST /gym-booking`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "date": "2024-01-15",
    "time_slot": "10:00-12:00",
    "user_id": "current_user_id"
}
```

### Получение доступных временных слотов
- **URL:** `GET /gym-availability?date=2024-01-15`

### Получение активных бронирований пользователя
- **URL:** `GET /user-gym-bookings`

### Отмена бронирования
- **URL:** `DELETE /gym-booking/{booking_id}`

## 5. Бронирование читального зала

### Бронирование читального зала
- **URL:** `POST /study-room-booking`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "room_number": 1,
    "date": "2024-01-15",
    "time_slot": "10:00-12:00",
    "user_id": "current_user_id"
}
```

### Получение доступных комнат
- **URL:** `GET /study-rooms`

### Получение доступных временных слотов
- **URL:** `GET /study-room-availability?room=1&date=2024-01-15`

### Получение активных бронирований пользователя
- **URL:** `GET /user-study-room-bookings`

### Отмена бронирования
- **URL:** `DELETE /study-room-booking/{booking_id}`

## 6. Новости и объявления

### Получение новостей
- **URL:** `GET /news`

### Получение объявлений
- **URL:** `GET /announcements`

### Создание объявления (для администраторов)
- **URL:** `POST /announcements`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "title": "Заголовок объявления",
    "content": "Содержание объявления",
    "category": "Общее",
    "user_id": "admin_user_id"
}
```

## 7. Документы

### Получение списка документов
- **URL:** `GET /documents`

### Получение шаблонов документов
- **URL:** `GET /document-templates`

### Загрузка документа
- **URL:** `GET /documents/{document_id}/download`

## 8. Профиль пользователя

### Получение профиля пользователя
- **URL:** `GET /user/profile`

### Обновление профиля
- **URL:** `PUT /user/profile`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
    "first_name": "Иван",
    "last_name": "Иванов",
    "middle_name": "Иванович",
    "phone_number": "+79123456789"
}
```

## Статус коды ответов

- `200` - Успешный запрос
- `201` - Ресурс создан
- `400` - Неверный запрос
- `401` - Не авторизован
- `403` - Доступ запрещен
- `404` - Ресурс не найден
- `500` - Внутренняя ошибка сервера

## Аутентификация

Для защищенных эндпоинтов необходимо передавать JWT токен в заголовке:
```
Authorization: Bearer your_jwt_token_here
```

## Примечания

- Все даты передаются в формате `YYYY-MM-DD`
- Время передается в формате `HH:MM`
- Фотографии передаются в формате base64
- PDF документы также передаются в формате base64
- Для эмулятора Android используется `10.0.2.2` вместо `localhost`
- Для реального устройства используйте IP адрес компьютера в локальной сети
