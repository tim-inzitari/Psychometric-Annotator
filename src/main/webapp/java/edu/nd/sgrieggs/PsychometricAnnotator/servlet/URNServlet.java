package edu.nd.sgrieggs.PsychometricAnnotator.servlet;

import edu.nd.sgrieggs.PsychometricAnnotator.bo.obj.Word;
import edu.nd.sgrieggs.PsychometricAnnotator.io.LetterSaver;
import edu.nd.sgrieggs.PsychometricAnnotator.io.User;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;


/**
 * Created by smgri on 6/21/2017.
 */

public class URNServlet extends javax.servlet.http.HttpServlet {
    private final String IMAGE_HANDLER_LOCATION = "https://py-image-handler-dot-premium-bloom-174915.appspot.com/";
    private static final Logger log = Logger.getLogger(URNServlet.class.getName());
    private HashMap<String, Integer> userMap;
    private ArrayList<User> userList;
    private int noUsers = 0;
    private ServletContext ctx;

    public void init() {
        log.info("Servlet Loading...");
        System.out.println("Servlet Loading...");
        userMap = new HashMap<String, Integer>();
        userList = new ArrayList<User>();
        System.out.println("Success!");
        ctx = getServletContext();
    }

    public void destroy() {
        System.out.println("Boom");
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String askResponse = request.getParameter("askResponse");
        String user = request.getUserPrincipal().getName();
        //for debugging purposes
        if(user.equals("smgrieggs@gmail.com")){
            user = "TEST";
        }else{
            user = hashToID(user.hashCode());
        }
        //System.out.println(request.getUserPrincipal().getName());
//        if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
//            user = hashToID(userService.getCurrentUser().getEmail().hashCode());
//        }else{
//            Enumeration<String> params = request.getParameterNames();
//            while(params.hasMoreElements()){
//                String paramName = params.nextElement();
//                System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
//            }
//        }
        log.info("user: " + user + " has just connected");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (askResponse.toLowerCase().equals("res")) {
            String type = request.getParameter("type").toLowerCase();
            if (type.equals("line")) {
                System.out.println("YOu SHouLD BE sEE ing dis");
                String in = request.getParameter("data");
                boolean out = false;
//                try{
                    System.out.println("works here");
                    out = handleLineReturn(translateUser(user), in);
                    System.out.println("probably not here");
//                }catch(Exception e){
//                    log.severe("Error handling line return: " + e.getMessage());
//                    Enumeration<String> params = request.getParameterNames();
//                    String logMessage = "";
//                    while(params.hasMoreElements()){
//                        String paramName = params.nextElement();
//                        logMessage = logMessage + "Parameter Name - "+paramName+", Value - "+request.getParameter(paramName) + "\n";
//                    }
//                    log.severe(logMessage);
//                }
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            else if (type.equals("word")) {
                String in = request.getParameter("data");
                String[] URNs = deStringify(in);
                boolean out = false;
                try {
                    out = handleWordReturn(translateUser(user), in);
                }catch(Exception e){
                    log.severe(e.getMessage());
                    Enumeration<String> params = request.getParameterNames();
                    String logMessage = "";
                    while(params.hasMoreElements()){
                        String paramName = params.nextElement();
                        logMessage = logMessage + "Parameter Name - "+paramName+", Value - "+request.getParameter(paramName) + "\n";
                    }
                    log.severe(logMessage);
                }
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            else if (type.equals("char")) {
                String in = request.getParameter("data");
                String[] URNs = deStringify(in);
                String anno = request.getParameter("annotation");
                boolean out = false;
                try{
                    out = handleCharacterReturn(translateUser(user), anno, in);
                }catch(Exception e){
                    log.severe(e.getMessage());
                    Enumeration<String> params = request.getParameterNames();
                    String logMessage = "";
                    while(params.hasMoreElements()){
                        String paramName = params.nextElement();
                        logMessage = logMessage + "Parameter Name - "+paramName+", Value - "+request.getParameter(paramName) + "\n";
                    }
                    log.severe(logMessage);
                }
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            else if (type.equals("anno")) {
                boolean out = handleAnnotationReturn(translateUser(user), Integer.parseInt(request.getParameter("timer")), request.getParameter("annotation"), Integer.parseInt(request.getParameter("difficulty")));
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            else {
                log.severe("Invalid Response : " + type);
            }
        } else if (askResponse.toLowerCase().equals("init")) {
            String ret = "";

            boolean[] out = handleInitialAsk(translateUser(user));
            for (int x = 0; x < out.length; x++) {
                System.out.println(out[x]);
                ret = ret + ((out[x]) ? "TRUE" : "FALSE");
                if ((x + 1) != out.length) {
                    ret = ret + ",";
                }
            }
            response.getWriter().write(ret);
        }else if(askResponse.toLowerCase().equals("img")){
            Word active = userList.get(translateUser(user)).getActiveWord();
            int x = (int)Double.parseDouble(request.getParameter("x"));
            int y = (int)Double.parseDouble(request.getParameter("y"));
            String imgURL = request.getParameter("data");
            String urn = request.getParameter("urn");
            int cn = Integer.parseInt(request.getParameter("id"));
            int ln = active.getLineNo();
            int wn = active.getWordNo();



            //System.out.println(ctx.getAttribute());

            LetterSaver saver = new LetterSaver((String)ctx.getAttribute("FILES_DIR"), urn, ln, wn, cn, x, y, user, imgURL);
            if(saver.saveImage()){
                saver = null;
                response.getWriter().write("True");
            }else{
                saver = null;
                System.out.println("I fucked up");
            }


        } else if(askResponse.toLowerCase().equals("ask")){
            String type = request.getParameter("type");
            if(type == null){
                log.warning("Invalid Response");
            } else if(type.toLowerCase().equals("lineselector")) {
                response.getWriter().write(handleLineSelector(translateUser(user)));
            } else if (type.toLowerCase().equals("wordselector")) {
                response.getWriter().write(handleWordSelector(translateUser(user)));
            } else if (type.toLowerCase().equals("characterselector")) {
                response.getWriter().write(handleCharacterSelector(translateUser(user)));
            } else if (type.toLowerCase().equals("charann")) {
                response.getWriter().write(handleCharacterAnnotator(translateUser(user)));
            } else {
                response.getWriter().write("Invalid request");
                log.severe("INVALID REQUEST RECEIVED");
                Enumeration<String> params = request.getParameterNames();
                String logMessage = "";
                while (params.hasMoreElements()) {
                    String paramName = params.nextElement();
                    logMessage = logMessage + "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n";
                }
                log.severe(logMessage);
            }
        }else{
            response.getWriter().write("Invalid request");
            log.severe("INVALID REQUEST RECEIVED");
            Enumeration<String> params = request.getParameterNames();
            String logMessage = "";
            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                logMessage = logMessage + "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n";
            }
            log.severe(logMessage);
        }
    }



    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        //String text = po.getNextURN();
        response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("urn:cite2:hmt:vaimg.v1:VA012RN_0013");       // Write response body.
        //System.out.println(text);
    }

    private String handleLineSelector(int user) {
        //placeholder
        return userList.get(user).getNextPage();
    }

    private String handleCharacterSelector(int user) {
        return userList.get(user).getNextWord();
    }


    private boolean handleLineReturn(int user, String input) {
        User active = userList.get(user);
        System.out.println(active.toString());
        String[] temp2 = deStringify(input);
        for(int x = 0; x < temp2.length; x++){
            System.out.println(x +": " + temp2[x]);
        }
        boolean test = active.returnPage(temp2);
        System.out.println("3");
        return test;
    }

    private boolean handleWordReturn(int user, String input) {
        return userList.get(user).returnLine(deStringify(input));
    }

    private boolean handleCharacterReturn(int user, String annotation, String input) {
        return userList.get(user).returnWord(annotation, deStringify(input));
    }

    private String handleWordSelector(int user) {
        //placeholder
        return userList.get(user).getNextLine();

    }

    private String handleCharacterAnnotator(int user) {
        return userList.get(user).getNextLetter();
    }

    private boolean handleAnnotationReturn(int user, int timer, String annotation, int difficulty) {
        return userList.get(user).returnLetter(timer, annotation, difficulty);

    }

    private boolean[] handleInitialAsk(int user) {
        return userList.get(user).initalCheck();
    }


    //it appears there is a problem.
    private int translateUser(String user) {
        Integer out = userMap.get(user.toUpperCase());
        System.out.println("user: " + user + " out: " + out);
        if (out == null) {
            System.out.println("noUsers = " + noUsers);
            userMap.put(user, noUsers);
            userList.add(new User(user.toUpperCase(), noUsers));
            out = noUsers++;
        }
        return out;
    }

    private String[] deStringify(String input) {
        input = input.substring(2, input.length() - 2);
        String[] output = input.split("\",\"");
        return output;
    }

    private String hashToID(int hash){
        boolean neg = hash < 0;
        if(neg){
            hash *= -1;
        }
        String out = hash + "";
        char[] outA = out.toCharArray();
        int magic = 1;

        for(int x = 0; x < outA.length; x ++){
            int example = (hash % (magic*10) ) / magic;
            if(!neg){
                outA[outA.length-(x+1)] = (char)(((int)'A') + example);
            }else{
                outA[outA.length-(x+1)] = (char)(((int)'Z') - example);
            }
            magic *= 10;
        }
        return new String(outA);
    }
}
