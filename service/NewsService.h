#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/News.h"
#include "../repository/NewsRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class NewsService
{
public:
    // Конструктор
    explicit NewsService(const drogon::orm::DbClientPtr& dbClient);

    News createNews
    (
        std::string header,
        std::string body,
        std::string author,
        std::string date,
        std::string date_start,
        std::string date_end,
        std::string image_path
    );

    bool deleteNews(int id_news);

    std::list<News> getNews();

private:
    std::shared_ptr<NewsRepository> repository; // Доступ к БД
};