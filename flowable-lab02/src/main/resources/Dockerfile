FROM java:8-jre
MAINTAINER yalinyee lover_ya@sina.com

ENV TZ=Asia/Shanghai
RUN apt-get install tzdata
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY target/Flowablelab02.jar  Flowablelab02.jar

CMD ["java", "-Xmx1024m", "-jar", "Flowablelab02.jar"]

# HEALTHCHECK --interval=120s --timeout=60s CMD curl -f http://device-service:8080/actuator/health || exit 1

EXPOSE 8080
