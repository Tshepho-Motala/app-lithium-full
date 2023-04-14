#!/bin/bash
set JVM_ARGS="-Xms$XMS -Xmx$XMX -Dpropname=propvalue"

mkdir /tmp/jmeter

touch /tmp/jmeter.log

/opt/jmeter/bin/jmeter -n -t /jmeter/$JOB.jmx -l /tmp/jmeter/samples.log -j /tmp/jmeter/jmeter.log \
	-Jhost=$HOST -Jport=$PORT -Jproto=$PROTO || ( cat /tmp/jmeter/*; exit 10)
