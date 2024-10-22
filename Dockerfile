# 베이스 이미지 설정
FROM amazoncorretto:21.0.4

WORKDIR /app
# 애플리케이션 JAR 파일을 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 실행 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]