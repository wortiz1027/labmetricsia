# -------------------------------------------------------------
# - DOCKERFILE
# - AUTOR: @DevSoft Team
# - FECHA: 24-Septiembre-2026
# - DESCRIPCION: Docker compose file que permite la
# -              creacion de 1 contendor para el mcp
# -              detalles de maquinas
# -------------------------------------------------------------
# -------------------------------------------------------------
# docker build \
#  --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
#  --build-arg BUILD_VERSION="2.5.0-RC1" \
#  --build-arg BUILD_REVISION=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown") \
#  -t host-telemetry:latest .
# -------------------------------------------------------------
# escape=\ (backslash)

# ==========================================
# FASE 1: Entorno de Compilación (Build Stage)
# ==========================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B || true

COPY src/ src/
RUN ./mvnw clean package -DskipTests

RUN mkdir -p extracted
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ==========================================
# FASE 2: Entorno de Ejecución (Runtime Stage)
# ==========================================
FROM eclipse-temurin:25-jre-alpine AS runner
WORKDIR /app

RUN addgroup -S spring && \
    adduser -S spring -G spring -h /app -s /sbin/nologin && \
    mkdir -p /app/logs && \
    chown -R spring:spring /app && \
    rm -rf /var/cache/apk/* /lib/apk/db/*

USER spring:spring

ARG BUILD_DATE
ARG BUILD_VERSION
ARG BUILD_REVISION

LABEL org.opencontainers.image.created=$BUILD_DATE \
    org.opencontainers.image.authors="@DevSoft Team" \
    org.opencontainers.image.url="https://github.com/wortiz1027/labmetricsia/blob/main/Dockerfile" \
    org.opencontainers.image.documentation="" \
    org.opencontainers.image.source="https://github.com/wortiz1027/labmetricsia/blob/main/Dockerfile" \
    org.opencontainers.image.version=$BUILD_VERSION \
    org.opencontainers.image.revision=$BUILD_REVISION \
    org.opencontainers.image.vendor="Develment Software Team | https://www.devsoft.com.co/" \
    org.opencontainers.image.licenses="" \
    org.opencontainers.image.title="Mcp Server para la gestion de spring ia" \
    org.opencontainers.image.description="Componente encargado de gestionar la informacion de consultas de detalles de maquinas"

COPY --from=builder /build/extracted/dependencies/ ./
COPY --from=builder /build/extracted/spring-boot-loader/ ./
COPY --from=builder /build/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/extracted/application/ ./

ENV JAVA_OPTS="-XX:+UseZGC -Dfile.encoding=UTF-8 -XX:+AlwaysPreTouch -Xss256k"

EXPOSE 8079

ENTRYPOINT ["java", "-jar", "application.jar"]
