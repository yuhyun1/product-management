FROM openjdk:23
ARG JAR_FILE=build/libs/product-management-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]