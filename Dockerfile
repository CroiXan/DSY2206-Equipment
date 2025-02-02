# imagen de java sdk21 con oracle
FROM openjdk:21-jdk

# asignando espacio de trabajo app para contenedor
WORKDIR /app

# copiando jar
COPY target/dsy2206equipments-0.0.1-SNAPSHOT.jar app.jar

# exponiendo puerto 8080 apra el contenedor
EXPOSE 8083

# comandos para ejecucion app
CMD ["java", "-jar", "app.jar"]