#!/bin/bash

# 로그 디렉토리 생성
DEPLOY_LOG=/home/ec2-user/app/backend/deploy.log
mkdir -p /home/ec2-user/app/backend

# 배포 시작 시간 기록
echo "> 배포 시작 : $(date +%c)" >> $DEPLOY_LOG

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

# AWS 환경 변수 설정 및 확인
AWS_ACCESS_KEY_ID=$(aws configure get aws_access_key_id)
AWS_SECRET_ACCESS_KEY=$(aws configure get aws_secret_access_key)
AWS_REGION=$(aws configure get region)

# 환경 변수 확인 로깅
echo "> AWS Access Key ID: ${AWS_ACCESS_KEY_ID}" >> $DEPLOY_LOG
echo "> AWS Region: ${AWS_REGION}" >> $DEPLOY_LOG

# 환경 변수 설정
export AWS_ACCESS_KEY_ID
export AWS_SECRET_ACCESS_KEY
export AWS_REGION
export AWS_S3_BUCKET
export AWS_STACK_AUTO

# 환경 변수 확인을 위한 로깅
echo "> 환경 변수 확인:" >> $DEPLOY_LOG
env | grep AWS >> $DEPLOY_LOG

# JAR 파일 실행 (Spring이 인식할 수 있는 형식으로 환경 변수 전달)
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

# 실행 확인
sleep 3
CURRENT_PID=$(pgrep -f ${JAR_NAME})
echo "> 배포 완료 : $CURRENT_PID" >> $DEPLOY_LOG
