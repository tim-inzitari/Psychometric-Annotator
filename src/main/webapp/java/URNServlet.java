
import bo.obj.ImageURL;
import io.ImagePusher;
import io.User;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
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

    public void init() {
        log.info("Servlet Loading...");
        System.out.println("Servlet Loading...");
        userMap = new HashMap<String, Integer>();
        userList = new ArrayList<User>();
        System.out.println("Success!");
    }

    public void destroy() {
        System.out.println("Boom");
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        ImagePusher ip = new ImagePusher();
        UserService userService = UserServiceFactory.getUserService();
        String askResponse = request.getParameter("askResponse");
        String user = "TEST";
        if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
            user = hashToID(userService.getCurrentUser().getEmail().hashCode());
        }
        log.info("user: " + user + " has just connected");
        if (askResponse.toLowerCase().equals("res")) {
            String type = request.getParameter("type").toLowerCase();
            if (type.equals("line")) {
                String in = request.getParameter("data");
                String[] URNs = deStringify(in);
                boolean out = handleLineReturn(translateUser(user), in);
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            if (type.equals("word")) {
                String in = request.getParameter("data");
                String[] URNs = deStringify(in);
                boolean out = handleWordReturn(translateUser(user), in);
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            if (type.equals("char")) {
                String in = request.getParameter("data");
                String[] URNs = deStringify(in);
                String anno = request.getParameter("annotation");
                boolean out = handleCharacterReturn(translateUser(user), anno, in);
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            }
            if (type.equals("anno")) {
                boolean out = handleAnnotationReturn(translateUser(user), Integer.parseInt(request.getParameter("timer")), request.getParameter("annotation"), Integer.parseInt(request.getParameter("difficulty")));
                if (out) {
                    response.getWriter().write("TRUE");
                } else {
                    response.getWriter().write("FALSE");
                }
            } else {
                System.out.println("INVALID INPUT");
            }
        } else if (askResponse.toLowerCase().equals("init")) {
            String ret = "";
            boolean[] out = handleInitialAsk(translateUser(user));
            for (int x = 0; x < out.length; x++) {
                ret = ret + ((out[x]) ? "TRUE" : "FALSE");
                if ((x + 1) != out.length) {
                    ret = ret + ",";
                }
            }
            log.info(ret);
            response.getWriter().write(ret);
        }else if(askResponse.toLowerCase().equals("img")){
            log.info("Redirecting image response to python app");
            String out = ip.pushImage(new ImageURL((int)Double.parseDouble(request.getParameter("x")),(int)Double.parseDouble(request.getParameter("y")),request.getParameter("data"),request.getParameter("urn")));
            response.getWriter().write(out);
        } else {
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
            }
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
        return active.returnPage(deStringify(input));
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

    private int translateUser(String user) {
        Integer out = userMap.get(user.toUpperCase());
        if (out == null) {
            //System.out.println("noUsers = " + noUsers);
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
