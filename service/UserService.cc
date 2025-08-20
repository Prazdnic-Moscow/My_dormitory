#include "UserService.h"
#include <vector>
#include <ranges>
#include <json/json.h>

// Конструктор — инициализируем репозиторий
UserService::UserService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<UserRepository>(dbClient);
}

// Регистрация пользователя
UserData UserService::registerUser(
    const std::string &phone_number,
    const std::string &password,
    const std::string &name,
    const std::string &last_name,
    const std::string &surname,
    const std::string &document)
{

    checkData(phone_number, password);
    // Хешируем пароль
    std::string passwordHash = BCrypt::generateHash(password);
    // Создаём пользователя в БД
    UserData user;
    try {
        user = repository->createUser(
            phone_number,
            passwordHash, 
            name, 
            last_name, 
            surname, 
            document
        );
            return user;
    } 
    catch (const std::exception &e) 
    {
        throw std::runtime_error(std::string("Failed to register user: ") + e.what());
    }
}

// Логин
std::string UserService::login(const std::string &phone_number, const std::string &password)
{
    checkData(phone_number, password);
    // Получаем пользователя из БД
    UserData user;
    try {
        user = repository->getUserByPhone(phone_number);
    } catch (const std::exception &e) {
        throw std::runtime_error("User not found");
    }

    std::string db_password_hash = user.getPassword();

    // Проверяем пароль
    if (!BCrypt::validatePassword(password, db_password_hash)) {
        throw std::runtime_error("Invalid password or login");
    }

    
    // Генерируем JWT токен
    try 
    {
        std::list<std::string> roles = user.getRoles();
        Json::Value jsonRoles;
        for (const auto& role : roles)
        {
            jsonRoles.append(role); // Добавляем каждую роль в массив
        }
        auto token = jwt::create()
            .set_payload_claim("roles", jwt::claim(jsonRoles.toStyledString())) // Добавляем массив
            .set_payload_claim("Id", jwt::claim(user.getId()))
            .set_subject(phone_number)
            .set_expires_at(std::chrono::system_clock::now() + std::chrono::hours{24})
            .sign(jwt::algorithm::hs256{"your_secret_key"});

        return token;
    } 
    catch (const std::exception &e) 
    {
        throw std::runtime_error(std::string("Token generation failed: ") + e.what());
    }
}

std::list<UserData> UserService::getUsersdb()
{
    return repository->getUsers();
};

UserData UserService::getUser(int id)
{
    return repository->getUser(id);
}

void UserService::deleteUser(int id)
{
    return repository->deleteUser(id);
}

void UserService::checkData(const std::string &phone_number, const std::string &password)
{
    if (phone_number.empty() || password.empty()) {
        throw std::runtime_error("Phone number and password are required");
    }

    if (phone_number.length() != 11) {
        throw std::runtime_error("Invalid phone number format");
    }
}