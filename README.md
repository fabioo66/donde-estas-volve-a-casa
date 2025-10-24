# Configuracion para el ambiente de desarrollo
Este repositorio contiene la configuración necesaria para establecer un ambiente de desarrollo local utilizando Docker.

## Requisitos Previos
- [Docker](https://www.docker.com/get-started)
- [Java Development Kit (JDK) 25](https://www.oracle.com/java/technologies/javase-jdk25-downloads.html)
- [Tomcat 10.1.48](https://tomcat.apache.org/download-10.cgi)

## Configuración del Proyecto
1. Clona este repositorio en tu máquina local:
   ```bash
   git clone https://github.com/fabioo66/donde-estas-volve-a-casa.git
    cd donde-estas-volve-a-casa
    ```
2. Navega al directorio del proyecto:
3. Construye y levanta los contenedores de Docker. En linux
   ```bash
   docker run -d \
    --name proyectoTTPS \
    -e MYSQL_ROOT_PASSWORD=valen \
    -e MYSQL_DATABASE=proyectoTTPS \
    -e MYSQL_USER=valen \
    -e MYSQL_PASSWORD=valen \
    -p 3307:3306 \
    mysql:8.0
   ```
    En Windows:
    ```bash
   docker run -d --name proyectoTTPS -e MYSQL_ROOT_PASSWORD=valen -e MYSQL_DATABASE=proyectoTTPS -e MYSQL_USER=valen -e MYSQL_PASSWORD=valen -p 3307:3306 mysql:8.0
    ```
4. Correr Tomcat 10.1.48 y desplegar el archivo WAR en el directorio `webapps` de Tomcat.
5. Correr docker 
6. Ejecutar el archivo src/main/java/ttps/utils/DatabaseInitializer.java para crear las tablas.
7. Ejecutar los tests unitarios
