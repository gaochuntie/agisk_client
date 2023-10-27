//
// Created by jackmaxpale on 2023/9/20.
//

#include <fstream>
#include "SingleTools.h"
#include "include/gpt/gpt.h"
#include "include/gpt/gptpart.h"
#include "include/gpt/parttypes.h"
#include "des/encrypt.h"
#include "tinyxml2/tinyxml2.h"
/**
 *
 * @param driver
 * @param code
 * @param name
 * @param number
 * @return 1 failed
 */
int formatPartition(string driver,string code,string name,uint64_t number){
//TODO implement filesystem support
    /**
     * format new created partition to given filesystem
     * args:
     * string code --the filesystem code
     * string drive_s  --the disk driver
     * string name_s --the partition name
     * uint64 number --the newly created partition number,0 start,not 1,so this is the actrue number
     * eg: /dev/block/sda 0x8300 new_part1 32
     * so the partition file is /dev/block/sda33
     */

    //failed
    return 1;
}

/**
 * decrypt a encrypted string using des
 * @param en_content is the encrypted string
 * @param key is the des key
 * @return decrypted string
 */
string doDecryptString(string en_content,string key){
    string dec = decrypt(en_content, key);
    return dec;
}

/**
 * like above
 * @param de_content
 * @param key
 * @return
 */
string doEncryptString(string de_content,string key) {
    string enc = encrypt(de_content, key);
    return enc;
}
///////

string doEncryptAgiskSubXml(string de_content,string o_key,int flag,string sn){

    //parse action node
    string action_content = ExtractActionNodeContent(de_content.c_str());
    tinyxml2::XMLDocument xmlDoc;
    xmlDoc.Parse(de_content.c_str());
    string key=o_key;
    //parse key
    switch (flag) {
        case 0:
            break;
        case 1:
            key = doEncryptString(o_key, sn);
            break;
    }
    ReplaceActionWithString(xmlDoc, doEncryptString(action_content, key));
    tinyxml2::XMLPrinter printer;
    xmlDoc.Print(&printer);
    return printer.CStr();
}
string doDecryptAgiskSubXml(string en_content,string o_key,int flag,string sn){
    //parse action node
    string action_content = ExtractActionNodeContent(en_content.c_str());
    tinyxml2::XMLDocument xmlDoc;
    xmlDoc.Parse(en_content.c_str());
    string key=o_key;
    //parse key
    switch (flag) {
        case 0:
            break;
        case 1:
            key = doDecryptString(o_key, sn);
            break;
    }
    ReplaceActionWithString(xmlDoc, doDecryptString(action_content, key));
    tinyxml2::XMLPrinter printer;
    xmlDoc.Print(&printer);
    return printer.CStr();
}

std::string ExtractActionNodeContent(const char* xmlString) {
    tinyxml2::XMLDocument xmlDoc;
    tinyxml2::XMLError parseResult = xmlDoc.Parse(xmlString);

    if (parseResult == tinyxml2::XML_SUCCESS) {
        tinyxml2::XMLElement* rootElement = xmlDoc.FirstChildElement();
        if (rootElement) {
            // Assuming there's only one <Action> node, if there can be multiple, you may need to iterate through them.
            tinyxml2::XMLElement* actionElement = rootElement->FirstChildElement("Action");
            if (actionElement) {
                tinyxml2::XMLPrinter printer;
                actionElement->Accept(&printer);
                return printer.CStr();
            }
        }
    }

    // Return an empty string or handle errors as needed.
    return "";
}
void ReplaceActionWithString(tinyxml2::XMLDocument& xmlDoc,string subcontent) {
    tinyxml2::XMLElement* rootElement = xmlDoc.FirstChildElement("config");
    if (rootElement) {
        tinyxml2::XMLElement* actionElement = rootElement->FirstChildElement("Action");
        if (actionElement) {
            // Replace the content of <Action> with the encrypted string.
            actionElement->SetText(subcontent.c_str());
        }
    }
}

int WriteToFile(string &content, string &dest){
    std::ofstream file(dest, std::ios::out);

    if (file.is_open()) {
        file << content;
        file.close();
        return 0; // Success
    } else {
        std::cerr << "Error: Could not open the file for writing." << std::endl;
        return 1; // An error occurred
    }
}