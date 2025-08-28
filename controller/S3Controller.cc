#include "S3Controller.h"
void S3Controller::upload(const HttpRequestPtr& req,
                 std::function<void(const HttpResponsePtr&)>&& callback)
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
        std::string filename = "news/" + std::string(uuid_str) + extension;

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