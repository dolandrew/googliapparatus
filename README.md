# Googli Apparatus Backend

A searchable database of phish lyrics

## To build and run in docker:

```
mvn clean install
docker build -t googliapparatus .
docker run googliapparatus
```

## To build and run with maven:

```
mvn clean install
mvn spring-boot:run
```

* Merges to the master branch are deployed to docker hub.
