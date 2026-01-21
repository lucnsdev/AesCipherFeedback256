cmake_minimum_required(VERSION 4.1)
project(Aes_CipherFeedback256)

set(CMAKE_CXX_STANDARD 20)

add_executable(Aes_CipherFeedback256 main.cpp
        aes_cfb.cpp
)
