ROOT_PATH="/home/ubuntu/probono-backend"
#JAR="$ROOT_PATH/build/libs/school-0.0.1-SNAPSHOT.jar"
JAR ="school-0.0.1-SNAPSHOT.jar"
STOP_LOG="$ROOT_PATH/stop.log"
SERVICE_PID=$(pgrep -f $JAR) # 실행중인 Spring 서버의 PID

if [ -z "$SERVICE_PID" ]; then
  echo "서비스 NotFound!222" >> $STOP_LOG
else
  echo "서비스 종료 " >> $STOP_LOG
  sudo kill "$SERVICE_PID"
  # kill -9 $SERVICE_PID # 강제 종료를 하고 싶다면 이 명령어 사용
fi
# ROOT_PATH="/home/ubuntu/probono-backend"
# JAR="$ROOT_PATH/build/libs/school-0.0.1-SNAPSHOT.jar"
# STOP_LOG="$ROOT_PATH/stop.log"
# SERVICE_PID=$(ps -ef | grep $JAR | grep -v grep | awk '{print $2}') # 실행중인 Spring 서버의 PID

# if [ -z "$SERVICE_PID" ]; then
#   echo "서비스 NotFound!" >> $STOP_LOG
# else
#   echo "서비스 종료 " >> $STOP_LOG
#   # kill "$SERVICE_PID"
#   sudo kill -9 $SERVICE_PID # 강제 종료를 하고 싶다면 이 명령어 사용
# fi
