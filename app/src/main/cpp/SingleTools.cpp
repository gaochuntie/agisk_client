//
// Created by jackmaxpale on 2023/9/20.
//

#include "SingleTools.h"
#include "include/gpt/gpt.h"
#include "include/gpt/gptpart.h"
#include "include/gpt/parttypes.h"

/**
 *
 * @param driver
 * @param code
 * @param name
 * @param number
 * @return 1 failed
 */
int formatPartition(string driver,string code,string name,uint64_t number){
//TODO implement filesystem support
    /**
     * format new created partition to given filesystem
     * args:
     * string code --the filesystem code
     * string drive_s  --the disk driver
     * string name_s --the partition name
     * uint64 number --the newly created partition number,0 start,not 1,so this is the actrue number
     */

    //failed
    return 1;
}