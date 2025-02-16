# 베이스 이미지 설정
FROM gradle:8.10.2-jdk21 as build
ENV APP_HOME=/apps
WORKDIR $APP_HOME
COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN chmod +x gradlew

COPY src $APP_HOME/src
RUN ./gradlew clean build -x test


FROM amazoncorretto:21.0.4

ENV TEMP_HOME=/tmp/ffmpeg
WORKDIR $TEMP_HOME

RUN amazon-linux-extras enable epel && \
    yum install -y epel-release wget tar xz && \
    wget https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz && \
    tar -xvf ffmpeg-release-amd64-static.tar.xz && \
    mv ffmpeg-*-amd64-static/ffmpeg /usr/local/bin/ && \
    mv ffmpeg-*-amd64-static/ffprobe /usr/local/bin/ && \
    chmod +x /usr/local/bin/ffmpeg /usr/local/bin/ffprobe && \
    yum clean all



ENV APP_HOME=/apps
ARG ARTIFACT_NAME=app.jar
ARG JAR_FILE_PATH=build/libs/api-0.0.1-SNAPSHOT.jar

WORKDIR $APP_HOME

RUN mkdir -p /data/stt/audio/origin /data/stt/audio/convert && \
    chmod -R 766 /data/stt/audio

#COPY --from=build /apps/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build $APP_HOME/$JAR_FILE_PATH $ARTIFACT_NAME

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]