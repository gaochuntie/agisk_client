/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-09 22:01:20
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-05-03 21:51:25
 * @Description: adjustion class
 */

#ifndef __ADJUSTION__
#define __ADJUSRION__


#include "../include/gpt/gpt.h"

struct partition_list {
    /* data */
    //partition
    GPTPart partition_current;
    //next
    partition_list *next = nullptr;
    //prev
    partition_list *prev = nullptr;

    //partition num
    uint32_t partnum = -1;

    partition_list(GPTPart part, uint32_t partnumber) : partition_current(part),
                                                        partnum(partnumber) {};
};

class Adjustion {
private:
    /* data */
    string result;
    int ret = 0;
    GPTData gptdata;
public:
    Adjustion(GPTData &gptdata);

    ~Adjustion();

    int retValue(void);

    string &getResult(void);

    int delete_pt(uint32_t part_num);

    int new_pt(char *name, uint64_t start_sector, uint64_t size_sector);

    int change_name_pt(uint32_t num, char *new_name);

    int auto_clone(partition_list *list);

    int operator==(int);
};


#endif
