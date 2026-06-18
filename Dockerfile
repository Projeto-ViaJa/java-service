FROM eclipse-temurin:21-jre-alpine

# Instala tzdata e configura o timezone
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && \
    echo "America/Sao_Paulo" > /etc/timezone && \
    apk del tzdata

WORKDIR /app
COPY target/java-service-1.0-SNAPSHOT.jar java-service-1.0-SNAPSHOT.jar
COPY db.properties db.properties

ENTRYPOINT ["java", "-Daws.ec2MetadataServiceEndpoint=http://169.254.169.254", "-Duser.timezone=America/Sao_Paulo", "-jar", "java-service-1.0-SNAPSHOT.jar"]