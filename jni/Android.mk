LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := multimon

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL \
                -Wall -g

LOCAL_LDFLAGS          := -Wl,-Map,xxx.map

LOCAL_SRC_FILES := \
   hdlc.c \
   demod_afsk12.c \
   costabf.c \
   costabi.c \
   multimon.c

LOCAL_LDLIBS := -ldl -llog

include $(BUILD_SHARED_LIBRARY)
