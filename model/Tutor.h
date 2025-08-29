#pragma once
#include <iostream>
#include <list>
#include <drogon/drogon.h>
class Tutor 
{
    std::string id;
    std::string header;
    std::string body;
    std::string date;
    std::string image_path;
public:

    void fromDb(const drogon::orm::Row &result) 
    { 
       id = result["id"].as<std::string>();
       header = result["header"].as<std::string>();
       body = result["body"].as<std::string>();
       date = result["date"].as<std::string>();
       image_path = result["image_path"].as<std::string>();
    }

// Setters
    void setId(const std::string& id_news) { this->id = id_news; }
    void setHeader(const std::string& header_news) { this->header = header_news; }
    void setBody(const std::string& body_news) { this->body = body_news; }
    void setDate(const std::string& date_news) { this->date = date_news; }
    void setImagePath(const std::string& path) { image_path = path; }
    // Getters
    std::string getId() const { return id; }
    std::string getHeader() const { return header; }
    std::string getBody() const { return body; }
    std::string getDate() const { return date; }
    std::string getImagePath() const { return image_path; }
};