#include "UserRepository.h"
// Реализация метода создания пользователя
UserData UserRepository::createUser(const std::string &phone_number, 
                                    const std::string &passwordHash,
                                    const std::string &name,
                                    const std::string &last_name,
                                    const std::string &surname,
                                    const std::list<std::string> &document)
{
    auto trans = db_->newTransaction();
    // 1. Создаём пользователя
    auto result = trans->execSqlSync
    (
        "INSERT INTO users (phone_number, password, name, last_name, surname) "
        "VALUES ($1, $2, $3, $4, $5) "
        "RETURNING id, phone_number, password, name, last_name, surname",
        phone_number, passwordHash, name, last_name, surname
    );
    UserData user;
    user.fromDb(result[0]);
    int user_id = user.getId();
    
    if (!document.empty())
    {
        for (auto doc : document)
        {
            auto result_2 = trans->execSqlSync
            (
                "INSERT INTO user_file (user_id, file_path) "
                "VALUES ($1, $2) ",
                user_id, doc
            );
        }
    }
    user.setDocuments(document);
    // 2. Назначаем роли
    std::vector<int> roleIds = {1, 5, 8, 10, 12};
    std::vector<std::string> roleNames = {"news_read", "user_read", "tutor_read", "file_read", "wash_machine_read"};

    for (int roleId : roleIds) 
    {
        trans->execSqlSync
        (
            "INSERT INTO users_roles (user_id, role_id) "
            "VALUES ($1, $2)",
            user.getId(), roleId
        );
    }
    std::list<std::string> roles(roleNames.begin(), roleNames.end());
    user.setRoles(roles);
    return user;
}

bool UserRepository::checkUserExists(const std::string &phone_number)
{
    auto result = db_->execSqlSync
    (
        "SELECT * FROM users "
        "WHERE phone_number = $1", 
        phone_number
    );
    return !result.empty();
}

UserData UserRepository::getUserByPhone(const std::string &phone_number) 
{
    auto tran = db_->newTransaction();
    UserData user;
    auto result = tran->execSqlSync
    (
        "SELECT * FROM users u "
        "WHERE u.phone_number = $1 ", 
        phone_number
    );

    // 2. Проверяем, что результат не пустой
    if (result.empty()) 
    {
        throw std::runtime_error("User not found");
    }

    user.fromDb(result[0]);
    auto result_2 = tran->execSqlSync
    (
        "SELECT r.role_type FROM users u " 
        "JOIN users_roles u_r ON u.id = u_r.user_id "
        "JOIN roles r ON u_r.role_id = r.id "
        "WHERE u.phone_number = $1", 
        phone_number
    );
    
    std::list<std::string> role_type;
    for (int i = 0; i < result_2.size(); i++)
    {
        role_type.push_back(result_2[i]["role_type"].as<std::string>());
    }
    user.setRoles(role_type);

    auto result_3 = tran->execSqlSync
    (
        "SELECT u_f.file_path FROM users u " 
        "JOIN user_file u_f ON u.id = u_f.user_id "
        "WHERE u.phone_number = $1", 
        phone_number
    );

    std::list<std::string> file_path;
    for (int i = 0; i < result_3.size(); i++)
    {
        file_path.push_back(result_3[i]["file_path"].as<std::string>());
    }
    user.setDocuments(file_path);
    return user;
}

std::list<UserData> UserRepository::getUsers() 
{
    auto tran = db_->newTransaction();
    auto result = tran->execSqlSync
    (
        "SELECT * FROM users "
    );
    std::list<UserData> users;
    
    for (int i=0; i < result.size(); i++)
    {
        UserData user;
        user.fromDb(result[i]);
        int user_id = user.getId();
        auto result_2 = tran->execSqlSync
        (
            "SELECT r.role_type FROM users u " 
            "JOIN users_roles u_r ON u.id = u_r.user_id "
            "JOIN roles r ON u_r.role_id = r.id "
            "WHERE u.id = $1", 
            user_id
        );
        std::list<std::string> role_type;
        for (int i = 0; i < result_2.size(); i++)
        {
            role_type.push_back(result_2[i]["role_type"].as<std::string>());
        }
        user.setRoles(role_type);

        auto result_3 = tran->execSqlSync
        (
            "SELECT u_f.file_path FROM users u " 
            "JOIN user_file u_f ON u.id = u_f.user_id "
            "WHERE u.id = $1", 
            user_id
        );
        std::list<std::string> file_path;
        for (int i = 0; i < result_3.size(); i++)
        {
            file_path.push_back(result_3[i]["file_path"].as<std::string>());
        }
        user.setDocuments(file_path);
        users.push_back(user);
    }
    return users;
}

UserData UserRepository::getUser(int id)
{
    auto tran = db_->newTransaction();
    auto result = tran->execSqlSync
    (
        "SELECT * FROM users "
        "WHERE id = $1", 
        id
    );
    
    if (result.empty()) 
    {
        throw std::runtime_error("User not found");
    }

    UserData user;
    user.fromDb(result[0]);

    auto result_2 = tran->execSqlSync
    (
        "SELECT r.role_type FROM users u " 
        "JOIN users_roles u_r ON u.id = u_r.user_id "
        "JOIN roles r ON u_r.role_id = r.id "
        "WHERE u.id = $1", 
        id
    );

    std::list<std::string> role_type;
    for (int i=0; i < result_2.size(); i++)
    {
        role_type.push_back(result_2[i]["role_type"].as<std::string>());
    }
    user.setRoles(role_type);
    
    
    auto result_3 = tran->execSqlSync
    (
        "SELECT u_f.file_path FROM users u " 
        "JOIN user_file u_f ON u.id = u_f.user_id "
        "WHERE u.id = $1", 
        id
    );
    std::list<std::string> file_path;
    for (int i = 0; i < result_3.size(); i++)
    {
        file_path.push_back(result_3[i]["file_path"].as<std::string>());
    }
    user.setDocuments(file_path);
    
    return user;
}

bool UserRepository::deleteUser(int id)
{
    auto tran = db_->newTransaction();
    //для начала нужно удалить фотку из minio
    S3Service s3service("mydormitory");
    // 1. Получаем все изображения для удаления из MinIO
    auto files_result = tran->execSqlSync
    (
        "SELECT file_path FROM user_file " 
        "WHERE user_id = $1",
        id
    );
    
    for (auto num : files_result)
    {
        std::string file_path = num["file_path"].as<std::string>();
        s3service.deleteFile(file_path);
    }

    tran->execSqlSync
    (
        "DELETE FROM users_roles "
        "WHERE user_id = $1 ", 
        id
    );
    
    tran->execSqlSync
    (
        "DELETE FROM user_file "
        "WHERE user_id = $1 ", 
        id
    );
    
    auto result = tran->execSqlSync
    (
        "Delete FROM users "
        "WHERE id = $1 ", 
        id
    );
    
    if (result.affectedRows() == 0)
    {
        return false;
    }
    return true;
}


void UserRepository::addRole(int user_id, 
                             int role_id) 
{
    auto result = db_->execSqlSync
    (
        "INSERT INTO users_roles (user_id, role_id) " 
        "VALUES ($1, $2) ", 
        user_id, role_id
    );
}

bool UserRepository::deleteRole(int user_id, 
                                int role_id)
{
    auto result = db_->execSqlSync
    (
        "DELETE FROM users_roles " 
        "WHERE user_id = $1 AND role_id = $2", 
        user_id, role_id
    );
    return !result.affectedRows() == 0;
}
