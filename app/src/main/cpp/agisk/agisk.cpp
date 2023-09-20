/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-04 13:29:17
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-05-03 21:50:53
 * @Description: get gpt info
 */
#include "gpt/gpt.h"
#include "gpt/gptpart.h"
#include <stdio.h>
#include <iostream>
#include "Utils.hpp"
#include <cstring>
#include "Adjustion.hpp"

using namespace std;

//#define MAXHOLD 128

void exit_safe(void);

//Below maintain a single partition_list list
partition_list *list_head = nullptr;

partition_list *initPartitionList();

partition_list *insertItem(partition_list *at);

partition_list *deleteItem(partition_list *which);

partition_list *addItem(partition_list *what);

string *ClonePartList[MAXHOLD];
uint32_t clone_number = 0;
char *devicename = nullptr;
char *commandfile = nullptr;

int main(int argc, char *argv[]) {

    ClonePartList[0] = nullptr;

    //variables verify
    if (argc < 2 | argc > 4) {
        showHelp();
        exit(1);

    }
    for (size_t i = 1; i < argc; i++) {
        if (!strcmp(argv[i], "-f") && i < argc - 1) {

            commandfile = argv[i + 1];

            if (i == 1) {

                devicename = argv[i + 2];

            } else {

                devicename = argv[i - 1];

            }

        }
    }


    //read file

    clone_number = readLineList(ClonePartList, commandfile);




    ///////////////////////////////////////////
    GPTData gptdata(devicename);

    ////////////////////////////////////////////
    gptdata.JustLooking();

    uint32_t MainHeaderLBA = gptdata.GetMainHeaderLBA();
    uint32_t BlockSize = gptdata.GetBlockSize();

    uint32_t num = gptdata.GetNumParts();



    //Declare datas

    uint32_t low, high;
    uint32_t NumParts = gptdata.GetNumParts();
    uint32_t CountParts = gptdata.CountParts();
    uint32_t sector_size = gptdata.GetBlockSize();

    // A 4mib area reserved for atms
    // this area take up a partnum if possible
    // If you want to have some partitions cloned more than 1 time,this area is necessary
    //or it is not requested.

    // Because each clone will hide orig partitions,so We can lost cloned partitions' situation without a recorde area!!!
    //SO it comes with atms metadata
    //Actions about atms metadata are all defined in Action.hpp,You can modify it to your format
    // Also you can resize this area at line below...
    // atmsMetedataSizeBlock defined in Utils.hpp -extern
    atmsMetedataSizeBlock = 4194304 / sector_size;

    gptdata.GetPartRange(&low, &high);


    //

    //show somw useful details
    printf("\nPartition numbers : %d \n", num);
    printf("MainHeaderLBA %d \n", MainHeaderLBA);
    printf("BlockSize %d \n", BlockSize);
    cout << "Exist Parts : " << gptdata.CountParts() << endl;
    cout << "Clone Parts : " << clone_number << endl;



    //this function search all avaliable parts,and set the lowest num to low,the highest num to high,and return available nums

    // printf(" %d \n",);
    // printf(" %d   %d \n",low,high);
    // printf(" %d \n",gptdata.GetNumParts());
    // printf(" %d \n",gptdata.CountParts());
    // printf(" %d \n",gptdata.ValidPartNum(40));
    // printf(" %d \n",gptdata.ValidPartNum(50));
    // printf(" %d \n",gptdata.IsFree(40));
    // printf(" %d \n",gptdata.IsFree(50));
    //return 0;

    ///test area
    cout << endl;

    if (clone_number == 0) {
        cout << "E:Clone list is null." << endl;
        exit_safe();
    }

    using namespace std;

    partition_list *head = initPartitionList();

    for (uint32_t i = low; i <= high; i++) {
        /* code */
        if (gptdata.IsUsedPartNum(i)) {
            //cout<<"Operatoring "<<i<<"      ";
            GPTPart part = gptdata[i];
            //cout<<"     "<<part.GetDescription()<<endl;            
            for (uint32_t j = 0; j < clone_number; j++) {
                /* code */
                if (part.GetDescription() == *(ClonePartList[j])) {
                    std::cout << "Clone : " << i << "(" << i + 1 << ") " << " -Name : "
                              << part.GetDescription() << " -Start : " << part.GetFirstLBA()
                              << " -End : " << part.GetLastLBA() << " -Length : "
                              << part.GetLengthLBA() << endl;

                    partition_list *item = new partition_list(part, i);
                    addItem(item);
                    // cout<<"DD "<<item->prev<<endl;
                    // cout<<"DD "<<item->next<<endl;
                    // cout<<"DD "<<item->partition_current.GetDescription()<<endl<<endl;
                }

            }


        }

    }
    //partition_list *tmp=list_head;

    // while (tmp!=nullptr)
    // {
    //     cout<<"Clone : "<<tmp->partition_current.GetDescription()<<endl;
    //     tmp=tmp->next;
    // }

    Adjustion adjustion(gptdata);
    adjustion.auto_clone(list_head);


    exit_safe();
    //

    //return android_dump(argv[1]);
}


void exit_safe() {

    partition_list *tmp = list_head;
    partition_list *tmp1 = nullptr;
    cout << "Releaseing ..." << endl;

    while (tmp != nullptr) {
        //cout<<"Delete "<<tmp<<" I " <<tmp->partition_current.GetDescription()<<endl;
        tmp1 = tmp->next;
        // //!!!!!!!!!!!!!!There may be a null point exception withour if check!!!!!!!!!!!!!!!!!!!!!!!!!
        // if (tmp1!=nullptr)
        // {
        //     tmp1->prev=nullptr;
        // }
        delete tmp;
        tmp = tmp1;
        //cout<<"Delete ok "<<endl;
    }

    for (size_t i = 0; i < MAXHOLD; i++) {
        if (ClonePartList[i] != nullptr) {
            delete ClonePartList[i];
        }

    }

    exit(0);

}

partition_list *initPartitionList() {
    if (list_head != nullptr) {
        cout << "Partition list already inited." << endl;
        return list_head;
    }
    list_head = new partition_list(GPTPart(), -1);
    list_head->next = nullptr;
    list_head->prev = nullptr;
    return list_head;
}

partition_list *insertItem(partition_list *where, partition_list *which) {
    partition_list *tmp = list_head;
    do {

        if (tmp == where) {
            partition_list *tmp1 = tmp->next;
            tmp->next = which;
            which->prev = tmp;
            which->next = tmp1;
            tmp1->prev = which;
            return where->next;
        }

        tmp = tmp->next;


    } while (tmp != nullptr);
    return nullptr;

}

partition_list *deleteItem(partition_list *at) {
    partition_list *tmp = list_head;
    do {
        //if delete list head
        if (at == list_head) {
            tmp = list_head->next;
            delete list_head;
            list_head = tmp;
            list_head->prev = nullptr;
            return list_head;
        } else {

            if (tmp->next == at) {
                partition_list *tmp1 = tmp->next->next;
                tmp1->prev = tmp;
                delete tmp->next;
                tmp->next = tmp1;
                return tmp->next;

            }

            tmp = tmp->next;

        }

    } while (tmp != nullptr);
    return nullptr;
}


/**
 * @description: add a item to the end of this list,test header also
 * @param {partition_list} *what
 * @return {The orig last item}
 */
partition_list *addItem(partition_list *what) {
    partition_list *tmp = list_head;
    //if header is null
    if (tmp->partition_current.GetLastLBA() == 0) {
        cout << " I consider part whih 0 lastLBA is null part." << endl;
        delete tmp;
        list_head = what;
        list_head->next = nullptr;
        list_head->prev = nullptr;
        return list_head;
    }

    while (tmp->next != nullptr) {
        tmp = tmp->next;
    }

    tmp->next = what;
    what->prev = tmp;
    what->next = nullptr;
    return tmp->next;
}