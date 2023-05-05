.PHONY:	clean image publish test vars

ACCOUNT?=$(shell aws sts get-caller-identity | jq -r .Account)
NAME?=$(shell awk -F: '$$1=="name" {print $$2}' deployment.yaml | sed -e 's/\s//g')
STAGE?=dev
ECR?=${ACCOUNT}.dkr.ecr.eu-west-1.amazonaws.com
IMAGE?=${NAME}/${STAGE}
REPO?=${ECR}/${IMAGE}

BRANCH:=$(shell git rev-parse --abbrev-ref HEAD)
COMMIT:=$(shell git rev-parse --short HEAD)
MVN_VERSION:=$(shell mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
TAG?=$(shell printf '%s_%s_%08d' ${MVN_VERSION} ${COMMIT} ${GITHUB_RUN_NUMBER})

MVN=mvn \
		-Dbuild.date=`date -Iseconds` \
		-Ddocker.image=${IMAGE} \
		-Ddocker.tag=${TAG} \
		-Dgit.branch=${BRANCH} \
		-Dgit.commit=${COMMIT}
		-Dgithub.run.number=${GITHUB_RUN_NUMBER}

image:
	@echo Building ${IMAGE}:${TAG} ...
	${MVN} package dockerfile:build
	@echo Tagging ${IMAGE}:latest ...
	@docker tag ${IMAGE}:${TAG} ${IMAGE}:${MVN_VERSION}
	@docker tag ${IMAGE}:${TAG} ${IMAGE}:latest

publish: image
	@echo Tagging ${REPO}:${TAG} ...
	@docker tag ${IMAGE}:${TAG} ${REPO}:${TAG}
	@docker tag ${IMAGE}:${TAG} ${REPO}:${MVN_VERSION}
	@docker tag ${IMAGE}:${TAG} ${REPO}:latest
	@echo Publishing ${REPO} ...
	@docker push ${REPO} --all-tags
	@echo Done.

tag:
	@echo ${TAG}

test:
	@mvn test

clean:
	@mvn clean

vars:
	@echo NAME:${NAME}
	@echo MVN_VERSION:${MVN_VERSION}
	@echo BRANCH:${BRANCH}
	@echo COMMIT:${COMMIT}
	@echo IMAGE:${IMAGE}
	@echo REPO:${REPO}
	@echo TAG:${TAG}
