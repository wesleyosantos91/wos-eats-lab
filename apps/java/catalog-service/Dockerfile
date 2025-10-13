# 1) Build com melhorias
FROM maven:3.9.11-amazoncorretto-25-alpine AS build
WORKDIR /workspace
COPY pom.xml .
# Cache mais eficiente das dependências
RUN mvn dependency:go-offline -B -Dmaven.repo.local=/tmp/maven-repo
COPY src src
RUN mvn -DskipTests clean package -Ptraining -Dmaven.repo.local=/tmp/maven-repo

# 2) Training para AppCDS com Java 25 features
FROM amazoncorretto:25.0.0-alpine3.22 AS appcds
WORKDIR /opt/app
COPY --from=build /workspace/target/*.jar app.jar

# Usar CDS Archive melhorado do Java 25
RUN java -Xmx2g \
    -XX:ArchiveClassesAtExit=/opt/app/application.jsa \
    -XX:+UnlockExperimentalVMOptions \
    -XX:+UseEpsilonGC \
    -Dspring.context.exit=onRefresh \
    -Dserver.port=8080 \
    -Dspring.profiles.active=training \
    -jar /opt/app/app.jar || true

# 3) Runtime otimizado
FROM amazoncorretto:25.0.0-alpine3.22 AS runtime
WORKDIR /opt/app

RUN apk add --no-cache libstdc++ curl && \
    addgroup -S appuser && adduser -S appuser -G appuser && \
    # Melhor segurança: diretório separado para app
    mkdir -p /opt/app/logs /opt/app/tmp && \
    chown -R appuser:appuser /opt/app

COPY --from=build  /workspace/target/*.jar  /opt/app/app.jar
COPY --from=appcds /opt/app/application.jsa /opt/app/application.jsa

USER appuser

# Healthcheck mais robusto
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8090/actuator/health || exit 1

ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8090

# Configurações JVM otimizadas para Java 25
ENV JVM_MEM="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=25 -XX:MinRAMPercentage=25 -XX:+AlwaysPreTouch -XX:+UseTransparentHugePages"
ENV JVM_GC="-XX:MaxGCPauseMillis=100 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=8m -XX:+ParallelRefProcEnabled -XX:+UseStringDeduplication -XX:+UseG1GC"
ENV JVM_CDS="-Xshare:on -XX:SharedArchiveFile=/opt/app/application.jsa -XX:+VerifySharedSpaces"
ENV JVM_CPU="-XX:+UseContainerSupport -XX:ActiveProcessorCount=2"
ENV JVM_LOGGING="-Xlog:gc*,safepoint=info:file=/opt/app/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=10m"
ENV JVM_SECURITY="-Djava.security.egd=file:/dev/./urandom -Djdk.tls.disabledAlgorithms=SSLv3, TLSv1, TLSv1.1, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL"

EXPOSE 8090

# Entrypoint com melhor tratamento de sinais e logging
ENTRYPOINT ["sh", "-c", "exec java $JVM_CPU $JVM_CDS $JVM_MEM $JVM_GC $JVM_LOGGING $JVM_SECURITY -jar /opt/app/app.jar"]