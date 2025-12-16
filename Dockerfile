# Etapa de construcción (Build)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Descargar dependencias (esto cachea las librerías para que sea más rápido)
RUN ./mvnw dependency:resolve
COPY src ./src
# Compilar el proyecto saltando los tests (para ahorrar tiempo en deploy)
RUN ./mvnw package -DskipTests

# Etapa de ejecución (Run)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Copiar el jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Exponer el puerto 8080
EXPOSE 8080
# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]