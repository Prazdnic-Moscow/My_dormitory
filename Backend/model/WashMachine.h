#pragma once
#include <iostream>
#include <list>
#include <drogon.h>
#include <string>
#include "ReserveWashMachine.h"
class WashMachine 
{
    int id;
    std::string name;
    std::list<ReserveWashMachine> reserve;

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

        void setReserve(std::list<ReserveWashMachine> reserver)
        {
            this -> reserve = reserver; 
        }

        int getId() 
        { 
            return id; 
        }

        std::string getName()
        {
            return name; 
        }

        std::list<ReserveWashMachine> getReserve()
        {
            return reserve; 
        }
};