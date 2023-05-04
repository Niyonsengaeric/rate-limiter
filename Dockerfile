#
# Build stage
#
FROM maven:3.8.2-jdk-8 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:8-jdk-slim
COPY --from=build /target/ratelimiter-0.0.1-SNAPSHOT.jar ratelimiter.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","ratelimiter.jar"]
