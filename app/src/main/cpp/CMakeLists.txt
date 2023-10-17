# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("my_application_c")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        agisk-cli

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        Basic.cpp
        atms_app_my_application_c_JNI.h
        DiskActions.cpp
        PartitionActions.cpp
        PartitionActions.h
        DiskActions.h
        MyLog.cpp
        MyLog.h
        SingleTools.h
        SingleTools.cpp
        des/b64.cpp
        des/b64.h
        des/encrypt.cpp
        des/encrypt.h
        des/vigenere.cpp
        des/vigenere.h
        tinyxml2/tinyxml2.cpp
        tinyxml2/tinyxml2.h
        )

#add_library(gpt STATIC IMPORTED)
#set_target_properties(gpt PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libgpt.a)
#include_directories(${CMAKE_SOURCE_DIR}/include/)
#
#add_library(uuid STATIC IMPORTED)
#set_target_properties(uuid PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libuuid.a)
#include_directories(${CMAKE_SOURCE_DIR}/include/)

#file(GLOB agisk-src
#        "agisk/*.hpp"
#        "agisk/*.cpp"
#        "include/gpt/*.h"
#        )
#
#add_library(agisk
#        SHARED
#        ${agisk-src}
#        )


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


add_library(
        #指定目标导入库.
        gpt

        #设置导入库的类型（静态或动态） 为 shared library.
        SHARED

        #告知 CMake imported-lib 是导入的库
        IMPORTED
)

set_target_properties(
        # 指定目标导入库
        gpt
        # 指定属性（本地导入的已有库）
        PROPERTIES IMPORTED_LOCATION
        # 指定你要导入库的路径.
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libgpt.so)

add_library(
        #指定目标导入库.
        c++_shared

        #设置导入库的类型（静态或动态） 为 shared library.
        SHARED

        #告知 CMake imported-lib 是导入的库
        IMPORTED
)

set_target_properties(
        # 指定目标导入库
        c++_shared
        # 指定属性（本地导入的已有库）
        PROPERTIES IMPORTED_LOCATION
        # 指定你要导入库的路径.
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libc++_shared.so)

add_library(
        #指定目标导入库.
        uuid
        #设置导入库的类型（静态或动态） 为 shared library.
        SHARED
        #告知 CMake imported-lib 是导入的库
        IMPORTED
)

set_target_properties(
        # 指定目标导入库
        uuid
        # 指定属性（本地导入的已有库）
        PROPERTIES IMPORTED_LOCATION
        # 指定你要导入库的路径.
        ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libuuid.so)

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.


target_link_libraries( # Specifies the target library.
        agisk-cli
        gpt
        uuid
        c++_shared
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})