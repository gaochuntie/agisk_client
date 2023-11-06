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
#include "MyLog.h"
#include <iostream>
#include <string>
#include "include/zip/zip.h"
#include <sys/stat.h>
#include <dirent.h>
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
    appendDebugLog("TO Encrypt String\n" + action_content + "\n-----------");
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
    string en_string=doEncryptString(action_content, key);
    appendDebugLog("Encrypted String\n" + en_string + "\n----------");
    string de_string = doDecryptString(en_string, key);
    appendDebugLog("Decrypted String\n" + de_string + "\n---------");
    ReplaceActionWithString(xmlDoc, en_string,0);
    tinyxml2::XMLPrinter printer;
    xmlDoc.Print(&printer);
    appendDebugLog("Encrypted Xml\n" + string(printer.CStr()) + "\n-----------");
    return printer.CStr();
}
string doDecryptAgiskSubXml(string en_content,string o_key,int flag,string sn){
    //parse action node
    string action_content = ExtractActionNodeContent(en_content.c_str());
    if (action_content.empty()) {
        appendDebugLog("Extra action node failed\n");
    }
    appendDebugLog("Action node:\n" + action_content + "\n------------------\n");
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
    appendDebugLog("Encrypted String\n" + action_content + "\n----------");
    string de_string = doDecryptString(action_content, key);
    ReplaceActionWithString(xmlDoc, de_string,1);
    appendDebugLog("Decrypted String\n" + de_string + "\n----------");
    tinyxml2::XMLPrinter printer;
    xmlDoc.Print(&printer);
    appendDebugLog("Decrypted Xml\n" + string(printer.CStr()) + "\n-----------");
    return printer.CStr();
}

std::string ExtractActionNodeContent(const char* xmlString) {
//    tinyxml2::XMLDocument xmlDoc;
//    tinyxml2::XMLError parseResult = xmlDoc.Parse(xmlString);
//
//    if (parseResult == tinyxml2::XML_SUCCESS) {
//        tinyxml2::XMLElement* rootElement = xmlDoc.FirstChildElement();
//        if (rootElement) {
//            // Assuming there's only one <Action> node, if there can be multiple, you may need to iterate through them.
//            tinyxml2::XMLElement* actionElement = rootElement->FirstChildElement("Action");
//            if (actionElement) {
//                tinyxml2::XMLPrinter printer;
//                actionElement->Accept(&printer);
//                return printer.CStr();
//            }
//        }
//    }
//
//    // Return an empty string or handle errors as needed.
//    return "";
    tinyxml2::XMLDocument xmlDoc;
    tinyxml2::XMLError parseResult = xmlDoc.Parse(xmlString);

    if (parseResult == tinyxml2::XML_SUCCESS) {
        tinyxml2::XMLElement* rootElement = xmlDoc.FirstChildElement();
        if (rootElement) {
            // Assuming there's only one <Action> node, if there can be multiple, you may need to iterate through them.
            tinyxml2::XMLElement* actionElement = rootElement->FirstChildElement("Action");
            if (actionElement) {
                tinyxml2::XMLPrinter printer;

                // Iterate through child nodes (including text and elements) of <Action>.
                for (tinyxml2::XMLNode* child = actionElement->FirstChild(); child; child = child->NextSibling()) {
                    child->Accept(&printer);
                }

                return printer.CStr();
            }
        }
    }

    // Return an empty string or handle errors as needed.
    return "";
}
void ReplaceActionWithString(tinyxml2::XMLDocument& xmlDoc,string subcontent,int isStringXmlFormat) {
    tinyxml2::XMLDocument newContentDoc;
    newContentDoc.Parse(subcontent.c_str());  // Parse the new content
    tinyxml2::XMLElement* rootElement = xmlDoc.FirstChildElement("config");
    if (rootElement) {
        tinyxml2::XMLElement* actionElement = rootElement->FirstChildElement("Action");
        if (actionElement) {
            // Replace the content of <Action> with the encrypted string.
            //\actionElement->SetText(subcontent.c_str());
            actionElement->DeleteChildren();
            if (!isStringXmlFormat) {
                //encrypted string,not a xml format,using setText
                // Create a new text node and set its value to the content
                tinyxml2::XMLText* newText = xmlDoc.NewText(subcontent.c_str());
                actionElement->InsertEndChild(newText);
                return;
            }
            // Copy the content from the new document into the existing document
            tinyxml2::XMLNode* newNode = newContentDoc.FirstChild();
            while (newNode) {
                tinyxml2::XMLNode* newNodeCopy = newNode->DeepClone(&xmlDoc);
                actionElement->InsertEndChild(newNodeCopy);
                newNode = newNode->NextSibling();
            }
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


int addDirToZip(zip_t *archive, const std::string &dirPath, const std::string &baseDir) {
    struct dirent *entry;
    struct stat st;
    char path[PATH_MAX];

    DIR *dir = opendir(dirPath.c_str());
    if (!dir) {
        perror("Error opening directory");
        return -1;
    }

    while ((entry = readdir(dir)) != NULL) {
        if (entry->d_name[0] == '.')
            continue; // Skip hidden files/directories

        snprintf(path, sizeof(path), "%s/%s", dirPath.c_str(), entry->d_name);

        if (lstat(path, &st) == -1) {
            perror("Error getting file information");
            closedir(dir);
            return -1;
        }

        if (S_ISDIR(st.st_mode)) {
            if (addDirToZip(archive, path, baseDir) != 0) {
                closedir(dir);
                return -1;
            }
        } else if (S_ISREG(st.st_mode)) {
            char zipPath[PATH_MAX];
            snprintf(zipPath, sizeof(zipPath), "%s/%s", baseDir.c_str(), path + baseDir.length() + 1);
            zip_source_t *src = zip_source_file(archive, path, 0, 0);
            if (src) {
                zip_file_add(archive, zipPath, src, ZIP_FL_OVERWRITE);
            }
        }
    }

    closedir(dir);
    return 0;
}

int compressDir( std::string &sourceDir,  std::string &destFile) {
    zip_t *archive = zip_open(destFile.c_str(), ZIP_CREATE | ZIP_TRUNCATE, NULL);
    if (!archive) {
        std::cerr << "Error creating the archive" << std::endl;
        return -1;
    }

    if (addDirToZip(archive, sourceDir, sourceDir) != 0) {
        zip_close(archive);
        return -1;
    }

    if (zip_close(archive) == -1) {
        std::cerr << "Error closing the archive" << std::endl;
        return -1;
    }

    return 0;
}
