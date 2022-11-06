//
// Created by jackmaxpale on 2022/10/23.
//
#include <string>
#ifndef MY_APPLICATION_C_MYLOG_H
#define MY_APPLICATION_C_MYLOG_H
using namespace std;

#define DISKACTION_LOG "/sdcard/agisk/disk.log"
#define PARTITION_DUMP_LOG "/sdcard/agisk/partdump.log"
#define PARTITION_LOG "/sdcard/agisk/partition.log"
#define SERIOUS_ERROR_LOG "/sdcard/agisk/serious_error.log"
#define GLOBAL_OUTPUT "/sdcard/agisk/output.data"

int appendBaseLog(string pathSs, string logss) ;

int appendLogCutLine(string pathSs,string tag);
#endif //MY_APPLICATION_C_MYLOG_H
