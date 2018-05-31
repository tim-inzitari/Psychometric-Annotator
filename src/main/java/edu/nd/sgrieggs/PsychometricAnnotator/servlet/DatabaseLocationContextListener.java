package edu.nd.sgrieggs.PsychometricAnnotator.servlet;


import edu.nd.sgrieggs.PsychometricAnnotator.io.DocumentDatabase;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener

/**
 * Created by smgri on 12/31/2017.
 */

public class DatabaseLocationContextListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(DatabaseLocationContextListener.class.getName());
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Loading Database Parameters");
        ServletContext ctx = servletContextEvent.getServletContext();
        String driver = ctx.getInitParameter("database.JDBC_DRIVER");
        String location = "jdbc:mysql://"+System.getenv("PSYANN_DB_HOST")+"/"+System.getenv("PSYANN_DATABASE");
        String user = System.getenv("PSYANN_DB_USER");
        String pw = System.getenv("PSYANN_DB_PASSWORD");
        log.info("driver: " + driver);
        log.info("location: " + location);
        log.info("user: " + user);
        if(pw != null){
          log.info("Password: ********");
        }
        DocumentDatabase.initialize(driver, location,user,pw);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }


}
