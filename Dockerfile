FROM azul/zulu-openjdk:21-latest AS builder
WORKDIR /application
ARG JAR_FILE=build/libs/spring-k8s-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher

FROM azul/zulu-openjdk:21-latest
EXPOSE 8080
WORKDIR /application
COPY --from=builder /application/application/dependencies/ ./
COPY --from=builder /application/application/snapshot-dependencies/ ./
COPY --from=builder /application/application/spring-boot-loader/ ./
COPY --from=builder /application/application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]