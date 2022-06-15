FROM tomcat:8.5.81

MAINTAINER Sam Grieggs
ADD ./app /app
RUN apt-get update && apt-get install -y maven


RUN cd /app && mvn install
RUN rm -rf webapps/ROOT*
RUN mv /app/target/PsychometricAnnotator-1.0-SNAPSHOT.war ./webapps/ROOT.war
RUN mv /app/target/PsychometricAnnotator-1.0-SNAPSHOT ./webapps/ROOT


CMD ["catalina.sh", "run"]
