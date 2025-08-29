#pragma once
#include <string>
#include <vector>
#include <cstdint>
#include "../Util/Util.h"
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/core/utils/memory/stl/AWSStringStream.h>
#include <aws/core/auth/AWSCredentials.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/GetObjectRequest.h>

class S3Service {
public:
    S3Service(const std::string& bucket, 
              const std::string& endpoint = "http://localhost:9000",
              const std::string& accessKey = "minioadmin",
              const std::string& secretKey = "minioadmin");
    
    bool uploadFile(const std::string file_path, const std::vector<uint8_t> data, 
                   const std::string contentType);
    bool deleteFile(const std::string& key);
    std::pair<std::vector<uint8_t>, std::string> downloadFile(const std::string& image_path);

private:
    std::string bucketName; // Имя бакета (папки) в MinIO/S3
    std::string url; // URL MinIO сервера (http://localhost:9000)
    std::string key;// Ключ доступа (логин)
    std::string secretkey; // Секретный ключ (пароль)
};