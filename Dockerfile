FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} medmanagementapp.jar
ENTRYPOINT ["java","-jar","/medmanagementapp.jar"]