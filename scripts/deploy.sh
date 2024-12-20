#!/bin/bash

# 로그 디렉토리 생성
DEPLOY_LOG=/home/ec2-user/app/backend/deploy.log
mkdir -p /home/ec2-user/app/backend

# 배포 시작 시간 기록
echo "> 배포 시작 : $(date +%c)" >> $DEPLOY_LOG

# 디버그: AWS 자격 증명 및 Parameter Store 접근 확인
echo "> Debug - AWS Caller Identity:" >> $DEPLOY_LOG
aws sts get-caller-identity >> $DEPLOY_LOG 2>&1
echo "> Debug - Parameter Store Access Test:" >> $DEPLOY_LOG
aws ssm get-parameter --name /development/AWS_ACCESS_KEY_ID --with-decryption >> $DEPLOY_LOG 2>&1

# AWS Systems Manager에서 파라미터 가져오기
export AWS_ACCESS_KEY_ID=$(aws ssm get-parameter --name /development/AWS_ACCESS_KEY_ID --with-decryption | jq -r '.Parameter.Value')
export AWS_SECRET_ACCESS_KEY=$(aws ssm get-parameter --name /development/AWS_SECRET_ACCESS_KEY --with-decryption | jq -r '.Parameter.Value')
export AWS_S3_BUCKET=$(aws ssm get-parameter --name /development/AWS_S3_BUCKET --with-decryption | jq -r '.Parameter.Value')
export JWT_SECRET_KEY=$(aws ssm get-parameter --name /development/JWT_SECRET_KEY --with-decryption | jq -r '.Parameter.Value')
export JWT_EXPIRATION=$(aws ssm get-parameter --name /development/JWT_EXPIRATION --with-decryption | jq -r '.Parameter.Value')
export JWT_REFRESH_EXPIRATION=$(aws ssm get-parameter --name /development/JWT_REFRESH_EXPIRATION --with-decryption | jq -r '.Parameter.Value')

# 환경 변수 존재 여부 확인 (값은 출력하지 않음)
echo "> Environment variables check:" >> $DEPLOY_LOG
echo "AWS_ACCESS_KEY_ID exists: $([ ! -z "$AWS_ACCESS_KEY_ID" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "AWS_SECRET_ACCESS_KEY exists: $([ ! -z "$AWS_SECRET_ACCESS_KEY" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "AWS_S3_BUCKET exists: $([ ! -z "$AWS_S3_BUCKET" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_SECRET_KEY exists: $([ ! -z "$JWT_SECRET_KEY" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_EXPIRATION exists: $([ ! -z "$JWT_EXPIRATION" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_REFRESH_EXPIRATION exists: $([ ! -z "$JWT_REFRESH_EXPIRATION" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG

# JAR 파일 찾기
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

# JAR 파일 실행
echo "> $JAR_NAME 배포" >> $DEPLOY_LOG
nohup java -jar \
    -Dcloud.aws.credentials.access-key=${AWS_ACCESS_KEY_ID} \
    -Dcloud.aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY} \
    -Dcloud.aws.s3.bucket=${AWS_S3_BUCKET} \
    -Dcloud.aws.region.static=ap-northeast-2 \
    -Dcloud.aws.stack.auto=false \
    -Dspring.security.jwt.secret-key=${JWT_SECRET_KEY} \
    -Dspring.security.jwt.expiration=${JWT_EXPIRATION} \
    -Dspring.security.jwt.refresh-token.expiration=${JWT_REFRESH_EXPIRATION} \
    -Dspring.profiles.active=prod \
    $JAR_NAME > /home/ec2-user/app/backend/nohup.out 2>&1 &

# 실행 확인
sleep 3
CURRENT_PID=$(pgrep -f ${JAR_NAME})
echo "> 배포 완료 : $CURRENT_PID" >> $DEPLOY_LOG
