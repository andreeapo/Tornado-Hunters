FROM openjdk:15-jdk-alpine
LABEL maintainer krutikov@sheridancollege.ca
COPY target/CustomTattooDesign_MobileCRM-0.0.1-SNAPSHOT.jar ctdhelloworld.jar
ENTRYPOINT ["java","-jar","ctdhelloworld.jar"]
EXPOSE 4004
ENV capDBusername=setWhenCompiling \
    capDBpassword=setWhenCompiling \
	capDBhost=setWhenCompiling \
	capDBdatabase=setWhenCompiling \
	capDBport=setWhenCompiling \
	capTestUser=setWhenCompiling \	
	capTestPassword=setWhenCompiling \
	capTestUser2=setWhenCompiling \	
	capTestPassword2=setWhenCompiling
	