#!/bin/bash

PROCESS_NAME="translateServer"
ROOT_PATH="/home/ubuntu/translate/free-translate-api"
GO="$ROOT_PATH/main.go"
TRANSLATE_LOG="$ROOT_PATH/translate.log"

# 실행 중인 서버의 PID 찾기
SERVICE_PID=$(pgrep -f "$GO")

# 로그 파일이 없으면 생성
if [ ! -f "$TRANSLATE_LOG" ]; then
  touch "$TRANSLATE_LOG"
fi

# 서비스 상태 확인
if [ -z "$SERVICE_PID" ]; then
  echo "$(date): 서비스 NotFound! 서비스 시작 중..." >> $TRANSLATE_LOG
  # PID가 없을 경우, 서비스 시작
  nohup go run "$GO" >> $TRANSLATE_LOG 2>&1 &
  echo "$(date): 서비스가 시작되었습니다." >> $TRANSLATE_LOG
else
  echo "$(date): 서비스가 이미 실행 중입니다. PID: $SERVICE_PID" >> $TRANSLATE_LOG
fi