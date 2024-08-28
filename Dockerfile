# Use uma imagem base do Maven para buildar a aplicação
FROM maven:3.9.9-eclipse-temurin-22 as build

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo pom.xml
COPY pom.xml .

# Baixe todas as dependências do projeto
RUN mvn dependency:go-offline

# Copia o código da aplicação
COPY src ./src

# Compila o código e empacote como um arquivo JAR
RUN mvn clean package -DskipTests

# Use uma imagem mais leve para rodar a aplicação
FROM openjdk:22-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]