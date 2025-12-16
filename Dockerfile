# Etapa de construcción (Build)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# --- ESTA ES LA LÍNEA NUEVA QUE NECESITAS ---
RUN chmod +x mvnw
# --------------------------------------------

# Descargar dependencias
RUN ./mvnw dependency:resolve
COPY src ./src
# Compilar el proyecto saltando los tests
RUN ./mvnw package -DskipTests

# Etapa de ejecución (Run)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]