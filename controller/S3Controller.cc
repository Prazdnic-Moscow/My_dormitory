#include "S3Controller.h"
void S3Controller::postImage(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback,
                 std::string folder)
{
    try
    {
        // Получаем "сырые" бинарные данные файла
        std::string body(req->body());
        if (body.empty()) 
        {
            throw std::runtime_error("Empty file body");
        }

        // Преобразуем тело в вектор байт
        std::vector<uint8_t> fileData(body.begin(), body.end());

        // Генерируем уникальное имя файла
        uuid_t uuid;
        uuid_generate_random(uuid);
        char uuid_str[37];
        uuid_unparse(uuid, uuid_str);
        auto contentType = req->getContentType();
        std::string extension = Headerhelper::getExtension(contentType);
        // Пример расширения, если знаешь тип — поменяй
        std::string filename = "/" + folder + "/" + std::string(uuid_str) + extension;

        // Создаем S3-сервис
        S3Service s3service("mydormitory");

        // Загружаем
        if (!s3service.uploadFile(filename, fileData, "application/octet-stream")) 
        {
            throw std::runtime_error("S3 upload failed");
        }

        // Ответ клиенту
        Json::Value result;
        result["image_path"] = filename;
        auto resp = HttpResponse::newHttpJsonResponse(result);
        callback(resp);
    }
    catch (const std::exception& e)
    {
        auto errResp = HttpResponse::newHttpResponse();
        errResp->setStatusCode(k500InternalServerError);
        errResp->setContentTypeCode(CT_TEXT_PLAIN);
        errResp->setBody("Upload error: " + std::string(e.what()));
        callback(errResp);
    }
}


void S3Controller::getImage(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback,
                            std::string folder, std::string image_path)
{
try {
    S3Service s3service("mydormitory");
    // Добавляем "news/" к имени файла
    std::string key = "/" + folder + "/" + image_path;
    auto [data, contentType] = s3service.downloadFile(key);

    auto resp = HttpResponse::newHttpResponse();
    resp->setContentTypeString(contentType);
    resp->setBody(std::string(data.begin(), data.end()));
    callback(resp);
    }
    catch (const std::exception& e) 
    {
        auto errResp = HttpResponse::newHttpResponse();
        errResp->setStatusCode(k404NotFound);
        errResp->setContentTypeCode(CT_TEXT_PLAIN);
        errResp->setBody("Image not found: " + std::string(e.what()));
        callback(errResp);
    }
}

void S3Controller::deleteImage(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback,
                            std::string folder, std::string image_path)
{
    try 
    {
        S3Service s3service("mydormitory");
        std::string key = "/" + folder + "/" + image_path;

        s3service.deleteFile(key);

        auto resp = HttpResponse::newHttpResponse();
        resp->setBody("Image deleted successfully.");
        callback(resp);
    } 
    catch (const std::exception& e) 
    {
        auto err = HttpResponse::newHttpResponse();
        err->setStatusCode(k500InternalServerError);
        err->setBody("Delete error: " + std::string(e.what()));
        callback(err);
    }
}