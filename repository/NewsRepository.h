#pragma once
#include <drogon/orm/DbClient.h> // Подключение к PostgreSQL
#include <string>
#include "../model/News.h"
#include <list>
#include "bcrypt/BCrypt.hpp"
class NewsRepository 
{
public:
    // Конструктор принимает подключение к БД
    NewsRepository(const drogon::orm::DbClientPtr &dbClient) : db_(dbClient) {}

    // Создать пользователя в БД
    News createNews(
        const std::string header, 
        const std::string body, 
        const std::string author,
        const std::string date, 
        const std::string date_start, 
        const std::string date_end,
        const std::string image_path
    );
    
    // Удаление
    bool deleteNews(int id);

    std::list<News> getNews();

private:
    drogon::orm::DbClientPtr db_; // Подключение к PostgreSQL
};