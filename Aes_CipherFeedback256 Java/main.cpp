#include <fstream>
#include <filesystem>
#include "aes_cfb.h"

using namespace std;

long getFileLength(const char *path) {
    try {
        return filesystem::file_size(path);
    } catch (filesystem::filesystem_error const &ex) {
        printf("Error while retrieving file size: %s. Error: %s\n", path, ex.what());
    }
    return 0;
}

int main() {
    printf("main\n");

    const char *videoPath = R"(C:\Users\lucns\OneDrive\Documentos\CryptTest\hello.txt)";
    const char *encryptedPath = R"(C:\Users\lucns\OneDrive\Documentos\CryptTest\encrypted.txt)";
    const char *decryptedPath = R"(C:\Users\lucns\OneDrive\Documentos\CryptTest\decrypted.txt)";

    long fLength = getFileLength(videoPath);
    printf("Initial file length: %ld\n", fLength);

    ifstream inStream(videoPath, ios::binary);
    if (!inStream.is_open()) {
        printf("File open failure for %s", videoPath);
        return 0;
    }
    char *buffer = new char[fLength];
    streambuf *streamBuffer = inStream.rdbuf();
    //streamsize size = streamBuffer->pubseekoff(0, ifstream::end);
    //streamBuffer->pubseekoff(0, ifstream::beg);
    streamBuffer->sgetn(buffer, fLength);
    inStream.close();
    // printf("File content: %s\n", buffer);

    const char *key_16 = "Lucas@0123456789"; // 16 bytes or 128 bit key
    const char *key_24 = "Lucas@0123456789Lucas@12"; // 24 bytes or 192 bit key
    const char *key_32 = "Lucas@0123456789Lucas@0123456789"; // 32 bytes or 256 bit key
    const char *initVector = "0123456789@Lucas"; // 16 bytes IV

    initialize(key_32, 16, initVector);
    char *out = encrypt(buffer, fLength);
    printf("Encrypt complete.\n");

    ofstream outputEncrypted(encryptedPath, ios::out | ios::binary);
    if (!outputEncrypted) {
        printf("ofstream open failure for initial input file.\n");
        delete[] buffer;
        delete[] out;
        return 0;
    }
    outputEncrypted.write(out, fLength);
    outputEncrypted.flush();
    outputEncrypted.close();
    printf("Encrypted file saved.");

    //delete[] buffer;
    //delete[] out;

    // ------------------------------

    ifstream inStream2(encryptedPath, ios::binary);
    if (!inStream2.is_open()) {
        printf("File open failure for encrypted file.");
        return 0;
    }
    streambuf *streamBuffer2 = inStream2.rdbuf();
    //streamsize size2 = streamBuffer2->pubseekoff(0, ifstream::end);
    //streamBuffe2r->pubseekoff(0, ifstream::beg);
    streamBuffer2->sgetn(buffer, fLength);
    inStream2.close();

    initialize(key_32, 16, initVector);
    out = decrypt(buffer, fLength);

    ofstream outputDecrypted(decryptedPath, ios::out | ios::binary);
    if (!outputDecrypted) {
        printf("ofstream open failure for final output file.\n");
        return 0;
    }
    outputDecrypted.write(out, fLength);
    outputDecrypted.close();
    printf("Decrypt complete.\n\n");

    fLength = getFileLength(decryptedPath);
    printf("Final file decrypted length: %ld\n", fLength);

    delete[] buffer;
    delete[] out;

    return 0;
}
