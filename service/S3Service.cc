#include "S3Service.h"

S3Service::S3Service(const std::string& bucket, 
                     const std::string& endpoint,
                     const std::string& accessKey,
                     const std::string& secretKey)
{
    bucketName = bucket;
    url = endpoint;
    key = accessKey;
    secretkey = secretKey;
}

bool S3Service::uploadFile(const std::string file_path, const std::vector<uint8_t> data, 
                   const std::string contentType)
{
    if (data.empty())
    {
        throw std::runtime_error("uploadFile: data is empty");
    }
    // Настройка клиента
    Aws::Client::ClientConfiguration config;
    config.endpointOverride = url;
    config.scheme = Aws::Http::Scheme::HTTP;  // Или HTTPS, если у вас настроен SSL
    
    Aws::Auth::AWSCredentials credentials(key, secretkey);
    
    Aws::S3::S3Client s3Client(credentials, config, Aws::Client::AWSAuthV4Signer::PayloadSigningPolicy::Never, false);
    
    // Создание запроса
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(file_path);
    request.SetContentType(contentType);//contentType.c_str()
    request.SetContentLength(data.size());

    // Использование данных из std::vector<uint8_t> для тела запроса
    auto inputData = Aws::MakeShared<Aws::StringStream>("PutObjectInputStream");
    inputData->write(reinterpret_cast<const char*>(data.data()), data.size());

    // Устанавливаем тело запроса как поток
    request.SetBody(inputData);

    // Выполнение запроса
    auto outcome = s3Client.PutObject(request);
    
    if (!outcome.IsSuccess()) 
    {
        throw std::runtime_error("S3 upload failed: " + outcome.GetError().GetMessage());
    }  
    return true;
}

bool S3Service::deleteFile(const std::string& image_path) 
{
    Aws::Client::ClientConfiguration config;
    config.endpointOverride = url;
    config.scheme = Aws::Http::Scheme::HTTP;

    Aws::Auth::AWSCredentials credentials(this->key, this->secretkey);
    Aws::S3::S3Client s3Client(credentials, config, Aws::Client::AWSAuthV4Signer::PayloadSigningPolicy::Never, false);

    Aws::S3::Model::DeleteObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(image_path);
    auto outcome = s3Client.DeleteObject(request);
    if (!outcome.IsSuccess()) 
    {
        throw std::runtime_error("Delete failed: " + outcome.GetError().GetMessage());
    }
    return true;
}

std::pair<std::vector<uint8_t>, std::string> S3Service:: downloadFile(const std::string& image_path)
{
    Aws::Client::ClientConfiguration config;
    config.endpointOverride = url;
    config.scheme = Aws::Http::Scheme::HTTP;

    Aws::Auth::AWSCredentials credentials(this->key, this->secretkey);
    Aws::S3::S3Client s3Client(credentials, config, Aws::Client::AWSAuthV4Signer::PayloadSigningPolicy::Never, false);

    Aws::S3::Model::GetObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(image_path);

    auto outcome = s3Client.GetObject(request);

    if (!outcome.IsSuccess()) 
    {
        throw std::runtime_error(outcome.GetError().GetMessage());
    }

    auto& stream = outcome.GetResultWithOwnership().GetBody();
    std::vector<uint8_t> data((std::istreambuf_iterator<char>(stream)),
                               std::istreambuf_iterator<char>());

    std::string contentType = outcome.GetResult().GetContentType();
    
    return { data, contentType };
}