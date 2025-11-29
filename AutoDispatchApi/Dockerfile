# Stage 1: Cache Gradle dependencies
FROM gradle:jdk21-alpine AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.* settings.gradle.* gradle.properties /home/gradle/app/
COPY gradle /home/gradle/app/gradle
WORKDIR /home/gradle/app
RUN gradle clean assemble --no-daemon

# Stage 2: Build Application
FROM gradle:jdk21-alpine AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# Stage 3: Create the Runtime Image
FROM amazoncorretto:21-alpine-jdk AS runtime
EXPOSE 8084
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
RUN mkdir /uploads

ENTRYPOINT ["java","-jar","/app/app.jar"]