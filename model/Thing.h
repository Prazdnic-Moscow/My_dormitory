#pragma once
#include <iostream>
#include <list>
#include <drogon/drogon.h>
class Thing 
{
    int id;
    std::string type;
    std::string body;
    std::string date;
    std::list<std::string> thing_paths;
public:

    void fromDb(const drogon::orm::Row &result) 
    { 
       id = result["id"].as<int>();
       type = result["type"].as<std::string>();
       body = result["body"].as<std::string>();
       date = result["date"].as<std::string>();
    }

// Setters
    void setId(const int& id_news) { this->id = id_news; }
    void setType(const std::string& type_thing) { this->type = type_thing; }
    void setBody(const std::string& body_thing) { this->body = body_thing; }
    void setDate(const std::string& date_thing) { this->date = date_thing; }
    void setFilePaths(const std::list<std::string>& paths) { thing_paths = paths; }
    // Getters
    int getId() const { return id; }
    std::string getType() const { return type; }
    std::string getBody() const { return body; }
    std::string getDate() const { return date; }
    const std::list<std::string>& getFilePaths() const { return thing_paths; }
};