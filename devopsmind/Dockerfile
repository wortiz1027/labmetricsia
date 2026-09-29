# -------------------------------------------------------------
# - DOCKERFILE
# - AUTOR: @DevSoft Team
# - FECHA: 16-septiebre-2026
# - DESCRIPCION: Docker compose file que permite la
# -              creacion de 1 contendor para el servicio de
# -              devopsmind
# -------------------------------------------------------------
# ==========================================
# FASE 1: Entorno de Compilación (Build Stage)
# ==========================================
# escape=\ (backslash)
FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B || true

COPY src/ src/
RUN ./mvnw clean package -DskipTests -B -Pprod

# ==========================================
# FASE 2: Entorno de Compilación (Extractor Stage)
# ==========================================
FROM eclipse-temurin:25-jdk-alpine AS extractor
WORKDIR /app

COPY --from=builder /app/target/*.jar application.jar

RUN jar -xf application.jar

# ==========================================
# FASE 2: Entorno de Ejecución (Runtime Stage)
# ==========================================
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring -h /app -s /sbin/nologin && rm -rf /var/cache/apk/* /lib/apk/db/*

USER spring:spring

ARG BUILD_DATE
ARG BUILD_VERSION
ARG BUILD_REVISION

# Informacion de la persona que mantiene la imagen
LABEL org.opencontainers.image.created=$BUILD_DATE \
    org.opencontainers.image.authors="@DevSoft Team" \
    org.opencontainers.image.url="https://github.com/wortiz1027/labmetricsia/blob/main/Dockerfile" \
    org.opencontainers.image.documentation="" \
    org.opencontainers.image.source="https://github.com/wortiz1027/labmetricsia/blob/main/Dockerfile" \
    org.opencontainers.image.version=$BUILD_VERSION \
    org.opencontainers.image.revision=$BUILD_REVISION \
    org.opencontainers.image.vendor="Develment Software Team | https://www.devsoft.com.co/" \
    org.opencontainers.image.licenses="" \
    org.opencontainers.image.title="Backend para la gestion de spring ia" \
    org.opencontainers.image.description="Componente encargado de gestionar la informacion de consultas de devops con modelos llm"

COPY --from=extractor /app/BOOT-INF/lib /app/lib
COPY --from=extractor /app/META-INF /app/META-INF
COPY --from=extractor /app/BOOT-INF/classes /app

ENV JAVA_OPTS="-XX:+UseZGC -Dfile.encoding=UTF-8 -XX:+AlwaysPreTouch -Xss256k -XX:+UseContainerSupport"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp .:./lib/* co.com.devsoft.devopsmind.Application"]
