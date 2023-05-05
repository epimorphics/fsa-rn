# fsa-rn

Simple Spring server for providing FSA Reference Numbers. REST API is:

| Verb  | Path                           | Action        | Notes                                               |
|-------|--------------------------------|---------------|-----------------------------------------------------|
| `GET` | `/generate/{authority}/{type}` | Generate a RN | use type `000` for testing, FSA is authority `1000` |
| `GET` | `/decode/{code}`               | Decode a RN   |                                                     |

For details on the Reference Number design and source for the generator see: https://github.com/FoodStandardsAgency/fsa-rn

## Building

To build and test you need a file `src/main/resources/badwords.json` which is not checked in to the git repo.

Obtain the latest version of this from [gdrive](https://drive.google.com/drive/u/1/folders/1Olex5Io_B5y2OHye9DcgyfDI2GKZOwz8). The file is gitignored to is safe.

```sh
# build jar
mvn package

# build docker image
mvn install dockerfile:build
```

In some dev environments (windows) you may need to use the provided `mvnw` script instead of `mvn`.

## Running

```sh
# run jar
java -jar fsa-reference-numbers-0.0.2-SNAPSHOT.jar

# run dockerfile
docker run -it -p 8080:8080 {{ Name of outputted docker image }}
```

## Configuration

The specification requires that no two services are running with the same instance number. If you are running more than one server you can set the instance number with
```sh
# jar
java -jar target/fsa-reference-numbers-0.0.2-SNAPSHOT.jar --fsa-rn.instance=(Instance number)

#docker
docker run -it -p 8080:8080 {{ docker image }} --fsa-rn.instance=(Instance number)
```

## Instance number allocation

| Usage | Instance number range |
|---|---|
| Epimorphics RN service | 0-9, currently 0, 1 in use |
| Epimorphics UV service | 10-19, currently 10 in use |
| FSA usage | 900 - 999 |
