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

bool Headerhelper::verifyToken (jwt::decoded_jwt<traits> decoded)
{
    try
    {
        // 1. Создаем верификатор
        auto verifier = jwt::verify<traits>()
        .allow_algorithm(jwt::algorithm::hs256{"your_secret_key"});
        // 3. Верифицируем
        verifier.verify(decoded);
        return true;
    }
    catch(const std::exception& e)
    {
        std::cout << "Verification failed: " << e.what() << std::endl;
        return false;
    }
}

std::string Headerhelper::getTokenType(jwt::decoded_jwt<traits> decoded) 
{
    try 
    {
        return decoded.get_type();
    } 
    catch (...) 
    {
        return "";
    }
}

bool Headerhelper::checkRoles (jwt::decoded_jwt<traits> decoded, std::string role)
{
    auto roles = decoded.get_payload_claim("roles").to_json();
    for (const auto& current_role : roles) 
    {
        if (current_role.asString() == role) 
        {
            return true;
        }
    }
    return false;
}

std::string Headerhelper::getExtension(drogon::ContentType contentType) 
{
    switch(contentType) 
    {
        case drogon::CT_APPLICATION_MSWORD: return ".docx";
        case drogon::CT_APPLICATION_MSWORDX: return ".docx";
        case drogon::CT_IMAGE_JPG: return ".jpg";
        case drogon::CT_IMAGE_PNG: return ".png";
        case drogon::CT_APPLICATION_PDF: return ".pdf";
        default: 
            throw std::runtime_error("Unsupported file type");
    }
}
