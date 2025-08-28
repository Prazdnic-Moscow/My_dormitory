#include "NewsController.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

void NewsController::getNews(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);

        if (!Headerhelper::verifyToken(decoded))
        {
            throw std::runtime_error("Token olds");
        }
        if (!Headerhelper::checkRoles(decoded, "news_read"))
        {
            throw std::runtime_error("Not rights Role - News_read");
        }

        S3Service s3service("mydormitory");
        // 6. Получение данных пользователя
        auto dbClient = drogon::app().getDbClient();
        NewsService newsService(dbClient);
        auto news = newsService.getNews();

        // 3. Формируем JSON-ответ
        Json::Value jsonUsers;
        for (const auto& news_current : news) 
        {
            Json::Value jsonUser;
            jsonUser["id"] = news_current.getId();
            jsonUser["header"] = news_current.getHeader();
            jsonUser["body"] = news_current.getBody();
            jsonUser["author"] = news_current.getAuthor();
            jsonUser["date"] = news_current.getDate();
            jsonUser["date_start"] = news_current.getDateStart();
            jsonUser["date_end"] = news_current.getDateEnd();
            jsonUser["image_path"] = news_current.getImagePath();
            jsonUsers.append(jsonUser);
        }

        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonUsers);
        callback(resp);
    }


void NewsController::deleteNews(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int id_news)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (Headerhelper::verifyToken(decoded))
        {
            auto roles = decoded.get_payload_claim("roles");
            auto role_type = roles.to_json();
            bool newsWriteFlag = false;
            for (const auto& role : role_type)
            {
                if (role.asString() == "news_write")
                {
                    newsWriteFlag = true;
                    break;
                }
            }
            if (!newsWriteFlag)
            {
                throw std::runtime_error("Access denied: insufficient privileges");
            }

            // 3. Получаем подключение к БД
            auto dbClient = drogon::app().getDbClient();
            NewsService news(dbClient);
            
            bool result = news.deleteNews(id_news);
            
            if (!result)
            {
                auto resp = HttpResponse::newHttpResponse();
                // 3. Возвращаем 204 No Content
                resp->setStatusCode(k404NotFound);
                callback(resp);
            }
            auto resp = HttpResponse::newHttpResponse();
            // 3. Возвращаем 204 No Content
            resp->setStatusCode(k204NoContent);
            callback(resp);
        }

    }



void NewsController::createNews(const HttpRequestPtr& req,
                   std::function<void(const HttpResponsePtr&)>&& callback)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        if (!Headerhelper::verifyToken(decoded)) 
        {
            auto resp = HttpResponse::newHttpResponse();
            resp->setStatusCode(k401Unauthorized);
            callback(resp);
            return;
        }
            
        if (!Headerhelper::checkRoles(decoded,"news_write"))
        {
            throw std::runtime_error("Access denied: insufficient privileges");
        }
        
        // Получаем JSON данные (после обработки файлов)
        auto json = req->getJsonObject();
        if (!json) 
        {
            throw std::runtime_error("Invalid JSON");
        }

        // Извлекаем данные из JSON
        std::string header = json->get("header", "").asString();
        std::string body = json->get("body", "").asString();
        std::string author = json->get("author", "").asString();
        std::string date = json->get("date", "").asString();
        std::string date_start = json->get("date_start", "").asString();
        std::string date_end = json->get("date_end", "").asString();
        std::string image_path = json->get("image_path", "").asString();

        // 3. Получаем подключение к БД
        auto dbClient = drogon::app().getDbClient();
        NewsService news(dbClient);
        
        auto news_data = news.createNews(
            header,
            body,
            author,
            date,
            date_start,
            date_end,
            image_path
        );

        // 3. Формируем JSON-ответ
        Json::Value jsonUser;
        jsonUser["id"] = news_data.getId();
        jsonUser["header"] = news_data.getHeader();
        jsonUser["body"] = news_data.getBody();
        jsonUser["author"] = news_data.getAuthor();
        jsonUser["date"] = news_data.getDate();
        jsonUser["date_start"] = news_data.getDateStart();
        jsonUser["date_end"] = news_data.getDateEnd();
        jsonUser["image_path"] = news_data.getImagePath();

        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonUser);
        callback(resp);
    }


    void NewsController::getImage(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback, const std::string& filename) 
{
    std::string token = Headerhelper::getTokenFromHeaders(req);
    auto decoded = jwt::decode<traits>(token);
    
    if (!Headerhelper::verifyToken(decoded)) 
    {
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(k401Unauthorized);
        callback(resp);
        return;
    }

    // Заглушка - реализуйте позже
    auto resp = HttpResponse::newHttpResponse();
    resp->setStatusCode(k501NotImplemented);
    resp->setBody("Image endpoint not implemented yet");
    callback(resp);
}

    

