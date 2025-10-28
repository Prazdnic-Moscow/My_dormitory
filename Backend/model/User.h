#pragma once
#include <iostream>
#include <list>
#include <drogon.h>
class UserData 
{
    private:
        // Регистрация с полными данными пользователя
        std::string phone_number;
        std::string password;
        std::string name;
        std::string last_name;
        std::string surname;
        std::list<std::string> document;
        std::list<std::string> role_type;
        int id;

    public:
        void fromDb(const drogon::orm::Row &result) 
        { 
            id = result["id"].as<int>();
            phone_number = result["phone_number"].as<std::string>();
            password = result["password"].as<std::string>();
            name = result["name"].as<std::string>();
            last_name = result["last_name"].as<std::string>();
            surname = result["surname"].as<std::string>();
        }

        // Setters
        void setId(const int& id) 
        { 
            this->id = id; 
        }
        
        void setPhoneNumber(const std::string& phone) 
        { 
            this->phone_number = phone; 
        }

        void setPassword(const std::string& pass) 
        { 
            this->password = pass; 
        }

        void setName(const std::string& name) 
        { 
            this->name = name; 
        }

        void setLastName(const std::string& lastName) 
        { 
            this->last_name = lastName;
        }

        void setSurname(const std::string& surname) 
        { 
            this->surname = surname; 
        }

        void setDocuments(const std::list<std::string>& path) 
        { 
            document = path; 
        }

        void setRoles(const std::list<std::string>& role) 
        { 
            this->role_type = role; 
        }

        // Getters
        int getId()
        { 
            return id;
        }

        std::string getPhoneNumber()
        { 
            return phone_number; 
        }

        std::string getPassword()
        { 
            return password; 
        }

        std::string getName()
        { 
            return name; 
        }

        std::string getLastName()
        { 
            return last_name; 
        }

        std::string getSurname()
        { 
            return surname; 
        }

        std::list<std::string> getDocument() 
        { 
            return document; 
        }

        std::list<std::string> getRoles()
        { 
            return role_type; 
        }
};