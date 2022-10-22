/*
 * @Author: gaochuntie
 * @Github: https://github.com/gaochuntie
 * @Date: 2022-04-04 13:23:30
 * @LastEditors: gaochuntie
 * @LastEditTime: 2022-04-04 13:27:14
 * @Description: sgdisk_cl functions for atms
 */
#ifndef __SGDISK_CL

#define __SGDISK_CL

/*
 * Dump partition details in a machine readable format:
 *
 * DISK [mbr|gpt] [guid]
 * PART [n] [type] [guid]
 */
int android_dump(char *device);

int sgdisk_main(int argc, char *argv[]);

#endif