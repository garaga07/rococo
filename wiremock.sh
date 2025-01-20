#!/bin/bash

docker pull wiremock/wiremock:2.35.0

docker run --name rococo-mock \
  -p 8080:8080 \
  -v "D:/IdeaProjects/rococo/wiremock/rest:/home/wiremock" \
  wiremock/wiremock:2.35.0 \
  --global-response-templating --enable-stub-cors