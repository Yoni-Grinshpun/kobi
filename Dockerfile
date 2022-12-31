FROM maven:3.8.6-openjdk-18
COPY . .
RUN mvn clean package
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/dockerized-maven-project-1.0-SNAPSHOT.jar"]