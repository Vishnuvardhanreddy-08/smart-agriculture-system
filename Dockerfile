# ---------- Stage 1: BUILD the jar file ----------
# This image has Maven and Java 21. It is used only to build, then thrown away.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the project files we need to build.
COPY pom.xml .
COPY src ./src

# Build the jar. -DskipTests skips tests (we have none yet), -B = batch mode (cleaner logs).
RUN mvn -B -q -DskipTests package

# ---------- Stage 2: RUN the app ----------
# A small image with only Java (no Maven, no source code).
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run as a normal user instead of root (safer).
RUN useradd --system --no-create-home appuser

# Copy ONLY the finished jar from stage 1.
COPY --from=build /app/target/*.jar app.jar
USER appuser

# Information only: the app listens on the PORT Render gives it (10000 by default).
EXPOSE 10000

# Start the app. The Java flags are tuned for a small 512 MB server:
#   -XX:+UseSerialGC            a light garbage collector (less memory)
#   -XX:MaxRAMPercentage=65     use at most 65% of the memory for Java's heap
#   -Xss512k                    smaller stacks for threads
#   -XX:TieredStopAtLevel=1     faster start-up on a very small CPU
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-XX:MaxRAMPercentage=65", "-Xss512k", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]