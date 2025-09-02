#include "NewsController.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

void NewsController::getNews(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int limit)
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

        if (limit > 20) limit = 20;
        // 6. Получение данных пользователя
        auto dbClient = drogon::app().getDbClient();
        NewsService newsService(dbClient);
        auto news = newsService.getNews(limit);

            // Формируем JSON-ответ
        Json::Value jsonNewsArray; // переименовал для ясности
        for (const auto& news_current : news) 
        {
            Json::Value jsonNewsItem;
            jsonNewsItem["id"] = news_current.getId();
            jsonNewsItem["header"] = news_current.getHeader();
            jsonNewsItem["body"] = news_current.getBody();
            jsonNewsItem["author"] = news_current.getAuthor();
            jsonNewsItem["date"] = news_current.getDate();
            jsonNewsItem["date_start"] = news_current.getDateStart();
            jsonNewsItem["date_end"] = news_current.getDateEnd();
            
            // Добавляем массив изображений
            Json::Value jsonImages(Json::arrayValue);
            for (const auto& image_path : news_current.getImagePaths()) 
            {
                jsonImages.append(image_path);
            }
            jsonNewsItem["news_path"] = jsonImages; // исправлено имя поля
            
            jsonNewsArray.append(jsonNewsItem); // ДОБАВЛЕНО: добавляем в массив
        }

        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonNewsArray);
        callback(resp);
    }


void NewsController::deleteNews(const HttpRequestPtr& req,
              std::function<void(const HttpResponsePtr&)>&& callback, int id_news)
    {
        std::string token = Headerhelper::getTokenFromHeaders(req);
        auto decoded = jwt::decode<traits>(token);
        
        if (!Headerhelper::checkRoles(decoded, "news_write"))
        {
            throw std::runtime_error("Not rights Role - News_write");
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



void NewsController::postNews(const HttpRequestPtr& req,
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
        // Получаем список изображений (list)
        std::list<std::string> image_paths;
        if (json->isMember("news_path") && (*json)["news_path"].isArray()) 
        {
            const Json::Value& imagesArray = (*json)["news_path"];
            for (const auto& image : imagesArray) 
            {
                std::string path = image.asString();
                if (!path.empty()) 
                {
                    image_paths.push_back(path); // добавляем в list
                }
            }
        }

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
            image_paths
        );

        // 3. Формируем JSON-ответ
        Json::Value jsonNews;
        jsonNews["id"] = news_data.getId();
        jsonNews["header"] = news_data.getHeader();
        jsonNews["body"] = news_data.getBody();
        jsonNews["author"] = news_data.getAuthor();
        jsonNews["date"] = news_data.getDate();
        jsonNews["date_start"] = news_data.getDateStart();
        jsonNews["date_end"] = news_data.getDateEnd();
        // Добавляем массив изображений
        Json::Value jsonImages(Json::arrayValue);
        for (const auto& image_path : news_data.getImagePaths()) 
        {
            jsonImages.append(image_path);
        }
        jsonNews["news_path"] = jsonImages;

        // 4. Создаем и настраиваем ответ
        auto resp = HttpResponse::newHttpJsonResponse(jsonNews);
        callback(resp);
    }

    

