FROM maven:3.8.6-openjdk-18 as buildstep
RUN mkdir -p /project/

WORKDIR /project/
RUN git clone https://github.com/SjurWarEagle/mobileDataUsage .
WORKDIR /project/
RUN ls -la
RUN mvn clean package spring-boot:repackage

FROM maven:3.8.6-openjdk-18
RUN mkdir -p /project/
WORKDIR /project
COPY --from=buildstep /project/target/chatGreetings-1.0-SNAPSHOT.jar /project

WORKDIR /project/mobileDataUsage/target
CMD ["java", "-jar", "/project/mobileDataUsage-1.0-SNAPSHOT.jar"]
