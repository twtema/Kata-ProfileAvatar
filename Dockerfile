FROM docker.io/eclipse-temurin:17.0.7_7-jre

WORKDIR /app
COPY target/ProfileAvatar.jar /app/ProfileAvatar.jar

EXPOSE 8083
CMD ["java", "-jar", "ProfileAvatar.jar"]