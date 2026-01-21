// Developed by @lucns

#ifndef AES_CFB_H
#define AES_CFB_H

void initialize(const char *key, int leyLength, const char *iv);
char* encrypt(char *input, long inputLength);
char* decrypt(char *input, long inputLength);

#endif //AES_CFB_H