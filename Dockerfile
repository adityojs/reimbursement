# Gunakan Maven untuk build aplikasi
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Gunakan OpenJDK untuk menjalankan aplikasi
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/transaksi-reimbursement-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
