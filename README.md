# fsa-rn

Simple Spring server for providing FSA Reference Numbers. REST API is:

| Verb  | Path                           | Action        | Notes                                               |
|-------|--------------------------------|---------------|-----------------------------------------------------|
| `GET` | `/generate/{authority}/{type}` | Generate a RN | use type `000` for testing, FSA is authority `1000` |
| `GET` | `/decode/{code}`               | Decode a RN   |                                                     |

For details on the Reference Number design and source for the generator see: https://github.com/FoodStandardsAgency/fsa-rn

## Building

To build and test you need a file `src/main/resources/badwords.json` which is not checked in to the git repo.

Obtain the latest version of this from [gdrive](https://drive.google.com/drive/u/1/folders/1Olex5Io_B5y2OHye9DcgyfDI2GKZOwz8). The file is gitignored, so this is safe.

```sh
make image
```

Reusable workflow and CI/CD are not currently supported due to the management of the badwords.json list.

Publish with:

```sh
AWS_PROFILE=fsa GITHUB_RUN_NUMBER=nn make publish
```

## Running

```sh
# run dockerfile
docker run -it -p 8080:8080 {{ Name of outputted docker image }}
```

## Configuration

The specification requires that no two services are running with the same instance number. If you are running more than one server you can set the instance number with
```sh
#docker
docker run -it -p 8080:8080 {{ docker image }} sh -c "/run_app.sh --fsa-rn.instance={instance number}"
```

### Instance number allocation

| Usage | Instance number range |
|---|---|
| Epimorphics RN service | 0-9, currently 0, 1 in use |
| Epimorphics UV service | 10-19, currently 10 in use |
| FSA usage | 900 - 999 |

## Changelog

`0.0.8`  Updated for java 21, which requires use of `--add-opens`, see bin/run_app.sh

`0.0.7`  Original version deploy to FSA cluster
