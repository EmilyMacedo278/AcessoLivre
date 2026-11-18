
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle/ gradle/
COPY src/ src/

RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew && \
    ./gradlew --no-daemon clean bootJar

RUN JAR=$(find build/libs -name "*.jar" ! -name "*-plain.jar" | head -n 1) && \
    test -n "$JAR" && cp "$JAR" /tmp/app.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /tmp/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
