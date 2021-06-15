#Stage1
#base image containing java runtime
FROM openjdk:11-slim as build

#Maintainer Info
LABEL maintainer = "Eyimofe Ogunbiyi <ogunbiyioladapo33@gmail.com>"

#Application Jar File
#Docker maven plugin sets this variable
ARG JAR_FILE

#Add application's jar file to container by the name app.jar
COPY ${JAR_FILE} app.jar

#unpackage jar file
#Here we make a directory in our image called target/dependency and then we cd into that folder then subsequently
#unpackage the jar file into that created directory
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)



#Stage 2
# apk is specific to the apline distro so to use apk we need alpine
FROM openjdk:11-slim

VOLUME /tmp



#Copy unpackaged application to new container
#copy unpackaged jar files from the /target/dependency folder which we created earler into a new directory
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app



#Execute the application
ENTRYPOINT ["java","-cp","app:app/lib/*","com.nubari.licensingservice.LicensingServiceApplication"]




