FROM tomcat:8.5

MAINTAINER Sam Grieggs
ADD ./app /app
RUN apt-get update && apt-get install -y openjdk-8-jdk && apt-get install -y maven && apt-get install python -y


RUN cd /app && mvn install
RUN rm -rf webapps/ROOT*
RUN mv /app/target/PsychometricAnnotator-1.0-SNAPSHOT.war ./webapps/ROOT.war
RUN mv /app/target/PsychometricAnnotator-1.0-SNAPSHOT ./webapps/ROOT
RUN ls -l /app/
RUN ls -l /app/scripts/
RUN python /app/scripts/accountmanager.py < /app/scripts/users


CMD ["catalina.sh", "run"]
