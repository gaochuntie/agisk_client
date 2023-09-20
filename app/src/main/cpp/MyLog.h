//
// Created by jackmaxpale on 2022/10/23.
//
#include <string>
#ifndef MY_APPLICATION_C_MYLOG_H
#define MY_APPLICATION_C_MYLOG_H
using namespace std;

#define DISKACTION_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/disk.log"
#define PARTITION_DUMP_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/partdump.log"
#define PARTITION_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/partition.log"
#define GLOBAL_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/main.log"
#define SERIOUS_ERROR_LOG "/sdcard/Android/data/atms.app.agiskclient/files/log/serious_error.log"
#define GLOBAL_OUTPUT "/sdcard/Android/data/atms.app.agiskclient/files/log/output.data"

int appendBaseLog(string pathSs, string logss) ;

int appendLogCutLine(string pathSs,string tag);
string NowTime();
#endif //MY_APPLICATION_C_MYLOG_H
