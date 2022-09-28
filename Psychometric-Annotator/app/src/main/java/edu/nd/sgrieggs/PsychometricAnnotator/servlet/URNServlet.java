package edu.nd.sgrieggs.PsychometricAnnotator.servlet;

import edu.nd.sgrieggs.PsychometricAnnotator.bo.obj.Word;
import edu.nd.sgrieggs.PsychometricAnnotator.io.LetterSaver;
import edu.nd.sgrieggs.PsychometricAnnotator.io.LineSaver;
import edu.nd.sgrieggs.PsychometricAnnotator.io.User;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by smgri on 6/21/2017.
 */

public class URNServlet extends javax.servlet.http.HttpServlet {
    private static final Logger log = Logger.getLogger(URNServlet.class.getName());
    private HashMap<String, Integer> userMap;
    private ArrayList<User> userList;
    private int noUsers = 0;
    private ServletContext ctx;

    public void init() {
        log.info("Servlet Loading...");
        userMap = new HashMap<String, Integer>();
        userList = new ArrayList<User>();
        ctx = getServletContext();
    }

    public void destroy() {

    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String askResponse = request.getParameter("askResponse");
        // String user = request.getUserPrincipal().getName();
        String user = getClientIpAddress(request);
        log.info(user +" connected, stored as: " + hashToID(user.hashCode()));
        // for debugging purposes
        if(user.equals("smgrieggs@gmail.com")){
            user = "TEST";
        }else{
            user = hashToID(user.hashCode());
        }
        log.info("user: " + user + " has just connected");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        if (askResponse.toLowerCase().equals("res")) {
            String type = request.getParameter("type").toLowerCase();

            // Character level annotation Segmented Line function

            if (type.equals("line")) {
                String in = request.getParameter("data");
                boolean out = false;
                try{
                    out = handleLineReturn(translateUser(user), in);
                }catch(Exception e){
                    log.severe("Error handling line return: " + e.getMessage());
                    Enumeration<String> params = request.getParameterNames();
                    String logMessage = "";
                    while(params.hasMoreElements()){
                        String paramName = params.nextElement();
                        logMessage = logMessage + "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n";
                    }
                    log.severe(logMessage);
                }
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            // line level annotation style line segement
            else if (type.equals("lineseg")) {
                String in = request.getParameter("data");
                String lineString = request.getParameter("lineString");
                boolean out = false;
                try{
                    out = handleLineSegReturn(translateUser(user), in, lineString);
                }catch(Exception e){
                    e.printStackTrace(pw);
                    log.severe("Error handling lineSeg return: " + e.getMessage() + sw.toString());
                    Enumeration<String> params = request.getParameterNames();
                    String logMessage = "";
                    while(params.hasMoreElements()){
                        String paramName = params.nextElement();
                        logMessage = logMessage + "Parameter Name - " + paramName + ", Value - " + request.getParameter(paramName) + "\n";
                    }
                    log.severe(logMessage);
                }
                log.severe("Got here in line seg");
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }

            else if (type.equals("word")) {
                String in = request.getParameter("data");
                System.out.println(request.getParameter("lineNo"));
                int lineNo = Integer.parseInt(request.getParameter("lineNo"));
                String[] URNs = deStringify(in);
                boolean out = false;
                try {
                    out = handleWordReturn(translateUser(user), in, lineNo);
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
                int lineNo = Integer.parseInt(request.getParameter("lineNo"));
                int wordNo = Integer.parseInt(request.getParameter("wordNo"));
                boolean out = false;
                try{
                    out = handleCharacterReturn(translateUser(user), anno, in, lineNo, wordNo);
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

                int lineNo = Integer.parseInt(request.getParameter("lineNo"));
                int wordNo = Integer.parseInt(request.getParameter("wordNo"));
                int letterNo = Integer.parseInt(request.getParameter("lineNo"));
                String urn = request.getParameter("urn");

                boolean out = handleAnnotationReturn(translateUser(user), Integer.parseInt(request.getParameter("timer")), request.getParameter("annotation"), Integer.parseInt(request.getParameter("difficulty")),urn,lineNo,wordNo,letterNo);
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
            //The response is a letter image
            Word active = userList.get(translateUser(user)).getActiveWord();
            int x = (int)Double.parseDouble(request.getParameter("x"));
            int y = (int)Double.parseDouble(request.getParameter("y"));
            String imgURL = request.getParameter("data");
            String urn = request.getParameter("urn");
            int cn = Integer.parseInt(request.getParameter("id"));
            int ln = active.getLineNo();
            int wn = active.getWordNo();
            char anno = request.getParameter("annotation").toCharArray()[0];



            log.info("saving input letter to " + (String)ctx.getAttribute("FILES_DIR")+"....");
            LetterSaver saver = new LetterSaver((String)ctx.getAttribute("FILES_DIR"), urn, ln, wn, cn, x, y, user, imgURL, anno);
            if(saver.saveImage()){
                saver = null;
                log.info("success!");
                response.getWriter().write("True");
            }else{
                log.severe("failure..");
                saver = null;
            }


        } else if(askResponse.toLowerCase().equals("imgLine")){
            //The response is a letter image
            int x = (int)Double.parseDouble(request.getParameter("x"));
            int y = (int)Double.parseDouble(request.getParameter("y"));
            String imgURL = request.getParameter("data");
            String urn = request.getParameter("urn");
            int ln = Integer.parseInt(request.getParameter("id"));
            String anno = request.getParameter("annotation");



            log.info("saving input letter to " + (String)ctx.getAttribute("FILES_DIR")+"....");
            LineSaver saver = new LineSaver((String)ctx.getAttribute("FILES_DIR"), urn, ln,x, y, user, imgURL, anno);
            if(saver.saveImage()){
                saver = null;
                log.info("success!");
                response.getWriter().write("True");
            }else{
                log.severe("failure..");
                saver = null;
            }


        }
        
        else if(askResponse.toLowerCase().equals("ask")){
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
            }
            else if(type.toLowerCase().equals("linesegselector")) {
                response.getWriter().write(handleLineSegSelector(translateUser(user)));}
            else {
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
    }

    private String handleLineSelector(int user) {
        //placeholder
        return userList.get(user).getNextPage();
    }

    private String handleLineSegSelector(int user) {
        //placeholder
        return userList.get(user).getNextPage();
    }

    private String handleCharacterSelector(int user) { return userList.get(user).getNextWord();
    }


    private boolean handleLineReturn(int user, String input) {
        User active = userList.get(user);
        String[] temp2 = deStringify(input);
        boolean test = active.returnPage(temp2);
        return test;
    }

    private boolean handleLineSegReturn(int user, String input, String lineString) {
        log.severe("user: " + user);
        log.severe("input: "+input);
        log.severe("lineString: " + lineString);
        User active = userList.get(user);
        // destringify urnroi list and lineannotation list respectfully
        String[] temp2 = deStringify(input, 2);
        String[] temp3 = deStringify(lineString, 2);
        log.severe("in handle: "+ temp2[0]);
        boolean test = active.returnPage(temp2, temp3);
        return test;

    }

    private boolean handleWordReturn(int user, String input, int lineNo) {
        return userList.get(user).returnLine(deStringify(input),lineNo);
    }

    private boolean handleCharacterReturn(int user, String annotation, String input, int lineNo, int wordNo) {
        return userList.get(user).returnWord(annotation, deStringify(input),lineNo, wordNo);
    }

    private String handleWordSelector(int user) {
        //placeholder
        return userList.get(user).getNextLine();

    }

    private String handleCharacterAnnotator(int user) {
        return userList.get(user).getNextLetter();
    }

    private boolean handleAnnotationReturn(int user, int timer, String annotation, int difficulty, String urn, int lineNo, int wordNo, int letterNo) {
        return userList.get(user).returnLetter(timer, annotation, difficulty, urn, lineNo, wordNo, letterNo);
    }

    private boolean[] handleInitialAsk(int user) {
        return userList.get(user).initalCheck();
    }


    private int translateUser(String user) {
        Integer out = userMap.get(user.toUpperCase());
        if (out == null) {
            userMap.put(user, noUsers);
            userList.add(new User(user.toUpperCase(), noUsers));
            out = noUsers++;
        }
        return out;
    }

    private String[] deStringify(String input) {
        log.info("destring: "+ input);
        input = input.substring(2, input.length() - 2);
        log.info("Destring2 "+ input);
        String[] output = input.split("\",\"");
        return output;
    }
    private String[] deStringify(String input, int offset) {
        log.info("destring: "+ input);
        input = input.substring(offset, input.length() - offset);
        log.info("Destring2 "+ input);
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

    //from https://memorynotfound.com/client-ip-address-java/
    private final String[] IP_HEADER_CANDIDATES = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR" };

    public String getClientIpAddress(javax.servlet.http.HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
