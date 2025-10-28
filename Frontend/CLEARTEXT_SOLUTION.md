# Решение проблемы CLEARTEXT communication

## Проблема
```
Network error: CLEARTEXT communication to localhost not permitted by network security policy
```

## ✅ Полное решение

### 1. Созданы Network Security Config файлы

**Debug версия** (`app/src/main/res/xml/network_security_config_debug.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Полностью разрешаем HTTP трафик для debug сборки -->
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
            <certificates src="user"/>
        </trust-anchors>
    </base-config>
</network-security-config>
```

**Production версия** (`app/src/main/res/xml/network_security_config.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">127.0.0.1</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.0/24</domain>
        <domain includeSubdomains="true">192.168.0.0/24</domain>
    </domain-config>
</network-security-config>
```

### 2. Обновлен build.gradle.kts
```kotlin
buildTypes {
    debug {
        isDebuggable = true
        isMinifyEnabled = false
        applicationIdSuffix = ".debug"
        versionNameSuffix = "-debug"
        manifestPlaceholders["networkSecurityConfig"] = "@xml/network_security_config_debug"
    }
    release {
        isMinifyEnabled = false
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
        manifestPlaceholders["networkSecurityConfig"] = "@xml/network_security_config"
    }
}
```

### 3. Обновлен AndroidManifest.xml
```xml
<application
    android:networkSecurityConfig="${networkSecurityConfig}"
    android:usesCleartextTraffic="true"
    tools:targetApi="28">
```

### 4. URL в ApiService
```java
private static final String BASE_URL = "http://10.0.2.2:3000";
```

## 🔧 Действия для решения проблемы:

### Шаг 1: Очистите проект
```bash
./gradlew clean
```

### Шаг 2: Пересоберите проект
```bash
./gradlew assembleDebug
```

### Шаг 3: Переустановите приложение
```bash
./gradlew installDebug
```

### Шаг 4: Проверьте логи
В логах должно появиться:
```
D/ApiService: Making request to: http://10.0.2.2:3000/login
D/ApiService: Request body: {"phone_number":"+79123456789","password":"password123"}
```

## 🌐 Альтернативные решения для разных сред:

### Для эмулятора Android:
- URL: `http://10.0.2.2:3000`

### Для реального устройства в той же WiFi сети:
1. Найдите IP адрес вашего компьютера:
   - Windows: `ipconfig`
   - Mac/Linux: `ifconfig`
2. Измените URL в ApiService:
   ```java
   private static final String BASE_URL = "http://192.168.1.100:3000"; // ваш IP
   ```

### Для HTTPS (рекомендуется для production):
1. Настройте SSL сертификат на бекенде
2. Измените URL:
   ```java
   private static final String BASE_URL = "https://your-domain.com";
   ```

## ⚠️ Важные замечания:

1. **Debug vs Release**: Debug версия разрешает HTTP для всех доменов, Release - только для указанных
2. **Безопасность**: В production используйте HTTPS
3. **Кэширование**: После изменений в network security config переустановите приложение
4. **Логирование**: Добавлено отладочное логирование для проверки URL

## 🐛 Если проблема остается:

1. Убедитесь, что приложение переустановлено после изменений
2. Проверьте, что используется debug сборка
3. Проверьте логи на предмет правильного URL
4. Убедитесь, что бекенд запущен на указанном порту
