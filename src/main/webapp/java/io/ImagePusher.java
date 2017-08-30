package io;
import bo.obj.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by smgri on 8/30/2017.
 */
public class ImagePusher{
    private static final String IMAGE_HANDLER_LOCATION = "https://py-image-handler-dot-premium-bloom-174915.appspot.com/";
    private static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();


    public static String pushImage(ImageURL img, int lineNo, int wordNo, String user, int charNo) throws MalformedURLException, IOException {
        String urn =img.getURN();
        String imgURL = img.getImageURL();
        String x = img.getX()+"";
        String y = img.getY()+"";
        String ln = lineNo+"";
        String wn = wordNo+"";
        String cn = charNo+"";

        String query = String.format("urn=%s&line=%s&word=%s&character=%s&x=%s&y=%s&imgURL=%s",
                URLEncoder.encode(urn, CHARSET),
                URLEncoder.encode(ln,CHARSET),
                URLEncoder.encode(wn,CHARSET),
                URLEncoder.encode(cn,CHARSET),
                URLEncoder.encode(x, CHARSET),
                URLEncoder.encode(y, CHARSET),
                URLEncoder.encode(imgURL, CHARSET));

        URLConnection connection = new URL(IMAGE_HANDLER_LOCATION).openConnection();
        connection.setDoOutput(true); // Triggers POST.
        connection.setRequestProperty("Accept-Charset", CHARSET);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);

        try (OutputStream output = connection.getOutputStream()) {
            output.write(query.getBytes(CHARSET));
        }
        String out = "";
        String temp = null;
        InputStream response = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(response));
        while ((temp = br.readLine())!=null){
            out = out + temp + "\n";
        }
        out = out.substring(0,out.length()-1);
        System.out.print(out);
        return out;
    }






}