TEMPLATE = app

QT += quick qml opengl gui gui-private
egl = 1
SOURCES += main.cpp \
           window_singlethreaded.cpp \
           cuberenderer.cpp

HEADERS += window_singlethreaded.h \
           cuberenderer.h

RESOURCES += rendercontrol.qrc \
    data.qrc

target.path = $$[QT_INSTALL_EXAMPLES]/quick/rendercontrol/rendercontrol_opengl
INSTALLS += target

DISTFILES += \
    android/AndroidManifest.xml \
    android/build.gradle \
    android/gradle.properties \
    android/gradle/wrapper/gradle-wrapper.jar \
    android/gradle/wrapper/gradle-wrapper.properties \
    android/gradlew \
    android/gradlew.bat \
    android/res/values/libs.xml \
    android/res/raw/* \
    android/src/com/halulu/example/*.java

contains(ANDROID_TARGET_ARCH,arm64-v8a) {
    ANDROID_PACKAGE_SOURCE_DIR = \
        $$PWD/android
}
