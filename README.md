Googli Apparatus
a searchable database of phish lyrics

To build and run in docker:
mvn clean install
docker build -t googliapparatus .
docker run googliapparatus

To build and run with maven:
mvn clean install
mvn spring-boot:run

To push to docker:
docker push dolandrew/googliapparatus   