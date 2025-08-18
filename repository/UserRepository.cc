#include "UserRepository.h"
// Реализация метода создания пользователя
UserData UserRepository::createUser(
    const std::string &phone_number, 
    const std::string &passwordHash,
    const std::string &name,
    const std::string &last_name,
    const std::string &surname,
    const std::string &document)
{
    try {
        // 1. Создаём пользователя
        auto result = db_->execSqlSync
        (
            "INSERT INTO users (phone_number, password, name, last_name, surname, document) "
            "VALUES ($1, $2, $3, $4, $5, $6) "
            "RETURNING id, phone_number, password, name, last_name, surname, document",
            phone_number, passwordHash, name, last_name, surname, document
        );
        
        if (result.empty()) 
        {
            throw std::runtime_error("Failed to create user");
        }
        
        UserData user;
        user.fromDb(result[0]);
        
        // 2. Назначаем роль
        db_->execSqlSync
        (
            "INSERT INTO users_roles (user_id, role_id) VALUES ($1, 6)",
            user.getId()
        );
        std::list<std::string> Roles;
        Roles.push_back("news_read");
        user.setRoles(Roles);
        // 3. Получаем полные данные с ролью
        return user;
        
    } catch (const std::exception& e) {
        throw std::runtime_error("Failed to create user: " + std::string(e.what()));
    }
}

UserData UserRepository::getUserByPhone(const std::string &phone_number) 
{
    UserData user;
    auto result = db_->execSqlSync
    (
        "SELECT * FROM users u"
        "WHERE u.phone_number = $1", phone_number
    );

    // 2. Проверяем, что результат не пустой
    if (result.empty()) 
    {
        throw std::runtime_error("User not found");
    }

    user.fromDb(result[0]);

    auto result_2 = db_->execSqlSync
    (
        "SELECT r.role_type FROM users u" 
        "JOIN users_roles u_r ON u.id = u_r.user_id"
        "JOIN roles r ON u_r.role_id = r.id"
        "WHERE u.phone_number = $1", phone_number
    );
    std::list<std::string> role_type;
    for (int i = 0; result_2.size(); i++)
    {
        role_type.push_back(result_2[i]["role_type"].as<std::string>());
    }
    user.setRoles(role_type);
    return user;
}

std::list<UserData> UserRepository::getUsers() 
{
    auto result = db_->execSqlSync
    (
        "SELECT u.id, u.phone_number, u.password, u.name, u.last_name, u.surname, u.document, r.role_type FROM users u"
        "JOIN users_roles u_r ON u.id = u_r.user_id"
        "JOIN roles r ON u_r.role_id = r.id"
    );
    std::list<UserData> users;
    
    for (int i=0; i < result.size(); i++)
    {
        std::cout << i;
        UserData user;
        user.fromDb(result[i]);
        users.push_back(user);
    }
    return users;
}

UserData UserRepository::getUser(int id)
{
    try {
        auto result = db_->execSqlSync(
            "SELECT u.id, u.phone_number, u.password, u.name, u.last_name, u.surname, u.document, r.role_type FROM users u"
            "JOIN users_roles u_r ON u.id = u_r.user_id"
            "JOIN roles r ON u_r.role_id = r.id"
            "WHERE id = $1", id
        );
        
        if (result.empty()) {
            throw std::runtime_error("User not found");
        }

        UserData user;
        user.fromDb(result[0]);
        return user;
    }
    catch (const std::exception& e) {
        throw std::runtime_error("Database error: " + std::string(e.what()));
    }
}

void UserRepository::deleteUser(int id)
{
try 
{
    db_->execSqlSync
    (
        "DELETE FROM users_roles"
        "WHERE user_id = $1", id
    );
    db_->execSqlSync
    (
        "DELETE FROM users"
        "WHERE id = $1", id
    );
    db_->execSqlSync
    (
        "Delete FROM users "
        "WHERE id = $1", id
    );
}
catch (const std::exception& e)
{
    throw std::runtime_error("Database error: " + std::string(e.what()));
}
}

void UserRepository::addRole(int user_id, int role_id) 
{
    db_->execSqlSync
    (
        "INSERT INTO users_roles (user_id, role_id)" 
        "VALUES ($1, $2) ", user_id, role_id
    );
}
