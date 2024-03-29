# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# 需要引入我们头文件,以这个配置的目录为基准
include_directories(../jniLibs/include)

# 添加共享库搜索路径
LINK_DIRECTORIES(../jniLibs/armeabi)

# 指定源文件目录
#AUX_SOURCE_DIRECTORY(./header HEADER_LIST)
AUX_SOURCE_DIRECTORY(./cpp CPP_LIST)

add_library( # Sets the name of the library.
        media-player

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${HEADER_LIST}
        ${CPP_LIST}
        media-player.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#find_library( # Sets the name of the path variable.
#        log-lib
#
#        # Specifies the name of the NDK library that
#        # you want CMake to locate.
#        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        # 链接额外的 ffmpeg 的编译
        media-player
        # 编解码(最重要的库)
        avcodec-57
        # 设备信息
        avdevice-57
        # 滤镜特效处理库
        avfilter-6
        # 封装格式处理库
        avformat-57
        # 工具库(大部分库都需要这个库的支持)
        avutil-55
        # 后期处理
        postproc-54
        # 音频采样数据格式转换库
        swresample-2
        # 视频像素数据格式转换
        swscale-4
        # 链接 android ndk 自带的一些库
        android
        # 链接 OpenSLES
        OpenSLES
        # Links the target library to the log library
        # included in the NDK.
        log)