FROM gradle:8.7-jdk21 AS builder

USER gradle
WORKDIR /app

COPY --chown=gradle:gradle gradle gradle/
COPY --chown=gradle:gradle gradlew ./
RUN chmod +x gradlew

COPY --chown=gradle:gradle build.gradle ./
#COPY --chown=gradle:gradle settings.gradle ./
#COPY --chown=gradle:gradle gradle.properties ./

COPY --chown=gradle:gradle src src/

RUN ./gradlew build --no-daemon -Dorg.gradle.vfs.watch=false


  # First, ensure Java/JDK is working
#RUN java -version && \
#javac -version
#  
#  # Try to build with debugging
#RUN if [ -f "gradlew" ]; then \
#chmod +x gradlew && \
#./gradlew tasks --no-daemon || true && \
#./gradlew build --no-daemon --stacktrace; \
#else \
#gradle build --no-daemon --stacktrace; \
#fi
  
  # Run stage
#FROM eclipse-temurin:21-jdk-jammy
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","--enable-preview", "-jar", "app.jar"]