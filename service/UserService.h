#pragma once
#include <list>
#include <string>
#include <memory>
#include "../model/User.h"
#include "../repository/UserRepository.h"
#include <bcrypt/BCrypt.hpp>
#include <jwt-cpp/jwt.h>
#include <drogon/drogon.h>
#include <stdexcept>
#include <iostream>

class UserService {
public:
    // Конструктор
    explicit UserService(const drogon::orm::DbClientPtr& dbClient);

    // Регистрация с полными данными пользователя
    UserData registerUser(
        const std::string &phone_number,
        const std::string &password,
        const std::string &name,
        const std::string &last_name,
        const std::string &surname,
        const std::string &document);

    // Войти в систему
    std::string login(const std::string &phone_number, const std::string &password);

    // Получение инфы по Id
    UserData getUser(int id);

    // Удаление
    bool deleteUser(int id);
    
    std::list<UserData> getUsers();

    void checkData(const std::string &phone_number, const std::string &password);
    
    void addRole(int user_id, int role_id);
    bool deleteRole(int user_id, int role_id);

    private:
    std::shared_ptr<UserRepository> repository; // Доступ к БД
};