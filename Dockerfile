FROM openjdk:17-oracle
WORKDIR /app
COPY target/mc-account-0.0.1-SNAPSHOT.jar mc-account.jar
CMD ["java", "-jar", "mc-account.jar"]
