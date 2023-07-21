#!/bin/sh

export  list_name="MD5ListForKey.txt"
export  file_path="$(pwd)/$list_name"
export  key_part="$(pwd)/key_part.txt"
#echo $file_path
export  part_dir="/dev/block/by-name"
export  exclusion="super sd* mmcblk* userdata virtual_sd ubuntu gaochuntie recovery* boot boot* vender vender*"
help() {
    echo "Android Dual System Key Tool"
    echo "Seek out the partitions related to key,so you can make them into dual status like your dual super,boot partitions"
    echo "Usage : You need to run this script two times"
    echo "\tFirst : Just set a pin for you device and run this script"
    echo "\tThen  : Keep the output file and change the pin for you device"
    echo "\tLast  : Run this script with -f [the_file_above] and it will give you the partitions related to key"
    echo "Note : This script will ignore partitions bigger than 1G because they are sure not the excepted"
}

# Function to check if the file should be excluded
exclude_file() {
    file="$1"
    if echo "$file" | grep -Eq "$exclusion"; then
        return 0 # File matches exclusion pattern
    fi
    return 1 # File doesn't match any exclusion pattern
}

#first run
generateMD5List() {

# Find files in the directory (excluding partition devices and files matching the exclusion patterns) and generate the output
find "$part_dir" -maxdepth 1 -exec sh -c '
    for file do
        # Check if the file is a partition device
        if file "$file" | grep -q "block special"; then
            continue
        fi

        # Check if the file should be excluded
        skip=false
        for pattern in $exclusion; do
            if echo "$file" | grep -Eq "$pattern"; then
                skip=true
                break
            fi
        done

        # Skip the file if it matches an exclusion pattern
        if $skip; then
            continue
        fi

        # Check the file size using du and exclude files larger than 5GB
        size=$(du -Lb "$file" | awk "{print \$1}")
        if [ "$size" -gt 1000000000 ]; then
            continue
        fi

        # Calculate the clean MD5
        md5=$(md5sum "$file" | awk "{print \$1}")

        # Check if the output file exists and create or append accordingly
        if [ ! -e "$file_path" ]; then
            echo "$file:$md5" > "$file_path"
        else
            echo "$file:$md5" >> "$file_path"
        fi
        echo "$file:$md5"
    done
' sh {} +
}


#second run with -f flag
compareMD5List() {
  md5_list_file="$(pwd)/$1"
  echo "MD5 File : $md5_list_file"
  if [ -e "$key_part" ]; then
      rm -rf $key_part
  fi
  touch $key_part
      while IFS=: read -r file md5_from_list; do
          # Find the current MD5 of the file using your actual command or approach
          current_md5=$(md5sum "$file" | awk '{print $1}')

          # Compare with the MD5 from the list
          if [ "$md5_from_list" != "$current_md5" ]; then
              echo "MD5 changed for file: $file"
              echo "Old MD5: $md5_from_list"
              echo "New MD5: $current_md5"
              echo "Found Key Part: $file"
              echo "$file">>$key_part
          fi
      done < "$md5_list_file"
}

# Main part of the script

# Check for help flag
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    help
    exit 0
fi

# Continue with the rest of your script logic here

if [ "$1" = "-f" ] && [ -f "$2" ];then
  echo "Comparing MD5 List"
  compareMD5List "$2"
  exit 0
  fi

echo "MD5List is not existed.Generating..."
generateMD5List
echo "Done.Check $list_path"
exit 0