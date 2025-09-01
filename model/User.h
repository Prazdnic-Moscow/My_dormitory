#pragma once
#include <iostream>
#include <list>
#include <drogon/drogon.h>
class UserData {
private:
    // Регистрация с полными данными пользователя
    std::string phone_number;
    std::string password;
    std::string name;
    std::string last_name;
    std::string surname;
    std::string document;
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
       document = result["document"].as<std::string>();
    }

    // Setters
    void setId(const int& id) { this->id = id; }
    void setPhoneNumber(const std::string& phone) { this->phone_number = phone; }
    void setPassword(const std::string& pass) { this->password = pass; }
    void setName(const std::string& name) { this->name = name; }
    void setLastName(const std::string& lastName) { this->last_name = lastName; }
    void setSurname(const std::string& surname) { this->surname = surname; }
    void setDocument(const std::string& doc) { this->document = doc; }
    void setRoles(const std::list<std::string>& role) { this->role_type = role; }

    // Getters
    int getId() const { return id; }
    std::string getPhoneNumber() const { return phone_number; }
    std::string getPassword() const { return password; }
    std::string getName() const { return name; }
    std::string getLastName() const { return last_name; }
    std::string getSurname() const { return surname; }
    std::string getDocument() const { return document; }
    std::list<std::string> getRoles() const { return role_type; }
};