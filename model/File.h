#pragma once
#include <iostream>
#include <list>
#include <drogon/drogon.h>
class File 
{
    int id;
    std::string body;
    std::string file_path;
public:

    void fromDb(const drogon::orm::Row &result) 
    { 
       id = result["id"].as<int>();
       body = result["body"].as<std::string>();
       file_path = result["file_path"].as<std::string>();
    }

// Setters
    void setId(const int& id_news) { this->id = id_news; }
    void setBody(const std::string& body_news) { this->body = body_news; }
    void setFilePath(const std::string& path) { file_path = path; }
    // Getters
    int getId() const { return id; }
    std::string getBody() const { return body; }
    std::string getFilePath() const { return file_path; }
};