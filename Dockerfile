# 1단계: 빌드
FROM gradle:9.2-jdk21 AS build

WORKDIR /app

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle /app/
RUN gradle build -x test --parallel > /dev/null 2>&1 || true

# 소스 코드 복사 및 빌드
COPY src /app/src
RUN gradle clean bootJar -x test

# 2단계: Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 시간대 설정 (Seoul)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드 결과물 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# 컨테이너 실행 명령
ENTRYPOINT ["java", \
            "-jar", \
            "-Dspring.profiles.active=prd", \
            "-Dhttps.protocols=TLSv1.2", \
            "-Djdk.tls.client.protocols=TLSv1.2", \
            "-Djdk.tls.disabledAlgorithms=SSLv3, RC4, DES, MD5withRSA, DH keySize < 1024", \
            "-Djdk.tls.legacyAlgorithms=NULL, RC4, DES, 3DES, MD5, SHA1", \
            "-Djavax.net.debug=ssl,handshake", \
            "app.jar"]