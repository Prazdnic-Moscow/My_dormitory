#pragma once
#include <iostream>
#include <list>
#include <drogon.h>
class File 
{
    int id;
    std::string body;
    std::string date;
    std::list<std::string> file_paths;

    public:

        void fromDb(const drogon::orm::Row &result) 
        { 
        id = result["id"].as<int>();
        body = result["body"].as<std::string>();
        date = result["date"].as<std::string>();
        }

    // Setters
        void setId(const int& id_news) 
        { 
            this->id = id_news; 
        }

        void setBody(const std::string& body_news) 
        {
            this->body = body_news; 
        }

        void setDate(const std::string& date_news) 
        { 
            this->date = date_news; 
        }

        void setFilePaths(const std::list<std::string>& paths) 
        { 
            file_paths = paths; 
        }

        // Getters
        int getId() const 
        { 
            return id; 
        }

        std::string getBody() 
        { 
            return body; 
        }

        std::string getDate() 
        { 
            return date; 
        }

        const std::list<std::string>& getFilePaths() 
        { 
            return file_paths; 
        }
};