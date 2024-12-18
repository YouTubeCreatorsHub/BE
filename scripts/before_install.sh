#!/bin/bash

# 백엔드 전용 디렉토리 생성
mkdir -p /home/ec2-user/app/backend

# 권한 설정
chmod -R 755 /home/ec2-user/app/backend

# 로그 파일 생성
touch /home/ec2-user/app/backend/deploy.log

# 기존 프로세스 종료
CURRENT_PID=$(pgrep -f '.jar')
if [ -n "$CURRENT_PID" ]; then
    echo "종료할 애플리케이션 PID: $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi
