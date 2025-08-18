cmake_minimum_required(VERSION 3.16)
project(myapp LANGUAGES CXX)

set(CMAKE_TOOLCHAIN_FILE "$ENV{HOME}/vcpkg/scripts/buildsystems/vcpkg.cmake" CACHE STRING "Vcpkg toolchain file")
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Один раз ищем
find_package(Drogon REQUIRED COMPONENTS postgres)
find_package(Trantor REQUIRED)
find_package(PostgreSQL REQUIRED)

file(GLOB SOURCES
    "main.cpp"
    "controllers/*.cc"
    "filters/*.cc"
    "plugins/*.cc"
    "models/*.cc"
    "controller/UserController.cc"
    "service/UserService.cc"
    "repository/UserRepository.cc"
    "Util/Util.cc"
)

add_executable(${PROJECT_NAME} ${SOURCES})

# BCrypt
find_path(BCRYPT_INCLUDE_DIR NAMES bcrypt/BCrypt.hpp PATHS /usr/local/include)
find_library(BCRYPT_LIBRARY NAMES bcrypt PATHS /usr/local/lib /usr/lib)
if(BCRYPT_INCLUDE_DIR AND BCRYPT_LIBRARY)
    message(STATUS "Found BCrypt: ${BCRYPT_LIBRARY}")
    target_include_directories(${PROJECT_NAME} PRIVATE ${BCRYPT_INCLUDE_DIR})
    target_link_libraries(${PROJECT_NAME} PRIVATE ${BCRYPT_LIBRARY})
else()
    message(FATAL_ERROR "BCrypt library not found! Install with: sudo apt-get install libbcrypt-dev")
endif()

target_link_libraries(${PROJECT_NAME}
    PRIVATE
    Drogon::Drogon
    Trantor::Trantor
    ${PostgreSQL_LIBRARIES}
)