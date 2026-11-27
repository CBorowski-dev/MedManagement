FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} medmanagementapp.jar
ENTRYPOINT ["java","-jar","/medmanagementapp.jar"]
