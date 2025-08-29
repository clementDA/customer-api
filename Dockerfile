# Image de base avec Java 17
FROM eclipse-temurin:17-jdk-alpine

# Répertoire de travail dans le conteneur
WORKDIR /app

# Copier le jar construit par Maven
COPY target/customer-api-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port de l'application
EXPOSE 8081

# Commande de démarrage
ENTRYPOINT ["java","-jar","app.jar"]