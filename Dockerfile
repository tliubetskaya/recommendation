FROM gradle:8-jdk21-jammy as build

WORKDIR /build

COPY . .

WORKDIR /generated

COPY . .

RUN ./gradlew bootJar --info

FROM gradle:8-jdk21-jammy as debug

WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar

WORKDIR /build

COPY --from=build /build/ /build/

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]