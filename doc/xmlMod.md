# Below I show a backup action xml

``` xml
<?xml version="1.0" encoding="UTF-8" ?>

<config>
    <attribute>
        <name>Backup</name>
        <id>faiomlcejabt</id>
        <author>asm1409</author>
        <mark>backup</mark>
        <description>disk backup test</description>
        <uuid>JKFUMHUNIOMJIO</uuid>
        <rom_name>Test</rom_name>
        <android_version>android12</android_version>
    </attribute>
    <Action>
        <DiskModification>
            <backup length="10240" start="512" destfile="/sdcard/test.img"
                driver="/dev/block/vda2" />
        </DiskModification>
    </Action>

</config>

```

# Attribute
    name : show in app xml list

    id : your unique id, Strictly required if your xml is included in official reposity

    author :author or your email likelly things

    mark : may saied xml type such as Installer,Backup,Changer ...

    description : as named

    uuid : If modified the xml please update uuid, if modified in agisk_client app,will update uuid when saving

    rom_name : as named ,such MIUI,Flyme,ColorOs ....

    android_version : specific android_version for this xml, this won't affect action,only a tag

# Action
    action list ,allow multi
    !!!All action is orderd as you set in xml!!!

# DiskModification
    Before ':' is action node name
    After ':' is action argv name
    Example : 
    <backup length="10240" start="512" destfile="/sdcard/test.img" driver="/dev block/vda2" />

    reserve:start length
    write:start length raw_file offset_raw
    clone:s_driver s_start s_length t_start
    format:start length
    backup:start length destfile
# PartitionModification
    The same as DiskModification

    new:name start size
    delete:name | pt_number
    clone:s_driver s_number t_start
    format:pt_number filesystem
    mount:pt_number filesystem mount_point