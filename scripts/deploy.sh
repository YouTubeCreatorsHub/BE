#!/bin/bash

# 로그 디렉토리 생성
DEPLOY_LOG=/home/ec2-user/app/backend/deploy.log
mkdir -p /home/ec2-user/app/backend

# 배포 시작 시간 기록
echo "> 배포 시작 : $(date +%c)" >> $DEPLOY_LOG

# 디버그: AWS 자격 증명 및 Parameter Store 접근 확인
echo "> Debug - AWS Caller Identity:" >> $DEPLOY_LOG
aws sts get-caller-identity >> $DEPLOY_LOG 2>&1
echo "> Debug - Parameter Store Values:" >> $DEPLOY_LOG
echo "> Debug - Parameter Store Access Test:" >> $DEPLOY_LOG
aws ssm get-parameter --name /development/AWS_ACCESS_KEY_ID --with-decryption >> $DEPLOY_LOG 2>&1
echo "> Debug - AWS_SECRET_ACCESS_KEY raw output:" >> $DEPLOY_LOG
aws ssm get-parameter --name /development/AWS_SECRET_ACCESS_KEY --with-decryption >> $DEPLOY_LOG 2>&1
echo "> Debug - AWS_SECRET_ACCESS_KEY query output:" >> $DEPLOY_LOG
aws ssm get-parameter --name /development/AWS_SECRET_ACCESS_KEY --with-decryption --query Parameter.Value --output text >> $DEPLOY_LOG 2>&1

# AWS Systems Manager에서 파라미터 가져오기 (임시 변수 사용)
ACCESS_KEY=$(aws ssm get-parameter --name /development/AWS_ACCESS_KEY_ID --with-decryption --query Parameter.Value --output text)
SECRET_KEY=$(aws ssm get-parameter --name /development/AWS_SECRET_ACCESS_KEY --with-decryption --query Parameter.Value --output text)
S3_BUCKET=$(aws ssm get-parameter --name /development/AWS_S3_BUCKET --with-decryption --query Parameter.Value --output text)
JWT_KEY=$(aws ssm get-parameter --name /development/JWT_SECRET_KEY --with-decryption --query Parameter.Value --output text)
JWT_EXP=$(aws ssm get-parameter --name /development/JWT_EXPIRATION --with-decryption --query Parameter.Value --output text)
JWT_REFRESH_EXP=$(aws ssm get-parameter --name /development/JWT_REFRESH_EXPIRATION --with-decryption --query Parameter.Value --output text)
MULTIPART_MAX_FILE_SIZE=$(aws ssm get-parameter --name /development/SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE --query Parameter.Value --output text)
MULTIPART_MAX_REQUEST_SIZE=$(aws ssm get-parameter --name /development/SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE --query Parameter.Value --output text)

# 환경 변수로 설정
export AWS_ACCESS_KEY_ID="${ACCESS_KEY}"
export AWS_SECRET_ACCESS_KEY="${SECRET_KEY}"
export AWS_S3_BUCKET="${S3_BUCKET}"
export JWT_SECRET_KEY="${JWT_KEY}"
export JWT_EXPIRATION="${JWT_EXP}"
export JWT_REFRESH_EXPIRATION="${JWT_REFRESH_EXP}"
export SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE="${MULTIPART_MAX_FILE_SIZE}"
export SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE="${MULTIPART_MAX_REQUEST_SIZE}"

# 환경 변수 존재 여부 확인 (값은 출력하지 않음)
echo "> Environment variables check:" >> $DEPLOY_LOG
echo "AWS_ACCESS_KEY_ID exists: $([ ! -z "$AWS_ACCESS_KEY_ID" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "AWS_SECRET_ACCESS_KEY exists: $([ ! -z "$AWS_SECRET_ACCESS_KEY" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "AWS_S3_BUCKET exists: $([ ! -z "$AWS_S3_BUCKET" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_SECRET_KEY exists: $([ ! -z "$JWT_SECRET_KEY" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_EXPIRATION exists: $([ ! -z "$JWT_EXPIRATION" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "JWT_REFRESH_EXPIRATION exists: $([ ! -z "$JWT_REFRESH_EXPIRATION" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE exists: $([ ! -z "$SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG
echo "SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE exists: $([ ! -z "$SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE" ] && echo "Yes" || echo "No")" >> $DEPLOY_LOG

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

# JAR 파일 실행 부분도 수정
nohup java -jar \
    -Dcloud.aws.credentials.access-key=${AWS_ACCESS_KEY_ID} \
    -Dcloud.aws.credentials.secret-key=${AWS_SECRET_ACCESS_KEY} \
    -Dcloud.aws.s3.bucket=${AWS_S3_BUCKET} \
    -Dcloud.aws.region.static=ap-northeast-2 \
    -Dcloud.aws.stack.auto=false \
    -Dspring.security.jwt.secret-key=${JWT_SECRET_KEY} \
    -Dspring.security.jwt.expiration=${JWT_EXPIRATION} \
    -Dspring.security.jwt.refresh-token.expiration=${JWT_REFRESH_EXPIRATION} \
    -Dspring.servlet.multipart.max-file-size=${SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE} \
    -Dspring.servlet.multipart.max-request-size=${SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE} \
    -Dspring.profiles.active=prod \
    $JAR_NAME > /home/ec2-user/app/backend/nohup.out 2>&1 &

# 실행 확인
sleep 3
CURRENT_PID=$(pgrep -f ${JAR_NAME})
echo "> 배포 완료 : $CURRENT_PID" >> $DEPLOY_LOG
