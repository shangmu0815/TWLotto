LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := tests

LOCAL_STATIC_JAVA_LIBRARIES := GoogleAdMobAdsSdk-4.0.4

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := TWLotto

#LOCAL_SDK_VERSION := current

include $(BUILD_PACKAGE)

##################################################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
      GoogleAdMobAdsSdk-4.0.4:GoogleAdMobAdsSdk-4.0.4.jar
include $(BUILD_MULTI_PREBUILT)

# Use the following include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
