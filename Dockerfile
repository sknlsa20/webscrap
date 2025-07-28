FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/webscrapping-1-0.0.1-SNAPSHOT.jar demo1.jar
EXPOSE 9999
ENTRYPOINT ["java","-jar","demo1.jar"]