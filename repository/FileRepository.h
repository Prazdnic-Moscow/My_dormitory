#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../controller/S3Controller.h"
#include "../model/File.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class FileRepository 
{
public:
    // Конструктор принимает подключение к БД
    FileRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    File createFile(
        const std::string body, 
        const std::string date,
        std::list<std::string> file_path
    );
    
    // Удаление
    bool deleteFile(int id_tutor);

    std::list<File> getFiles();

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};