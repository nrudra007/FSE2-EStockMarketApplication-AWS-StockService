FROM openjdk:8
EXPOSE 8300
ADD target/stock-service.jar stock-service.jar
ENTRYPOINT ["java","-jar","/stock-service.jar"]