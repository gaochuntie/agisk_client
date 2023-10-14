//
// Created by jackmaxpale on 2023/9/20.
//

#ifndef MY_APPLICATION_C_SINGLETOOLS_H
#define MY_APPLICATION_C_SINGLETOOLS_H

#include <iostream>
#include <string>

using namespace std;
int formatPartition(string driver,string code,string name,uint64_t number);




string doDecryptString(string en_content,string key);
string doEncryptString(string de_content,string key);




string doEncryptAgiskSubXml(string de_content,string key,int flag);
string doDecryptAgiskSubXml(string en_content,string key,int flag);
#endif //MY_APPLICATION_C_SINGLETOOLS_H

