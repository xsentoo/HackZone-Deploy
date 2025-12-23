# --- Étape 1 : On construit l'appli (Build) ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# On copie tout le code source dans l'image
COPY . .
# On lance la compilation (création du .jar)
RUN mvn clean package -DskipTests

# --- Étape 2 : On prépare l'image finale (Run) ---
FROM eclipse-temurin:21-jdk
WORKDIR /app
# On récupère le .jar qu'on vient de fabriquer à l'étape 1
COPY --from=build /app/target/*.jar app.jar
# On lance l'appli
ENTRYPOINT ["java", "-jar", "app.jar"]