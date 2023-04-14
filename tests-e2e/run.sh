#!/bin/bash

if [ ! -z GATEWAY_HOST ]; then
  GATEWAY_HOST=192.168.99.1
fi
if [ ! -z GATEWAY_HOST ]; then
  GATEWAY_PROTO=http
fi
if [ ! -z GATEWAY_PORT ]; then
  GATEWAY_PORT=9000
fi

if [ ! -z UI_NETWORD_ADMIN_URL ]; then
  UI_NETWORK_ADMIN_URL=http://192.168.99.1:9800
fi

# Build and run the jmeter docker

#For openshift, build and push to our registry instead like:
# docker build -t registry.playsafesa.com:5000/lithium/jmeter:develop .
# docker push registry.playsafesa.com:5000/lithium/jmeter:develop 

docker build -t lithium_jmeter jmeter || exit 1
docker run -it --rm \
	-v /tmp/jmeter:/tmp/jmeter \
	-e "PROTO=$GATEWAY_PROTO" \
	-e "HOST=$GATEWAY_HOST" \
	-e "PORT=$GATEWAY_PORT" \
	lithium_jmeter

# Build and run the protractor docker and its dependencies

docker-machine ssh default "sudo rm -rf /tmp/videos/*" || exit 1
docker build -t lithium_protractor protractor || exit 1
docker run -d --name=lithium_selenium -p 4444:24444 -p 5900:25900 -p 6080:26080 \
	-v /tmp/videos:/videos \
	-v /dev/shm:/dev/shm \
	-e "VIDEO=true" -e "VNC_PASSWORD=test" -e "NOVNC=true" \
	elgalu/selenium || exit 1
docker exec lithium_selenium wait_all_done 30s || exit 1
docker run -it --rm --link lithium_selenium:selenium \
	-e "URL=$UI_NETWORK_ADMIN_URL" \
	lithium_protractor
docker stop lithium_selenium || exit 1
docker rm lithium_selenium || exit 1
docker-machine scp default:/tmp/videos/* videos/ || exit 1

