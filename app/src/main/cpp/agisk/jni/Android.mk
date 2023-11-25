LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Add your source files here
LOCAL_SRC_FILES := \
    Action.cpp \
    Adjustion.cpp \
    agisk.cpp \
    Utils.cpp

# Add include directories
LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/gpt \
    $(LOCAL_PATH)/uuid

# Specify the name of your executable module
LOCAL_MODULE := agisk

# Specify library locations
LOCAL_LDLIBS := -L$(LOCAL_PATH)/libs/arm64-v8a

# Link against the pre-built libraries
LOCAL_LDLIBS += -lgpt -luuid

# Specify library dependencies
LOCAL_STATIC_LIBRARIES := gpt uuid

include $(BUILD_EXECUTABLE)
