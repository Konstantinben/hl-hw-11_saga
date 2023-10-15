FROM marceloagmelo/maven-3.6.3-openjdk-11 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

FROM openjdk:11
COPY --from=build /home/app/target/order-service-1.0-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8080