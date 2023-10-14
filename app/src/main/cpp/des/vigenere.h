#ifndef __VIGENERE__
#define __VIGENERE__
#include <string>
using namespace std;
int index(char c) ;
std::string extend_key(std::string& msg, std::string& key) ;
std::string encrypt_vigenere(std::string& msg, std::string& key) ;
std::string decrypt_vigenere(std::string& encryptedMsg, std::string& newKey) ;


#endif // !__VIGENERE__

