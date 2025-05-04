FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/auth-server-0.0.1-SNAPSHOT.jar auth-server.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "auth-server.jar"]
