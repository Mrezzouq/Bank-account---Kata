FROM openjdk:17-slim AS builder
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY bank-domain bank-domain
COPY bank-infrastructure bank-infrastructure
COPY bank-application bank-application
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew build -x test

FROM openjdk:17-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar bank_account.jar

ENTRYPOINT ["java", "-jar", "bank_account.jar"]
