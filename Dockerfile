FROM openjdk:17-alpine

ADD target/helmify-0.0.1-SNAPSHOT.jar helmify.jar

CMD ["java", "-jar", "helmify.jar"]