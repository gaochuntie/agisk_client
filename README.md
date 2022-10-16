# Agisk android app client
    This is a project regarding all disk/partition actions as xml configuration files.
    For example:
        <Action>
        <DiskModification>
            <backup length="10240" start="512" destfile="/sdcard/test.img" driver="/dev/block/vda2" />
        </DiskModification>
    </Action>
    
    When you exec this xml in agisk client, chunks from 512byte to 10752bytes in /dev/block/vda2 will be copied out to /sdcard/test.img
    
[For other action xml,please see doc/xmlMod.md](doc/xmlMod.md)

# Developing stage.
  This project is still in developing stage,You may not be able to run it as expected.
  
# TO contribute to this project,please see commit in cpp/* 
  All work on android application will be handled myself. Please contribute to this project by implement TODO in cpp/*
  c++ sources.Each function is taged with a detailed commit.
  [Tips] libuuid is not include,I will provide it soon.
  I will check request every night 
  
# Language issue
  The project only support English language and will never be translated into other languages
  for it's an open source and free hacker tool for android.
  
# About me
  Thank you very much to check my project,if you have any issue or individule suggestion,please open an issue or email me at
  2041469901@qq.com Nightly check [ QQ Contract Request not allowed.]
   or jackmaxpale@gmail.com [Less check]
  
