#pragma once
#include <string>
#include <list>
#include <memory>
#include "../model/File.h"
#include "../repository/FileRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>
#include "jwt-cpp/traits/open-source-parsers-jsoncpp/traits.h"
using traits = jwt::traits::open_source_parsers_jsoncpp;
using claim = jwt::basic_claim<traits>;

class FileService
{
public:
    // Конструктор
    explicit FileService(const drogon::orm::DbClientPtr& dbClient);

    File createFile
    (
        std::string body,
        std::string date,
        std::list<std::string> file_path
    );

    bool deleteFile(int id_file);

    std::list<File> getFiles();

private:
    std::shared_ptr<FileRepository> repository; // Доступ к БД
};