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
    static std::shared_ptr<Aws::S3::S3Client> cacheClient;
    if (!cacheClient)
    {
        // Настройка клиента
        Aws::Client::ClientConfiguration config;
        config.endpointOverride = url;
        config.scheme = Aws::Http::Scheme::HTTP; // Или HTTPS, если у вас настроен SSL

        Aws::Auth::AWSCredentials credentials(key, secretkey);
        cacheClient = std::make_shared<Aws::S3::S3Client>(
            credentials,
            config,
            Aws::Client::AWSAuthV4Signer::PayloadSigningPolicy::Never,
            false
        );
    }
    s3Client = cacheClient;
}

bool S3Service::uploadFile(const std::string file_path, const std::vector<uint8_t> data, 
                           const std::string contentType)
{
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
    auto outcome = s3Client->PutObject(request);
    
    if (!outcome.IsSuccess()) 
    {
        return false;
    }  
    return true;
}

bool S3Service:: deleteFile(const std::string& image_path) 
{
    Aws::S3::Model::HeadObjectRequest headRequest;
    headRequest.SetBucket(bucketName);
    headRequest.SetKey(image_path);
    Aws::S3::Model::DeleteObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(image_path);


    auto headOutcome = s3Client->HeadObject(headRequest);
    if (!headOutcome.IsSuccess()) 
    {
        if (headOutcome.GetError().GetErrorType() == Aws::S3::S3Errors::RESOURCE_NOT_FOUND) 
        {
            return true;
        }
        return false;
    }

    auto outcome = s3Client->DeleteObject(request);
    if (!outcome.IsSuccess()) 
    {
        return false;
    }
    return true;
}

std::pair<std::vector<uint8_t>, std::string> S3Service:: downloadFile(const std::string& image_path)
{
    Aws::S3::Model::GetObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(image_path);

    auto outcome = s3Client->GetObject(request);
    auto& stream = outcome.GetResultWithOwnership().GetBody();
    std::vector<uint8_t> data((std::istreambuf_iterator<char>(stream)),
                               std::istreambuf_iterator<char>());

    std::string contentType = outcome.GetResult().GetContentType();
    return { data, contentType };
}