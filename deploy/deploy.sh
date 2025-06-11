#!/bin/bash
set -e

cd "$(dirname "$0")"

if docker ps --filter "name=probono-blue" --quiet | grep -q .; then
  RUN_TARGET="green"
  STOP_TARGET="blue"
  RUN_PORT=8081
  STOP_PORT=8080
else
  RUN_TARGET="blue"
  STOP_TARGET="green"
  RUN_PORT=8080
  STOP_PORT=8081
fi

echo "🟢 Starting $RUN_TARGET on port $RUN_PORT..."
docker-compose -f "docker-compose-$RUN_TARGET.yml" pull || true
docker-compose -f "docker-compose-$RUN_TARGET.yml" up -d

for i in {1..10}; do
  if curl -s http://127.0.0.1:$RUN_PORT/test | grep -q "blue-green test2"; then
    echo "✅ Health check passed"
    break
  fi
  sleep 3
  if [ $i -eq 10 ]; then
    echo "❌ Health check failed"
    exit 1
  fi
done

WEB_SERVER_USER=ubuntu
WEB_SERVER_HOST=172.31.34.250               # EC2-B Private IP or Domain
NGINX_CONFIG_PATH="/etc/nginx/sites-enabled/default"
EC2_A_PRIVATE_IP=$(hostname -I | awk '{print $1}')

ssh -i ~/.ssh/web-server.pem -o StrictHostKeyChecking=no $WEB_SERVER_USER@$WEB_SERVER_HOST "
  sudo sed -i \"s|proxy_pass http://$EC2_A_PRIVATE_IP:$STOP_PORT;|proxy_pass http://$EC2_A_PRIVATE_IP:$RUN_PORT;|\" $NGINX_CONFIG_PATH &&
  sudo nginx -t &&
  sudo systemctl reload nginx
"

docker stop probono-$STOP_TARGET && docker rm probono-$STOP_TARGET
docker image prune -af