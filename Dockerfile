FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY sistema-control-inventario-bodega-automotriz/.mvn/ .mvn/
COPY sistema-control-inventario-bodega-automotriz/mvnw sistema-control-inventario-bodega-automotriz/pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY sistema-control-inventario-bodega-automotriz/src/ src/
RUN ./mvnw -B clean package

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN groupadd --system --gid 10001 spring \
    && useradd --system --uid 10001 --gid spring --no-create-home spring

COPY --from=build --chown=10001:10001 \
    /workspace/target/sistema-control-inventario-bodega-automotriz-0.0.1-SNAPSHOT.jar \
    /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8081

USER 10001:10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
