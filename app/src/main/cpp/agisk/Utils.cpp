/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-30 20:07:58
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-05-03 21:12:53
 * @Description: Utils for atms
 */


#include "Utils.hpp"
#include <iostream>
#include <iosfwd>
#include <cstring>
#include <fstream>
#include <sstream>

using namespace std;
uint32_t atmsMetedataSizeBlock = 0;

void showHelp(void) {
    cout << " agisk   --> gaochuntie@Github" << endl
         << " Usage: Pattern1 : agisk -om [there is the same with sgdisk ]" << endl
         << "        this act as the same with sgdisk" << endl
         << "        example: agisk -om /dev/block/sda --print" << endl
         << "" << endl
         << "        Pattern2 : agisk [action/action list file] -f device" << endl
         << "        this act atms specified functions : " << endl
         << "" << endl;
}

uint32_t readLineList(string **list, char *file) {

    ifstream inFile(file);

    std::string lineStr; // 文件中的一行数据

    uint32_t number = 0;

    //
    if (inFile) // 有该文件  
    {
        while (getline(inFile, lineStr)) // line中不包括每行的换行符  
        {
            // string转char *
            //
            char *lineCharArray;
            const int len = lineStr.length();
            lineCharArray = new char[len + 1];
            strcpy(lineCharArray, lineStr.c_str());
            trimall(lineCharArray);
            if (strcmp(lineCharArray, "")) {
                list[number] = new string(lineCharArray);
                if (number >= MAXHOLD) {
                    cout << "E:We can only hold " << MAXHOLD << " clones" << endl;
                    delete[] lineCharArray;
                    return number;
                }
                number++;
            }
            delete[] lineCharArray;

        }
        return number;
    }
    cout << "E:File not accessable : " << file << endl;
    return 0;

}

void trimall(char *s) {
    int l = strlen(s);
    char ctp[128];
    char *tp = ctp;
    char *cs = s;
    while (*s) {
        if (*s != ' ') {
            *tp = *s;
            tp++;
        }
        s++;
    }
    *tp = '\0';
    strcpy(cs, ctp);
}