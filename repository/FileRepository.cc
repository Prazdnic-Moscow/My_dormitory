#include "FileRepository.h"
// Создать пользователя в БД
    File FileRepository::createFile(
        const std::string body, 
        const std::string file_path
    )
    {
        auto result = db_->execSqlSync(
            "INSERT INTO files (body, file_path) "
            "VALUES ($1, $2) "
            "RETURNING id, body, file_path ", body, file_path
        );
        File file;
        file.fromDb(result[0]);
        return file;
    }
    
    // Удаление
    bool FileRepository::deleteFile(int id_file)
    {
        //для начала нужно удалить фотку из minio
        S3Service s3service("mydormitory");
        auto result = db_->execSqlSync
        (
            "SELECT * FROM files "
            "WHERE id = $1 ", id_file
        );
        File file;
        file.fromDb(result[0]);
        std::string file_path = file.getFilePath();
        s3service.deleteFile(file_path);

        auto result_2 = db_->execSqlSync
        (
            "DELETE FROM files "
            "WHERE id = $1 ", id_file
        );
        
        if (result_2.affectedRows() == 0)
        {
            return false;
        }
        return true;
    }

    std::list<File> FileRepository::getFiles()
    {
        try
        {
            auto result = db_->execSqlSync(
            "SELECT * FROM files "
            );
            std::list<File> files_all;
            for (int i = 0; i < result.size(); i++)
            {
                File file;
                file.fromDb(result[i]);
                files_all.push_back(file);
            }
            return files_all;
        }
        catch(const std::exception& e)
        {
            std::cerr << e.what() << '\n';
        }
    }