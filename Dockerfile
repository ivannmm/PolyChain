FROM gradle:8-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:11
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/PolyChain.jar /app/PolyChain.jar