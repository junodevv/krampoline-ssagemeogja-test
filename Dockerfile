# 기본이미지
FROM krmp-d2hub-idock.9rum.cc/goorm/gradle:7.3.1-jdk17
# 워크 디렉토리
WORKDIR /home/project
# Spring 소스 코드를 이미지에 복사
COPY . .
# gradle 빌드 시 proxy 설정을 gradle.properties에 추가
RUN echo "systemProp.http.proxyHost=krmp-proxy.9rum.cc\nsystemProp.http.proxyPort=3128\nsystemProp.https.proxyHost=krmp-proxy.9rum.cc\nsystemProp.https.proxyPort=3128" > /root/.gradle/gradle.properties
# DATABASE_URL, KRAMPOLINE_IP/PORT 를 환경변수로 설정
ENV DATABASE_URL=${DATABASE_URL}
ENV KRAMPOLINE_HOST_IP=${KRAMPOLINE_HOST_IP}
ENV KRAMPOLINE_HOST_PORT=${KRAMPOLINE_HOST_PORT}
# gradlew에 실행 권한 부여 및 프로젝트 필드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test -Pjasypt.encryptor.password=${JASYPT_ENCRYPTOR_PASSWORD}
# 빌드 결과 jar 파일을 실행
CMD ["java", "-jar", "/home/project/build/libs/back-0.0.1-SNAPSHOT.jar"]
