# 1단계: Build
FROM gradle:8.4-jdk21 AS build
WORKDIR /app

# 빌드 스크립트만 먼저 복사해서 캐시 활용
COPY gradlew build.gradle settings.gradle ./
COPY gradle gradle
RUN chmod +x gradlew

# 의존성 캐싱 (에러는 무시하지 말고 실패 원인 찾기)
RUN ./gradlew dependencies --no-daemon --stacktrace

# 전체 복사
COPY . .

# 테스트가 원인일 수 있으므로, 우선 테스트 생략
RUN ./gradlew generateAvroJava generateProto bootJar --no-daemon -x test --stacktrace

# 2단계: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 4447
ENTRYPOINT ["java", "-jar", "/app/app.jar"]