#include "S3Controller.h"
void S3Controller::postFile(const HttpRequestPtr& req,
                            std::function<void(const HttpResponsePtr&)>&& callback,
                            std::string folder)
{
    if (folder != "user" && folder != "news" && folder != "file" && folder != "tutor" && folder != "repair" && folder != "thing" && folder != "repairman")
    {
        Json::Value error;
        error["error"] = "Invalid path for file";
        error["code"] = 400;
        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(k400BadRequest);
        callback(resp);
        return;
    }

    // Получаем "сырые" бинарные данные файла
    std::string body(req->body());
    if (body.empty()) 
    {
        Json::Value error;
        error["error"] = "Empty file body";
        error["code"] = 400;
        
        auto resp = HttpResponse::newHttpJsonResponse(error);
        resp->setStatusCode(k400BadRequest);
        callback(resp);
        return;
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
        auto err = HttpResponse::newHttpResponse();
        err->setStatusCode(k500InternalServerError);
        callback(err);
    }

    // Ответ клиенту
    Json::Value result;
    result["file_path"] = filename;
    auto resp = HttpResponse::newHttpJsonResponse(result);
    resp->setStatusCode(k201Created);
    callback(resp);
}


void S3Controller::getFile(const HttpRequestPtr& req,
                          std::function<void(const HttpResponsePtr&)>&& callback,
                          std::string folder, std::string file_path)
{
    try 
    {
        S3Service s3service("mydormitory");
        // Добавляем "news/" к имени файла
        std::string key = "/" + folder + "/" + file_path;
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

void S3Controller::deleteFile(const HttpRequestPtr& req,
                              std::function<void(const HttpResponsePtr&)>&& callback,
                              std::string folder, std::string file_path)
{

    S3Service s3service("mydormitory");
    std::string key = "/" + folder + "/" + file_path;

    if(!s3service.deleteFile(key))
    {
        auto err = HttpResponse::newHttpResponse();
        err->setStatusCode(k304NotModified);
        callback(err);
    }

    auto resp = HttpResponse::newHttpResponse();
    resp->setBody("Image deleted successfully.");
    resp->setStatusCode(k200OK);
    callback(resp);
}