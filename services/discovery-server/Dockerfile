FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring

COPY services/discovery-server/target/*.jar app.jar

EXPOSE 8761

USER spring

ENTRYPOINT ["java","-jar","app.jar"]