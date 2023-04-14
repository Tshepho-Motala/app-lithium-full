#!/bin/bash

DEFAULTURL="http://192.168.99.1:9800"

if [ -z "$URL" ]; then 
	URL=$DEFAULTURL;
fi

echo "Connecting to $URL"
echo "Override using the environment variable URL"

while ! timeout 1 wget -qO- http://selenium:$SELENIUM_PORT_4444_TCP_PORT ; do echo "Waiting for selenium..."; sleep 1; done;

exec protractor conf.js --params.url=$URL
