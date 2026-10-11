# ---------- Stage 1: BUILD the jar file ----------
# This image has Maven and Java 21. It is used only to build, then thrown away.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the project files we need to build.
# maven-settings.xml tells Maven to download libraries from a mirror of Maven Central,
# because Maven Central itself often answers "429 Too Many Requests" to shared build servers.
COPY maven-settings.xml .
COPY pom.xml .
COPY src ./src

# Build the jar. Flags: -B = batch mode, -ntp = no download progress lines (shorter logs),
# -DskipTests = skip tests (we have none yet), -s = use our settings file.
# The loop tries up to 3 times. Each time it first uses the mirror, then Maven Central directly.
# Libraries already downloaded are kept between attempts. "exit 0" stops the loop on success.
RUN for attempt in 1 2 3; do \
      mvn -B -ntp -s maven-settings.xml -DskipTests package && exit 0; \
      echo "Attempt $attempt with the mirror failed. Trying Maven Central directly..."; \
      mvn -B -ntp -DskipTests package && exit 0; \
      echo "Attempt $attempt failed. Waiting 30 seconds before the next try..."; \
      sleep 30; \
    done; \
    exit 1

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