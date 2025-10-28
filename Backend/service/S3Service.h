#pragma once
#include <string>
#include <vector>
#include <cstdint>
#include "Util.h"
#include <iostream>
#include <Aws.h>
#include <S3Client.h>
#include <PutObjectRequest.h>
#include <HeadObjectRequest.h>
#include <AWSStringStream.h>
#include <AWSCredentials.h>
#include <DeleteObjectRequest.h>
#include <GetObjectRequest.h>

class S3Service 
{
    public:
        S3Service(const std::string& bucket, 
                const std::string& endpoint = "http://localhost:9000",
                const std::string& accessKey = "minioadmin",
                const std::string& secretKey = "minioadmin");
        
        bool uploadFile(const std::string file_path, 
                        const std::vector<uint8_t> data, 
                        const std::string contentType);
        
        bool deleteFile(const std::string& key);
        
        std::pair<std::vector<uint8_t>, std::string> downloadFile(const std::string& image_path);

    private:
        std::string bucketName; // Имя бакета (папки) в MinIO/S3
        std::string url; // URL MinIO сервера (http://localhost:9000)
        std::string key;// Ключ доступа (логин)
        std::string secretkey; // Секретный ключ (пароль)
        std::shared_ptr<Aws::S3::S3Client> s3Client;
};