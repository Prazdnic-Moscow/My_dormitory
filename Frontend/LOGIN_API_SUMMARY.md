# Реализация запроса к бекенду для авторизации

## Что было сделано:

### 1. Добавлены зависимости
- **OkHttp 4.12.0** - для HTTP запросов
- **Gson 2.10.1** - для работы с JSON

### 2. Добавлены разрешения в AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. Создан класс ApiService
- Выполняет POST запрос на `http://localhost:3000/login`
- Отправляет JSON с полями `phone_number` и `password`
- Обрабатывает ответ от сервера
- Использует AsyncTask для выполнения запросов в фоновом потоке

### 4. Обновлен LoginActivity
- Интегрирован с ApiService
- Добавлен ProgressBar для отображения процесса загрузки
- Реализована обработка успешного входа и ошибок

### 5. Обновлен макет activity_login.xml
- Добавлен ProgressBar между полем "Забыли пароль?" и кнопкой "Войти"

## Формат запроса:

**URL:** `POST http://localhost:3000/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
    "phone_number": "+79123456789",
    "password": "your_password"
}
```

## Формат ответа:

**Успешный ответ:**
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

**Ответ с ошибкой:**
```json
{
    "success": false,
    "message": "Invalid credentials"
}
```

## Как использовать:

1. Пользователь вводит номер телефона и пароль
2. При нажатии кнопки "Войти" отправляется запрос к бекенду
3. Показывается ProgressBar во время выполнения запроса
4. При успешной авторизации пользователь переходит в MainActivity
5. При ошибке показывается сообщение с описанием проблемы

## Решение проблемы CLEARTEXT communication

### Проблема:
```
Network error: CLEARTEXT communication to localhost not permitted by network security policy
```

### Решение:
1. **Создан network security config** (`app/src/main/res/xml/network_security_config.xml`)
2. **Обновлен AndroidManifest.xml** - добавлены атрибуты:
   - `android:networkSecurityConfig="@xml/network_security_config"`
   - `android:usesCleartextTraffic="true"`
3. **Изменен URL в ApiService** с `localhost` на `10.0.2.2` (для эмулятора Android)

### Важные замечания:
- **Для эмулятора**: используйте `http://10.0.2.2:3000`
- **Для реального устройства**: используйте IP адрес вашего компьютера в локальной сети (например, `http://192.168.1.100:3000`)
- В production версии рекомендуется использовать HTTPS

## Файлы, которые были изменены:
- `gradle/libs.versions.toml` - добавлены зависимости
- `app/build.gradle.kts` - подключены библиотеки
- `app/src/main/AndroidManifest.xml` - добавлены разрешения и network security config
- `app/src/main/res/xml/network_security_config.xml` - **новый файл** для разрешения HTTP соединений
- `app/src/main/java/com/example/mydormitory/ApiService.java` - новый класс для работы с API
- `app/src/main/java/com/example/mydormitory/LoginActivity.java` - обновлена логика авторизации
- `app/src/main/java/com/example/mydormitory/LoginExample.java` - пример использования
- `app/src/main/res/layout/activity_login.xml` - добавлен ProgressBar
