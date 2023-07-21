#!/bin/bash
adb push key_hide_and_seek.sh /sdcard
adb shell su -c cp /sdcard/key_hide_and_seek.sh /data/agisk
adb shell su -c chmod a+x /data/agisk/key_hide_and_seek.sh
