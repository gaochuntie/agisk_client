//
// Created by jackmaxpale on 2023/9/20.
//

#ifndef MY_APPLICATION_C_SINGLETOOLS_H
#define MY_APPLICATION_C_SINGLETOOLS_H

#include <iostream>
#include <string>
#include "tinyxml2/tinyxml2.h"

using namespace std;

int formatPartition(string driver, string code, string name, uint64_t number);


string doDecryptString(string en_content, string key);

string doEncryptString(string de_content, string key);


string doEncryptAgiskSubXml(string de_content, string key, int flag, string sn);

string doDecryptAgiskSubXml(string en_content, string key, int flag, string sn);


std::string ExtractActionNodeContent(const char *xmlString);

void ReplaceActionWithString(tinyxml2::XMLDocument &xmlDoc, string en_subcontent,int isStringXmlFormat);

int WriteToFile(string &content, string &dest);

#endif //MY_APPLICATION_C_SINGLETOOLS_H

