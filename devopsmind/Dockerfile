# -------------------------------------------------------------
# - DOCKERFILE
# - AUTOR: @DevSoft Team
# - FECHA: 16-septiebre-2026
# - DESCRIPCION: Docker compose file que permite la
# -              creacion de 1 contendor para el servicio de
# -              devopsmind
# -------------------------------------------------------------
# -------------------------------------------------------------
# docker build \
#  --build-arg BUILD_DATE=$(date -u +'%Y-%m-%dT%H:%M:%SZ') \
#  --build-arg BUILD_VERSION="2.5.0-RC1" \
#  --build-arg BUILD_REVISION=$(git rev-parse --short HEAD 2>/dev/null || echo "unknown") \
#  -t dvd-rental:latest .
# -------------------------------------------------------------
# escape=\ (backslash)

# ==========================================
# FASE 1: Entorno de Compilación (Build Stage)
# ==========================================
# escape=\ (backslash)

FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /build

# Copiar el archivo de definición de dependencias y el wrapper de Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Descargar dependencias en caché para acelerar construcciones futuras
RUN ./mvnw dependency:go-offline -B || true

# Copiar el código fuente y empaquetar aplicando el perfil 'prod' de Maven
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B -Pprod

# 🚀 ESTRATEGIA DE EXTRACCIÓN INFALIBLE: Descomprimir el JAR nativamente para crear las capas
WORKDIR /build/extracted
RUN jar -xf /build/target/*.jar

# ==========================================
# FASE 2: Entorno de Ejecución (Runtime Stage)
# ==========================================
FROM dhi.io/eclipse-temurin:25-alpine AS runner
WORKDIR /app

# 1. Crear un usuario de sistema sin shell interactiva para mitigar exploits de terminal
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

COPY --from=builder /build/extracted/dependencies/ ./
COPY --from=builder /build/extracted/spring-boot-loader/ ./
COPY --from=builder /build/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/extracted/application/ ./

ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:+UnlockDiagnosticVMOptions -Dfile.encoding=UTF-8 -XX:+AlwaysPreTouch -Xss256k"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
