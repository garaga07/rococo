FROM eclipse-temurin:21-jdk

WORKDIR /rococo-tests
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./rococo-tests ./rococo-tests
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD ./gradlew testSmoke -Dtest.env=docker -Duser.timezone=Europe/Moscow