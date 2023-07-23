#!/bin/sh
#本脚本由　by Han | 情非得已c，编写
#应用于搞机助手上

#Agisk Import This Script Via https://github.com/liuran001/GJZS.git
#Agisk Edited
a=0
b=($(ls /dev/block/))
for i in ${b[@]}; do
  [[ -d /dev/block/$i ]] && unset b[$a]
  a=$((a + 1))
done

AWK=$(awk -where 2>/dev/null)
BLOCKDEV=$(which blockdev)

find /dev/block -type l | while read o; do
  [[ -d "$o" ]] && continue
  c=$(basename "$o")
  echo ${b[@]} | grep -q "$c" && continue
  echo $c
done | sort -u | while read Row; do
  BLOCK=$(find /dev/block -name $Row | head -n 1)
  SIZE=$(blockdev --getsize64 "$BLOCK")
  # BLOCK2=`readlink -e $BLOCK`
  echo "$BLOCK=$SIZE"
done
