#pragma once
#include <iostream>
#include <list>
#include <drogon/drogon.h>
class News 
{
    std::string id;
    std::string header;
    std::string body;
    std::string author;
    std::string date;
    std::string date_start;
    std::string date_end;
    std::string image_path;
public:

    void fromDb(const drogon::orm::Row &result) 
    { 
       id = result["id"].as<std::string>();
       header = result["header"].as<std::string>();
       body = result["body"].as<std::string>();
       author= result["author"].as<std::string>();
       date = result["date"].as<std::string>();
       date_start = result["date_start"].as<std::string>();
       date_end = result["date_end"].as<std::string>();
       image_path = result["image_path"].as<std::string>();
    }

// Setters
    void setId(const std::string& id_news) { this->id = id_news; }
    void setHeader(const std::string& header_news) { this->header = header_news; }
    void setBody(const std::string& body_news) { this->body = body_news; }
    void setAuthor(const std::string& author_news) { this->author = author_news; }
    void setDate(const std::string& date_news) { this->date = date_news; }
    void setDateStart(const std::string& date_start_news) { this->date_start = date_start_news; }
    void setDateEnd(const std::string& date_end_news) { this->date_end = date_end_news; }
    void setImagePath(const std::string& path) { image_path = path; }
    // Getters
    std::string getId() const { return id; }
    std::string getHeader() const { return header; }
    std::string getBody() const { return body; }
    std::string getAuthor() const { return author; }
    std::string getDate() const { return date; }
    std::string getDateStart() const { return date_start; }
    std::string getDateEnd() const { return date_end; }
    std::string getImagePath() const { return image_path; }
};