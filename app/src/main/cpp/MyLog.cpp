//
// Created by jackmaxpale on 2022/10/23.
//

#include "MyLog.h"
#include <string>
#include <iostream>
#include <fstream>


using namespace std;
extern int appendBaseLog(string pathSs, string log_file) {
    ofstream of;
    of.open(pathSs.c_str(), ios_base::app);
    of << log_file << endl;
    of.flush();
    of.close();
    return 0;
}