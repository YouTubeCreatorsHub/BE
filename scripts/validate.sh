#!/bin/bash

# 애플리케이션 실행 확인
CURRENT_PID=$(pgrep -f BE-0.0.1-SNAPSHOT.jar)

if [ -z $CURRENT_PID ]; then
    echo "Application is not running"
    exit 1
else
    echo "Application is running with PID: $CURRENT_PID"
    exit 0
fi
