FROM openjdk:19-oracle
WORKDIR /
ADD Tracker.jar /
ADD app/src/main/resources/Database.txt /
EXPOSE 3001
ENTRYPOINT [ "sh", "-c", "java -jar Tracker.jar $my_params" ]

