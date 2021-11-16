FROM openjdk:11
COPY "./target/Tongue-0.0.2-SNAPSHOT.jar" "app.jar"
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]