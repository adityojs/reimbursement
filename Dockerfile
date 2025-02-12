# Gunakan Maven untuk build aplikasi
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Gunakan OpenJDK untuk menjalankan aplikasi
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/transaksi-reimbursement-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]

FROM mysql:8.0
ENV MYSQL_ROOT_USER=root
ENV MYSQL_ROOT_PASSWORD=sadewa23
ENV MYSQL_DATABASE=reimbursement_db
EXPOSE 80

