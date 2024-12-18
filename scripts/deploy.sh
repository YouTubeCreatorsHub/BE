#!/bin/bash

# 로그 디렉토리 생성
DEPLOY_LOG=/home/ec2-user/app/backend/deploy.log
mkdir -p /home/ec2-user/app/backend

# 배포 시작 시간 기록
echo "> 배포 시작 : $(date +%c)" >> $DEPLOY_LOG

# JAR 파일 찾기
JAR_NAME=$(ls -tr /home/ec2-user/app/backend/build/libs/*.jar | tail -n 1)
echo "> JAR Name: $JAR_NAME" >> $DEPLOY_LOG

# 실행 중인 프로세스가 있으면 종료
CURRENT_PID=$(pgrep -f ${JAR_NAME})
if [ -z $CURRENT_PID ]
then
  echo "> 현재 실행중인 애플리케이션이 없습니다." >> $DEPLOY_LOG
else
  echo "> kill -15 $CURRENT_PID" >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
  sleep 5
fi

# JAR 파일 실행
echo "> $JAR_NAME 배포" >> $DEPLOY_LOG
nohup java -jar \
    -Dspring.config.location=/home/ec2-user/app/backend/application.yml \
    -Dspring.profiles.active=prod \
    $JAR_NAME > /home/ec2-user/app/backend/nohup.out 2>&1 &

# 실행 확인
sleep 3
CURRENT_PID=$(pgrep -f ${JAR_NAME})
echo "> 배포 완료 : $CURRENT_PID" >> $DEPLOY_LOG
