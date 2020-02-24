FROM openjdk:11-jdk

WORKDIR /

ADD /target/googliapparatus-0.0.1.jar googliapparatus-0.0.1.jar

EXPOSE 8080

CMD java -jar googliapparatus-0.0.1.jar
