/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-09 22:09:49
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-05-03 21:52:09
 * @Description: adjustion implement
 */
#include "Adjustion.hpp"
#include "Utils.hpp"
#include <iostream>

using namespace std;

Adjustion::Adjustion(GPTData &gptdata) {
    this->gptdata = gptdata;
}

Adjustion::~Adjustion() {
}

int Adjustion::retValue(void) {
    return this->ret;
}

string &Adjustion::getResult(void) {
    return this->result;
}

int Adjustion::delete_pt(uint32_t num) {
    return this->gptdata.DeletePartition(num);
}

int Adjustion::new_pt(char *name, uint64_t start_sector, uint64_t size_sector) {
    uint32_t available = this->gptdata.FindFirstFreePart();
    if (available == -1) {

        this->ret = -1;
        this->result = "No free partnum.";
        return 0;
    }
    return this->gptdata.CreatePartition(available, start_sector, size_sector + start_sector - 1);

}

int Adjustion::change_name_pt(uint32_t num, char *new_name) {
    return this->gptdata.SetName(num, string(new_name));

}

int Adjustion::auto_clone(partition_list *list) {
    partition_list *current = list;
    partition_list *temp;
    uint32_t low, high;
    uint32_t counter;
    uint64_t available_start = 0;
    uint64_t lastUsable;//this->gptdata.FindLastInFree(available_start);

    gptdata.GetPartRange(&low, &high);
    //partnum start with 0
    for (uint32_t i = low; i <= high; i++) {
        if (gptdata.IsUsedPartNum(i)) {
            if (available_start < this->gptdata[i].GetLastLBA() + 1) {
                available_start = this->gptdata[i].GetLastLBA() + 1;
            }

            if (lastUsable <= available_start) {
                lastUsable = gptdata.FindLastInFree(available_start);
            }

        }
    }
    available_start = available_start + atmsMetedataSizeBlock;


    uint64_t freeAvailable = lastUsable - available_start + 1;

    uint32_t free_partnum = -1;

    //Todo
    //cout<<"DD 1"<<endl;

    //check total available ?

    uint64_t count_total = 0;
    while (current != nullptr) {

        ///////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////
        //There i will delete orig partitions !

        gptdata.DeletePartition(current->partnum);

        ////////
        free_partnum = this->gptdata.FindFirstFreePart();
        if (free_partnum == -1) {
            ret = -1;
            result = "No free partnum.";
            return 1;
        }
        cout << "New " << free_partnum << "(" << free_partnum + 1 << ") " << " " << available_start
             << " " << available_start + current->partition_current.GetLengthLBA() - 1 << endl;
        ret = this->gptdata.CreatePartition(free_partnum, available_start, available_start +
                                                                           current->partition_current.GetLengthLBA() -
                                                                           1);

        if (ret == 0) {
            cout << "E:" << " Problems insert. @creating " << free_partnum << " " << available_start
                 << " " << available_start + current->partition_current.GetLengthLBA() - 1 << endl;

        } else {
            gptdata.SetName(free_partnum, current->partition_current.GetDescription());

        }

        //Warning , 0 sector between each pt for 2-sector boundaries

        available_start = available_start + current->partition_current.GetLengthLBA();
        current = current->next;
    }
    gptdata.JustLooking(0);
    gptdata.SaveGPTData();
    gptdata.JustLooking(1);

}

int Adjustion::operator==(int value) {
    if (this->ret == value) {
        return 1;
    }
    return 0;

}
    
