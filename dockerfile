FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY /target/dataLoader-1.0-SNAPSHOT.jar dataLoader-1.0-SNAPSHOT.jar

COPY db.properties db.properties

ENTRYPOINT ["java", "-Daws.ec2MetadataServiceEndpoint=http://169.254.169.254", "-jar", "dataLoader-1.0-SNAPSHOT.jar"]

