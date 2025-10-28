#include <drogon/HttpAppFramework.h>
#include "controller/UserController.h"
#include <aws/core/Aws.h>

int main() {
    // Инициализируем AWS SDK
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    // Настройка сервера
    drogon::app()
        .addListener("0.0.0.0", 3000)
        .loadConfigFile("../config.json")
        .run();

    // Завершаем AWS SDK
    Aws::ShutdownAPI(options);

    return 0;
}