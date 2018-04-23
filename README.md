# fsa-rn

Simple Spring server for providing FSA Reference Numbers

/fsa-rn/{authority}/{type}

## Building

Currently the reference numbers jar is not available, first install that into your local maven repository

```sh
git clone git@github.com:FoodStandardsAgency/fsa-rn.git
cd fsa-rn
cd java-rn
mvn package
mvn install:install-file \
   -Dfile=target/java-rn-0.0.1-SNAPSHOT.jar \
   -DgroupId=uk.gov.food \
   -DartifactId=rn \
   -Dversion=0.0.1-SNAPSHOT \
   -Dpackaging=jar \
   -DgeneratePom=true
```

Now return to this directory

```sh
# build jar
./mvnw package

# build docker image
./mvnw install dockerfile:build
```

## Running

```sh
# run jar
java -jar fsa-reference-numbers-0.0.1-SNAPSHOT.jar

# run dockerfile
docker run -it -p 8080:8080 {{ Name of outputted docker image }}
```

## Configuration

The specification requires that no two services are running with the same instance number. If you are running more than one server you can set the instance number with
```sh
# jar
java -jar target/fsa-reference-numbers-0.0.1-SNAPSHOT.jar --fsa-rn.instance=(Instance number)

#docker
docker run -it -p 8080:8080 {{ docker image }} --fsa-rn.instance=(Instance number)
```
