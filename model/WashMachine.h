#pragma once
#include <iostream>
#include <list>
#include <drogon.h>
#include <string>
class WashMachine 
{
    int id;
    std::string name;

    public :
        void FromDB(const drogon::orm::Row &result)
        {
            id = result["id"].as<int>();
            name = result["name"].as<std::string>();
        }

        void setId(const int& id) 
        { 
            this->id = id; 
        }

        void setName(const std::string& namee)
        {
            this -> name = namee; 
        }

        int getId() 
        { 
            return id; 
        }

        std::string getName()
        {
            return name; 
        }
};