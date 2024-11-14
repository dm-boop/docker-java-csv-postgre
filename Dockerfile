FROM openjdk:24-jdk-bullseye
WORKDIR /app
COPY ./app/out/artifacts/app_jar/app.jar /app/app.jar
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
CMD ["/wait-for-it.sh", "postgres:5432", "--", "java", "-jar", "app.jar"]
#CMD ["java", "-jar", "app.jar"]