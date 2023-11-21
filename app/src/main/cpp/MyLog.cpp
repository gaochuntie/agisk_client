//
// Created by jackmaxpale on 2022/10/23.
//

#include "MyLog.h"
#include <string>
#include <iostream>
#include <fstream>
#include <ctime>
#include <android/log.h>
#include <iomanip>
#include <chrono>
#include <sstream>


using namespace std;
 int appendBaseLog(string pathSs, string log_file) {
    ofstream of;
    of.open(pathSs.c_str(), ios_base::app);
    of << log_file << endl;
    of.flush();
    of.close();
    return 0;
}

 int appendLogCutLine(string pathSs,string tag){
    ofstream of;
    of.open(pathSs.c_str(), ios_base::app);
    of << "--------" <<tag<<"--------"<< endl;
     std::string formattedDateTime = NowTime();
    of << "####[" << formattedDateTime << "]" << endl;
    of.flush();
    of.close();
    return 0;
}
 int appendDebugLog(string logss){
    ofstream of;
    of.open(DEBUG_LOG, ios_base::app);
    of << logss << endl;
    of.flush();
    of.close();
    return 0;
}

std::string NowTime() {
    // Get the current time point
    auto currentTime = std::chrono::system_clock::now();

    // Convert the time point to a time_t object
    std::time_t currentTime_t = std::chrono::system_clock::to_time_t(currentTime);

    // Convert the time_t object to a tm struct
    std::tm* currentTM = std::localtime(&currentTime_t);

    // Format the current date and time
    std::ostringstream oss;
    oss << std::put_time(currentTM, "%Y-%m-%d %H:%M:%S");

    // Return the formatted string
    return oss.str();
}