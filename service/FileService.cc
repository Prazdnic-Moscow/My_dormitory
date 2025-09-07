#include "FileService.h"
// Конструктор
FileService::FileService(const drogon::orm::DbClientPtr& dbClient)
{
    repository = std::make_shared<FileRepository>(dbClient);
}
File FileService::createFile(std::string body,
                             std::string date,
                             std::list<std::string> file_path)
{
    return repository->createFile(body, date, file_path);
}


bool FileService::deleteFile(int id_file)
{
    return repository->deleteFile(id_file);
}

std::list<File> FileService::getFiles()
{
    return repository->getFiles();
}