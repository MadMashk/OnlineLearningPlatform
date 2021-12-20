FROM openjdk:15
ARG JAR_FILE=./target/WebApp-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]



