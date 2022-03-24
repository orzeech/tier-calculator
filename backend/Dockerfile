FROM openjdk:14-jdk-alpine
MAINTAINER orzechsoft.pl
VOLUME /tmp

EXPOSE 8080
COPY ./target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
