#include "Util.h"
std::string Headerhelper::getTokenFromHeaders (const HttpRequestPtr& req)
    {
        // 1. Проверка авторизации
        auto authHeader = req->getHeader("Authorization");
        if (authHeader.empty() || authHeader.find("Bearer ") == std::string::npos) 
        {
            throw std::runtime_error("Missing or invalid Authorization header");
        }

        // 2. Извлечение и проверка токена
        std::string token = authHeader.substr(7);
        if (token.empty()) 
        {
            throw std::runtime_error("Empty token");
        }
        return token;
    }