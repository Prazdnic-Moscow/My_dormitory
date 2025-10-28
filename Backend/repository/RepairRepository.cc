#include "RepairRepository.h"
// Создать пользователя в БД
    Repair RepairRepository::createRepair(const std::string type, 
                                          const std::string body, 
                                          std::list<std::string> repair_paths)
{
    auto transaction = db_->newTransaction();
    // Создаем запись файла
    auto result = transaction->execSqlSync
    (
        "INSERT INTO repair (type, body) "
        "VALUES ($1, $2) "
        "RETURNING id, type, body, date",
        type, body
    );
    
    Repair repair;
    repair.fromDb(result[0]);
    int repair_id = repair.getId();
    
    // Добавляем файлы
    if (!repair_paths.empty()) 
    {
        for (const auto& repair_path : repair_paths) 
        {
            transaction->execSqlSync
            (
                "INSERT INTO repair_file (repair_id, image_path) "
                "VALUES ($1, $2)",
                repair_id, repair_path
            );
        }
    }

    repair.setRepairPaths(repair_paths);
    return repair;
}
    
// Удаление
bool RepairRepository::deleteRepair(int id_repair)
{
    auto trans = db_->newTransaction();
    // Удаляем файлы из MinIO
    S3Service s3service("mydormitory");
    auto files_result = trans->execSqlSync
    (
        "SELECT image_path FROM repair_file "
        "WHERE repair_id = $1",
        id_repair
    );
    
    for (const auto& row : files_result) 
    {
        std::string repair_path = row["image_path"].as<std::string>();
        s3service.deleteFile(repair_path);
    }
    
    // Удаляем файлы из БД
    trans->execSqlSync
    (
        "DELETE FROM repair_file "
        "WHERE repair_id = $1",
        id_repair
    );
    
    // Удаляем запись файла
    auto result = trans->execSqlSync
    (
        "DELETE FROM repair "
        "WHERE id = $1",
        id_repair
    );
    return result.affectedRows() > 0;
}

std::list<Repair> RepairRepository::getRepairs()
{
    auto tran = db_->newTransaction();
    auto result = tran->execSqlSync
    (
        "SELECT * FROM repair "
        "ORDER BY date DESC "
    );
    
    std::list<Repair> repair_all;
    for (const auto& row : result) 
    {
        Repair repair;
        repair.fromDb(row);
        int repair_id = repair.getId();
        // Получаем файлы
        auto files_result = tran->execSqlSync
        (
            "SELECT image_path FROM repair_file "
            "WHERE repair_id = $1 "
            "ORDER BY repair_id",
            repair_id
        );
        
        std::list<std::string> repair_paths;
        for (const auto& file_row : files_result) 
        {
            repair_paths.push_back(file_row["image_path"].as<std::string>());
        }
        repair.setRepairPaths(repair_paths);
        
        repair_all.push_back(repair);
    }
    return repair_all;
}