FROM amazoncorretto:17-alpine
WORKDIR /app
COPY target/book-library-0.0.1-SNAPSHOT.jar book-library.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "book-library.jar"]
