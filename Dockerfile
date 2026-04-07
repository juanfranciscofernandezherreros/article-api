# -----------  build stage  -----------
FROM eclipse-temurin:17-jdk AS build

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy POM first for better layer caching
COPY pom.xml ./

# Download dependencies (cached unless pom.xml changes).
# "|| true" because jitpack.io artifacts may not resolve in go-offline mode;
# the actual build step will fetch any remaining dependencies.
RUN mvn dependency:go-offline -B || true

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests -B

# -----------  runtime stage  -----------
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
