package edu.nd.sgrieggs.PsychometricAnnotator.servlet;

/**
 * Created by smgri on 12/31/2017.
 */
import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FileLocationContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //String rootPath = System.getProperty("catalina.home");
        //obviously you need to change this for the real version, but do that back in South Bend.
        String rootPath = "/";
        ServletContext ctx = servletContextEvent.getServletContext();
        String relativePath = ctx.getInitParameter("tempfile.dir");
        File file = new File(rootPath + File.separator + relativePath);
        if(!file.exists()){
            if(file.mkdirs()) {
                System.out.println("We created: " + file);
            }else{
                System.out.println("Failure creating save space");
            }
        }
        System.out.println("save location: " + file);
        ctx.setAttribute("FILES_DIR_FILE", file);
        ctx.setAttribute("FILES_DIR", rootPath + File.separator + relativePath);

        System.out.println(ctx.getAttribute("FILES_DIR").getClass());
        System.out.println(ctx.getAttribute("FILES_DIR_FILE"));
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //do cleanup if needed
    }

}
