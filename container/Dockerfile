FROM tomcat:8.5.24

#add mysql jdbc driver to enable database connectivity
RUN apt-get update && apt-get install -y libmysql-java
ENV CLASSPATH="${CLASSPATH}:/usr/share/java/mysql-connector-java.jar"

CMD ["catalina.sh", "run"]
