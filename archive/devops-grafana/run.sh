#!/bin/bash

docker run -d --name=graphite -p 80:80 -p 2003:2003 hopsoft/graphite-statsd || exit 1
docker run -d --name=grafana \
	--link graphite:graphite \
	-p 3000:3000 grafana/grafana || exit 1

