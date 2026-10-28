FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring

COPY services/order-service/target/*.jar app.jar

EXPOSE 8083

USER spring

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=docker"]