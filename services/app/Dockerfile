FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gredlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gredlew && ./gredlew --no-daemon dependencies

COPY src src
RUN ./gredlew --no-daemon bootJar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]