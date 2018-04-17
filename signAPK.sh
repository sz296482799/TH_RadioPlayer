#!/bin/sh

INPUT_PATH=/Users/zshang/IdeaProjects/git/TH_RadioPlayer/app/build/outputs/apk/app-debug.apk
OUTPUT_PATH=/Users/zshang/IdeaProjects/git/TH_RadioPlayer/app/build/outputs/apk/app-signapk.apk

PK8_PATH=/Users/zshang/Documents/android/keystore/HISI/HI3798MV100_security/platform.pk8
PEM_PATH=/Users/zshang/Documents/android/keystore/HISI/HI3798MV100_security/platform.x509.pem

SIGNAPK_JAR_PATH=/Users/zshang/Documents/android/keystore/HISI/HI3798MV100_security/signapk.jar

while [ -n "$1" ]
do
        case "$1" in
                s) java -jar $SIGNAPK_JAR_PATH $PEM_PATH $PK8_PATH $INPUT_PATH $OUTPUT_PATH; shift 1;;
                i) adb push $OUTPUT_PATH /data/local/tmp/com.taihua.th_radioplayer;adb shell pm install -t -r "/data/local/tmp/com.taihua.th_radioplayer"; shift 1;;
                r) adb shell am start -n "com.taihua.th_radioplayer/.MainActivity"; shift 1;;
        esac
done



