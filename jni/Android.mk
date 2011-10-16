LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := multimon

LOCAL_CFLAGS := -DANDROID_NDK \
                -DDISABLE_IMPORTGL \
                -Wall -g

LOCAL_LDFLAGS          := -Wl,-Map,xxx.map

LOCAL_SRC_FILES := \
   hdlc.c \
   pocsag.c \
   demod_afsk12.c \
   demod_afsk24.c \
   demod_afsk24_2.c \
   demod_dtmf.c \
   demod_fsk96.c \
   demod_hapn48.c \
   demod_poc12.c \
   demod_poc24.c \
   demod_poc5.c \
   demod_zvei.c \
   costabf.c \
   costabi.c \
   multimon.c

LOCAL_LDLIBS := -ldl -llog

include $(BUILD_SHARED_LIBRARY)
