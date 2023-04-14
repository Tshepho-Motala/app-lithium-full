FROM		openjdk:8-jdk

ENV JMETER_VERSION	3.0
ENV JMETER_HOME	/opt/jmeter
ENV JMETER_DOWNLOAD_URL  http://mirror.ufs.ac.za/apache-dist/jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz

# download and extract jmeter 
RUN mkdir -p ${JMETER_HOME}
RUN curl -L ${JMETER_DOWNLOAD_URL} | tar -xz --strip=1 -C ${JMETER_HOME} 
#RUN curl -L --silent http://jmeter-plugins.org/downloads/file/JMeterPlugins-Standard-1.2.1.zip -o /tmp/jmeter-plugins-standard.zip 
#RUN unzip -o -d /opt/jmeter/ /tmp/jmeter-plugins-standard.zip
#RUN rm /tmp/jmeter-plugins-standard.zip

COPY jmeter /jmeter
ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
WORKDIR /jmeter 

ENV HOST=192.168.99.1 PORT=9000 PROTO=http
ENV XMX=1024m XMS=100m 
ENV JOB=default 

CMD /entrypoint.sh
