package edu.nd.sgrieggs.PsychometricAnnotator.io;

import java.io.*;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by smgri on 1/2/2018.
 */
public class LineSaver {

    private String saveLocation;
    private String urn;
    private int line;
    private int word;
    private int character;
    private int x;
    private int y;
    private String user;
    private String imgUrl;
    private char annotation;


    public LineSaver(String saveLocation, String urn, int line, int word, int character, int x, int y, String user, String imgUrl, char annotation){
        this.saveLocation = saveLocation;

        String operatingSystem = System.getProperty("os.name").toLowerCase();
        if (operatingSystem.contains("win")){
            this.urn =  urn.replace(":","-");
        }else{
            this.urn = urn;
        }
        this.urn = this.urn.split("@")[0];
        this.user = user;
        this.line = line;
        this.word = word;
        this.character = character;
        this.x = x;
        this.y = y;
        this.user = user;
        this.imgUrl = imgUrl;
        this.annotation = annotation;
    }



    public boolean saveImage() throws IOException {
        File imgOut = new File(saveLocation + File.separator + user + "-" + urn + ".png");
        System.out.println("attempting to save4");
        File dataOut = new File(saveLocation + File.separator + user + "-" + urn  + ".txt");
        System.out.println("attempting to save5");
        if(!imgOut.exists() && ! dataOut.exists()) {
            System.out.println(imgOut.toString());
            System.out.println(imgOut);
            imgOut.createNewFile();
            dataOut.createNewFile();

            PrintWriter writer = new PrintWriter(dataOut, "UTF-8");
            writer.println(urn);
            writer.println(line);
            writer.println(word);
            writer.println(character);
            writer.println(x);
            writer.println(y);
            writer.println(user);
            writer.println(annotation);

            FileOutputStream imageOutFile = new FileOutputStream(imgOut);
            String binStr = this.url2string(imgUrl);
            byte[] binD = this.getBinD(binStr);
            imageOutFile.write(binD);
            imageOutFile.close();
            writer.close();
            return true;
        }else{
            return false;
        }
    }

    private String url2string(String url){
        String[] out = url.split(";base64,");
        String output = null;
        try{
            output = out[1];
        }catch(Exception e){
            System.out.println("ERROR: INVALID URL");
            System.out.println(url);
        }
        return output;
    }

    private String getDtype(String url){
        String[] out = url.split(";base64,");
        out = out[0].split("/");
        return out[1];
    }

    private byte[] getBinD(String binString){
        return Base64.decodeBase64(binString);
    }

}
