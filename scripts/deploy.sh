#!/bin/bash

# 로그 디렉토리 생성
DEPLOY_LOG=/home/ec2-user/app/backend/deploy.log
mkdir -p /home/ec2-user/app/backend

# 배포 시작 시간 기록
echo "> 배포 시작 : $(date +%c)" >> $DEPLOY_LOG

# 현재 디렉토리 및 파일 상태 확인
echo "> Current directory: $(pwd)" >> $DEPLOY_LOG
echo "> Directory contents:" >> $DEPLOY_LOG
ls -la /home/ec2-user/app/backend >> $DEPLOY_LOG

# AWS 자격증명 상태 확인
echo "> AWS Credentials Status:" >> $DEPLOY_LOG
echo "> AWS_ACCESS_KEY_ID exists: $([ ! -z "$AWS_ACCESS_KEY_ID" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "> AWS_SECRET_ACCESS_KEY exists: $([ ! -z "$AWS_SECRET_ACCESS_KEY" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "> AWS_REGION: ${AWS_REGION}" >> $DEPLOY_LOG

# JAR 파일 찾기 전 디렉토리 확인
echo "> Checking build/libs directory:" >> $DEPLOY_LOG
ls -la /home/ec2-user/app/backend/build/libs >> $DEPLOY_LOG 2>&1

# JAR 파일 찾기 (plain JAR 제외)
JAR_NAME=$(ls -tr /home/ec2-user/app/backend/build/libs/*[!plain].jar | tail -n 1)
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

# JAR 실행 전 환경변수 상태 확인
echo "> Environment variables before running JAR:" >> $DEPLOY_LOG
env | grep -E "AWS_|JWT_" >> $DEPLOY_LOG

# JAR 파일 실행
echo "> $JAR_NAME 배포" >> $DEPLOY_LOG
nohup java -jar \
    -Dcloud.aws.credentials.access-key=${AWS_ACCESS_KEY_ID} \
    -Dcloud.aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY} \
    -Dcloud.aws.s3.bucket=${AWS_S3_BUCKET} \
    -Dcloud.aws.region.static=${AWS_REGION} \
    -Dcloud.aws.stack.auto=${AWS_STACK_AUTO} \
    -Dspring.security.jwt.secret-key=${JWT_SECRET_KEY} \
    -Dspring.security.jwt.expiration=${JWT_EXPIRATION} \
    -Dspring.security.jwt.refresh-token.expiration=${JWT_REFRESH_EXPIRATION} \
    -Dspring.profiles.active=prod \
    $JAR_NAME > /home/ec2-user/app/backend/nohup.out 2>&1 &

# 실행 후 프로세스 상태 확인
sleep 3
CURRENT_PID=$(pgrep -f ${JAR_NAME})
echo "> Process status after deployment:" >> $DEPLOY_LOG
ps -ef | grep java >> $DEPLOY_LOG
echo "> 배포 완료 : $CURRENT_PID" >> $DEPLOY_LOG
