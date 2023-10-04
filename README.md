# Release
  https://github.com/gaochuntie/agisk_client/releases
  

# Introduce : Agisk android app client & GPT Partition Manager
    This is a project regarding all disk/partition actions as xml configuration files.And include a GUI GPT Partition Manager.
    Such idea is the same as TWRP's ui-action.
    For example:
        <Action>
        <DiskModification>
            <backup length="10240" start="512" destfile="/sdcard/test.img" driver="/dev/block/vda2" />
        </DiskModification>
    </Action>
    
    When you exec this xml in agisk client, chunks from 512byte to 10752bytes in /dev/block/vda2 will be copied out to /sdcard/test.img
    
    * [For other action xml,please see doc/xmlMod.md](doc/xmlMod.md)

# Updating progress (Workable functions and not work functions)
  * All DiskAction , note : Reserved action is enhanced to be integrated into the whole app's action. Reserved Repository is stored only in App Progress,not include Root Service Progress
  * PartitionAction :  new(1,2,3) ; delete(1,2) ; mount
  * GPT Manager : new delete mount umount
  * Not Work : install page,advenced action,agisk twrp,settings(just useless now)
  * ![Image](https://github.com/gaochuntie/agisk_client/blob/dev/imgs/gpt_manager_1.jpg)
  * ![Image](https://github.com/gaochuntie/agisk_client/blob/dev/imgs/gpt_manager_2.jpg)
  * ![Image](https://github.com/gaochuntie/agisk_client/blob/dev/imgs/gpt_manager_new_1.jpg)
  * ![Image](https://github.com/gaochuntie/agisk_client/blob/dev/imgs/gpt_manager_new_1.jpg)
  ** Update date 202310040930
  
# Developing stage.
  This project is still in developing stage,You may not be able to run it as expected.
  
# TO contribute to this project,please see commit in cpp/* 
  * All work on android application will be handled myself. Or you can write xmls for your device and make a request to xmls/
.
  * Please contribute to this project by implement TODO in cpp/*
  c++ sources.Each function is taged with a detailed commit.
  * [Tips] libuuid and libgpt  prebuild in cpp/libs 
  * I will check request every night 
  
# Language issue
  * The project only support English language and will never be translated into other languages
  for it's an open source and free hacker tool for android.
  
# About me
  * Thank you very much to check my project,if you have any issue or individule suggestion,please open an issue or email me at
  2041469901@qq.com Nightly check [ QQ Contract Request not allowed.]
   * or jackmaxpale@gmail.com [Less check]
  
