# Gunakan Maven untuk build aplikasi
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Gunakan OpenJDK untuk menjalankan aplikasi
FROM openjdk:17.0.1-jdk-slim
# WORKDIR /app
# Salin file .jar dari tahap build
COPY --from=build /target/transaksi-reimbursement-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "-Dserver.port=8000", "-Dserver.address=0.0.0.0"]
EXPOSE 3306

# Optional: Menambahkan MySQL sebagai container terpisah (tidak untuk Vercel)
# FROM mysql:8.0
# ENV MYSQL_ROOT_USER=root
# ENV MYSQL_ROOT_PASSWORD=sadewa23
# ENV MYSQL_DATABASE=reimbursement_db
# EXPOSE 3306
