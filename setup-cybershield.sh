#!/bin/bash

# ==========================================
# SETUP PROJET CYBERSHIELD 360 (BACKEND)
# Architecture : Hexagonale (2 Modules)
# Stack : Java 22, Spring Boot 3, Netty
# ==========================================

PROJECT_NAME="backend-protection"
GROUP_ID="com.cybershield"
JAVA_VERSION="22"
SPRING_BOOT_VERSION="3.2.1"

echo "🚀 Initialisation du projet $PROJECT_NAME..."

# 1. Création du dossier racine
mkdir -p $PROJECT_NAME
cd $PROJECT_NAME

# ==========================================
# 2. GENERATION DU POM PARENT (Aggregator)
# ==========================================
echo "📦 Création du POM Parent..."
cat <<EOF > pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>$GROUP_ID</groupId>
    <artifactId>$PROJECT_NAME</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>CyberShield Backend Protection</name>

    <modules>
        <module>cybershield-core</module>
        <module>cybershield-service</module>
    </modules>

    <properties>
        <java.version>$JAVA_VERSION</java.version>
        <maven.compiler.source>$JAVA_VERSION</maven.compiler.source>
        <maven.compiler.target>$JAVA_VERSION</maven.compiler.target>
        <spring-boot.version>$SPRING_BOOT_VERSION</spring-boot.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>\${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
EOF

# ==========================================
# 3. MODULE 1 : CYBERSHIELD-CORE (Java Pur)
# ==========================================
echo "💎 Création du module CORE (Domain)..."
mkdir -p cybershield-core/src/main/java/com/cybershield/protection/core/{domain,port/in,port/out,usecase}
mkdir -p cybershield-core/src/test/java/com/cybershield/protection/core

# POM du Core
cat <<EOF > cybershield-core/pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>$GROUP_ID</groupId>
        <artifactId>$PROJECT_NAME</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>cybershield-core</artifactId>

    </project>
EOF

# Exemple de classe Domain
cat <<EOF > cybershield-core/src/main/java/com/cybershield/protection/core/domain/Device.java
package com.cybershield.protection.core.domain;

import lombok.Data;
import java.util.UUID;

@Data
public class Device {
    private UUID id;
    private String macAddress;
    private boolean isProtected;
}
EOF

# ==========================================
# 4. MODULE 2 : CYBERSHIELD-SERVICE (Infra)
# ==========================================
echo "⚙️ Création du module SERVICE (Spring Boot)..."
mkdir -p cybershield-service/src/main/java/com/cybershield/protection/{adapter/in/rest,adapter/out/persistence,adapter/out/event,config}
mkdir -p cybershield-service/src/main/resources

# POM du Service
cat <<EOF > cybershield-service/pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>$GROUP_ID</groupId>
        <artifactId>$PROJECT_NAME</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>cybershield-service</artifactId>

    <dependencies>
        <dependency>
            <groupId>$GROUP_ID</groupId>
            <artifactId>cybershield-core</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-otel</artifactId>
        </dependency>
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-exporter-otlp</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# Classe Principale Spring Boot
cat <<EOF > cybershield-service/src/main/java/com/cybershield/protection/CyberShieldApplication.java
package com.cybershield.protection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CyberShieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(CyberShieldApplication.class, args);
    }
}
EOF

# Configuration Application.yml (Sécurisée)
cat <<EOF > cybershield-service/src/main/resources/application.yml
server:
  port: 8080

spring:
  application:
    name: backend-protection
  datasource:
    url: jdbc:postgresql://localhost:5432/cybershield_db
    username: admin
    password: securepassword123
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  data:
    redis:
      host: localhost
      port: 6379

# Configuration ACTUATOR Sécurisée
management:
  server:
    port: 8081 # Port Admin Séparé
  endpoints:
    web:
      exposure:
        include: "health,metrics,prometheus,loggers"
        exclude: "env,beans,heapdump,mappings"
  endpoint:
    health:
      show-details: "ALWAYS"
    loggers:
      enabled: true
EOF

# ==========================================
# 5. DOCKER COMPOSE (Infra Locale)
# ==========================================
echo "🐳 Génération du docker-compose.yml..."
cat <<EOF > docker-compose.yml
version: '3.8'

services:
  # Base de données PostgreSQL
  db-core:
    image: postgres:15-alpine
    container_name: cyber-db
    environment:
      POSTGRES_DB: cybershield_db
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: securepassword123
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - cyber-net

  # Redis (Bus d'événements)
  broker:
    image: redis:7-alpine
    container_name: cyber-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - cyber-net

  # Jaeger (Tracing OpenTelemetry)
  jaeger:
    image: jaegertracing/all-in-one:1.50
    container_name: cyber-jaeger
    environment:
      - COLLECTOR_OTLP_ENABLED=true
    ports:
      - "16686:16686" # UI Dashboard
      - "4317:4317"   # Port OTLP gRPC
    networks:
      - cyber-net

volumes:
  postgres_data:
  redis_data:

networks:
  cyber-net:
    driver: bridge
EOF

echo "✅ PROJET GÉNÉRÉ AVEC SUCCÈS !"
echo "👉 Prochaine étape : Ouvre le dossier '$PROJECT_NAME' avec IntelliJ IDEA ou VSCode."
echo "👉 Lance l'infra avec : docker-compose up -d"