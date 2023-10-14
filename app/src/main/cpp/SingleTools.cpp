//
// Created by jackmaxpale on 2023/9/20.
//

#include "SingleTools.h"
#include "include/gpt/gpt.h"
#include "include/gpt/gptpart.h"
#include "include/gpt/parttypes.h"
#include "des/encrypt.h"
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
     * eg: /dev/block/sda 0x8300 new_part1 32
     * so the partition file is /dev/block/sda33
     */

    //failed
    return 1;
}

/**
 * decrypt a encrypted string using des
 * @param en_content is the encrypted string
 * @param key is the des key
 * @return decrypted string
 */
string doDecryptString(string en_content,string key){
    string dec = decrypt(en_content, key);
    return dec;
}

/**
 * like above
 * @param de_content
 * @param key
 * @return
 */
string doEncryptString(string de_content,string key) {
    string enc = encrypt(de_content, key);
    return enc;
}
///////

string doEncryptAgiskSubXml(string de_content,string key,int flag);
string doDecryptAgiskSubXml(string en_content,string key,int flag);