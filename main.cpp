#include <drogon/HttpAppFramework.h>
#include "controller/UserController.h"

int main() {
    // Настройка сервера
    drogon::app()
        .addListener("0.0.0.0", 3000)
        .loadConfigFile("../config.json")
        .run();
    
    return 0;
}