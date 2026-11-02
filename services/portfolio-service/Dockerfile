FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring

COPY services/portfolio-service/target/*.jar app.jar

EXPOSE 8084

USER spring

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]