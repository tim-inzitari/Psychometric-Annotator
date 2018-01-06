package edu.nd.sgrieggs.PsychometricAnnotator.servlet;


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import edu.nd.sgrieggs.PsychometricAnnotator.io.DocumentDatabase;


/**
 * Created by smgri on 12/31/2017.
 */
@WebListener
public class DatabaseLocationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Loading Database Parameters");
        ServletContext ctx = servletContextEvent.getServletContext();
        String driver = ctx.getInitParameter("database.JDBC_DRIVER");
        String location = ctx.getInitParameter("database.DB_LOC");
        String user = ctx.getInitParameter("database.DB_USER");
        String pw = ctx.getInitParameter("database.password");
        DocumentDatabase.initialize(driver, location,user,pw);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Shutting down!");
    }


}
