/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-30 20:06:58
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-05-03 20:50:39
 * @Description: Utils for atms
 */
#ifndef __UTILS_ATMS__
#define __UTILS_ATMS__

#include <stdlib.h>
#include "../include/gpt/gpt.h"

#define MAXHOLD 128
extern uint32_t atmsMetedataSizeBlock;

void showHelp();

uint32_t readLineList(string **, char *);

void trimall(char *s);

#endif