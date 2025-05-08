FROM eclipseecsp/ecsp-base-java17-tomcat10-apr:1.0.0

#ARG ENVIRONMENT
#RUN echo ${ENVIRONMENT}

ENV CATALINA_OPTS="-server -Xmx2G -Xms1G -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"
ADD /target/device-factory-management.war /tmp/hcp-deviceinfoquery-webapp.war
COPY src/scripts/* /opt/hcp-deviceinfoquery-webapp/bin/
# Add conf directory
ADD config/dev/ /usr/local/tomcat/conf/hcp-deviceinfoquery-webapp/

# Add context file
ADD config/dev/context.xml /usr/local/tomcat/conf/Catalina/localhost/hcp-deviceinfoquery-webapp.xml

RUN chmod 755 /opt/hcp-deviceinfoquery-webapp/bin/start.sh

RUN rm -rf /usr/local/tomcat/webapps/* && \
    mv /tmp/hcp-deviceinfoquery-webapp.war /usr/local/tomcat/webapps/
RUN addgroup --system appgroup
RUN adduser --system appuser --ingroup appgroup
RUN chown -R appuser:appgroup /usr/local/tomcat/
USER appuser
ENTRYPOINT /bin/sh /opt/hcp-deviceinfoquery-webapp/bin/start.sh
