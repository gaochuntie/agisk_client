#include "b64.h"
#include "vigenere.h"
#include <string>
using namespace std;
std::string encrypt(std::string& msg, std::string& key) ;
std::string decrypt(std::string& encrypted_msg, std::string& key) ;