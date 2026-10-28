FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring

COPY services/api-gateway/target/*.jar app.jar

EXPOSE 9090

USER spring

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]