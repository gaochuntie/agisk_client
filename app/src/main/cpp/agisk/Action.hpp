/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-09 22:10:13
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-04-30 20:04:00
 * @Description: static actions
 */

#ifndef __ACTIONS__
#define __ACTIONS__

#include "../include/gpt/gpt.h"

int write_dev_header(GPTData *header, char *dev);

int write_dev_bin(char *bin, char *dev);

int gen_GPTheader(GPTData *header, FILE *out);

#endif