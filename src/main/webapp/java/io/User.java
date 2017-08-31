package io;

import bo.obj.*;

import java.io.*;
import java.sql.*;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by smgri on 6/28/2017.
 */
public class User {


    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String DB_URL = "jdbc:mysql://localhost:3307/documents";

    //  Database credentials
    static final String USER = "software";
    private String pass;
    private static final Logger log = Logger.getLogger(User.class.getName());

    private Connection dbc = null;
    private String user;
    private Letter activeLetter;
    private Line activeLine;
    private Page activePage;
    private Word activeWord;


    public User(String user, int ID){
        log.info("Connecting to database...");
        this.pass = System.getProperty("cred");
        this.user = user;
        initializeDB();
        activePage = null;
        activeLine = null;
        activeWord = null;
        activeLetter = null;
    }

    public String getNextPage(){
        if(activePage != null){
            return activePage.getURN();
        }else{
            loadPageDB();
            if(activePage != null) {
                return activePage.getURN();
            }
        }
        return null;
    }

    public String getNextLine(){
        if(activeLine != null){
            return activeLine.getURN();
        }else{
            loadLineDB();
            if(activeLine != null) {
                return activeLine.getURN();
            }
        }
        return null;
    }

    public String getNextWord(){
        if(activeWord != null){
            return activeWord.getURN();
        }else{
            loadWordDB();
            if(activeWord != null){
                return activeWord.getURN();
            }
            return null;
        }
    }

    public String getNextLetter(){
        loadLetterDB();
        if (activeLetter != null) {
            return activeLetter.getURN();
        } else {
            return null;
        }
    }

    public boolean returnPage(String[] plines) {
        saveLinesDB(this.activePage.getID(), plines);
        return hasPageDB();
    }

    public boolean returnLine(String[] lwords){
        saveWordsDB(activeLine.getDocID(),activeLine.getLineNo(),lwords);
        return hasLineDB();
    }

    public boolean returnWord(String annotation, String[] wletters) {
        saveLettersDB(activeWord.getDocID(),activeWord.getLineNo(), activeWord.getWordNo(),annotation,wletters);
        return hasWordDB();
    }

    public boolean returnLetter(int timer, String annotation, int difficulty){
        saveAnnotationDB(timer,annotation,difficulty);
        return hasLetterDB();
    }

    public boolean[] initalCheck(){
        boolean out[] = new boolean[4];
        out[0] = hasPageDB();
        out[1] = hasLineDB();
        out[2] = hasWordDB();
        out[3] = hasLetterDB();
        return out;
    }

    public String sendToImageHandler(ImageURL img, int charNo) throws IOException{
        return ImagePusher.pushImage(img,activeWord.getLineNo(),activeWord.getWordNo(),activeWord.getTransID(),charNo);

    }




    //////////////////////////////////////////////////////////////////////////////////////
    //DB functions below!
    //////////////////////////////////////////////////////////////////////////////////////

    private Connection getConnection(){
        try {
            if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
                log.info("Should see this");
                DB_URL = System.getProperty("ae-cloudsql.cloudsql-database-url");
                log.info(DB_URL);
                Class.forName("com.mysql.jdbc.GoogleDriver");
            } else {
                DB_URL = System.getProperty("mysql.local-database-url");
                Class.forName("com.mysql.jdbc.Driver");
            }
            return DriverManager.getConnection(DB_URL);
        }catch(Exception e){
            log.severe("Exception while getting the connection: " + e.getMessage());
        }
        return null;
    }


    private String sanitize(String input){
        return input.toUpperCase().replaceAll("[^a-zA-Z]", "");
    }

    private void initializeDB(){
        Connection dbc = null;
        PreparedStatement checkTrans = null;
        PreparedStatement addTrans = null;
        PreparedStatement getDocs = null;
        PreparedStatement addTransPages = null;
        ResultSet checkTransRes = null;
        ResultSet getDocsRes = null;
        String dbTrans = sanitize(user);
        try {
            dbc = getConnection();
            String  checkTransSQL = "SELECT COUNT(*) FROM trans WHERE ID = ?";
            checkTrans = dbc.prepareStatement(checkTransSQL);
            checkTrans.setString(1,dbTrans);
            checkTransRes =  checkTrans.executeQuery();
            int count = 0;
            if(checkTransRes.next()) {
                count = checkTransRes.getInt(1);
            }
            checkTransRes.close();
            if(count == 0){
                String addTransSQL = "INSERT INTO trans(ID) VALUES (?)";
                log.warning("doublecheck");
                addTrans = dbc.prepareStatement(addTransSQL);
                addTrans.setString(1,dbTrans);
                addTrans.executeUpdate();
                addTrans.close();
                String getDocsSQL = "SELECT ID FROM doc";
                String addTransPagesSQL = "INSERT INTO unusedDocuments(docID,transID) VALUES (?,?)";
                getDocs = dbc.prepareStatement(getDocsSQL);
                addTransPages = dbc.prepareStatement(addTransPagesSQL);
                getDocsRes = getDocs.executeQuery();
                while(getDocsRes.next()){
                    addTransPages.setString(1,getDocsRes.getString(1));
                    addTransPages.setString(2,dbTrans);
                    addTransPages.executeUpdate();
                }
                getDocs.close();
                addTransPages.close();
                getDocsRes.close();
                log.info("User: " + user + " was added to the database.");
            }
            checkTransRes.close();
            checkTrans.close();
        }catch(Exception e){
            e.printStackTrace();
            log.severe("Exception during initialization: " + e.getMessage());
        }finally{
            try {
                if (checkTrans != null) {
                    checkTrans.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (addTrans != null) {
                    addTrans.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (getDocs != null) {
                    getDocs.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (addTransPages != null) {
                    addTransPages.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (checkTransRes != null) {
                    checkTransRes.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (getDocsRes != null) {
                    getDocsRes.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch(SQLException se){
                se.printStackTrace();
            }

        }
    }

    private void loadPageDB() {
        Connection dbc = null;
        PreparedStatement loadPage = null;
        ResultSet loadPageRes = null;
        try {
            dbc = getConnection();
            String loadPageSQL = "SELECT ID,URN \n" +
                    "FROM unusedDocuments\n" +
                    "INNER JOIN doc \n" +
                    "ON doc.ID=unusedDocuments.docID \n" +
                    "WHERE transID = ? \n" +
                    "ORDER BY RAND() LIMIT 1";
            loadPage = dbc.prepareStatement(loadPageSQL);
            loadPage.setString(1, sanitize(user));
            loadPageRes = loadPage.executeQuery();
            if (loadPageRes.next()) {
                activePage = new Page(loadPageRes.getInt(1), loadPageRes.getString(2));
                log.severe(activePage.getID() + " " + activePage.getURN());
            }
            loadPageRes.close();
            loadPage.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadPageRes != null) {
                    loadPageRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadPage != null) {
                    loadPage.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }

        }
    }

    private void saveLinesDB(int page, String[] lines){
        Connection dbc = null;
        PreparedStatement saveLines = null;
        PreparedStatement usePage = null;
        try {
            dbc = getConnection();
            String saveLinesSQL = "INSERT INTO line (transID,docID,lineNo,URN) VALUES (?,?,?,?)";
            saveLines = dbc.prepareStatement(saveLinesSQL);
            for(int x = 0; x < lines.length; x++){
                saveLines.setString(1,sanitize(user));
                saveLines.setInt(2,page);
                saveLines.setInt(3,x);
                saveLines.setString(4,lines[x]);
                saveLines.executeUpdate();
            }
            saveLines.close();
            String usePageSQL = "DELETE FROM unusedDocuments where docID = ? AND transID = ?";
            usePage = dbc.prepareStatement(usePageSQL);
            usePage.setInt(1,activePage.getID());
            usePage.setString(2,sanitize(user));
            usePage.executeUpdate();
            usePage.close();
            dbc.close();
            activePage = null;
        }catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (saveLines != null) {
                    saveLines.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (usePage != null) {
                    usePage.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    private void loadLineDB(){
        Connection dbc = null;
        PreparedStatement loadLine = null;
        ResultSet loadLineRes = null;
        try {
            dbc = getConnection();
            String loadLineSQL = "SELECT * \n" +
                    "FROM line\n" +
                    "WHERE transID = ? AND used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
            loadLine.setString(1, sanitize(user));
            loadLineRes = loadLine.executeQuery();
            if (loadLineRes.next()) {
                activeLine = new Line(loadLineRes.getString(1), loadLineRes.getInt(2), loadLineRes.getInt(3),loadLineRes.getString(4));
            }
            loadLineRes.close();
            loadLine.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadLineRes != null) {
                    loadLineRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadLine != null) {
                    loadLine.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }

        }
    }

    private void saveWordsDB(int page, int line, String[] words){
        Connection dbc = null;
        PreparedStatement saveWords = null;
        PreparedStatement deactivateLine = null;
        try{
            dbc = getConnection();
            String saveWordsSQL = "INSERT INTO word (transID,docID,lineNo,wordNo,URN) VALUES (?,?,?,?,?)";
            saveWords = dbc.prepareStatement(saveWordsSQL);
            for(int x = 0; x < words.length; x ++){
                saveWords.setString(1,sanitize(user));
                saveWords.setInt(2,page);
                saveWords.setInt(3,line);
                saveWords.setInt(4,x);
                saveWords.setString(5,words[x]);
                saveWords.executeUpdate();
            }
            saveWords.close();
            String deactivateLineSQL = "UPDATE line SET used = true WHERE docID = ? AND lineNo = ? AND transID = ?";
            deactivateLine = dbc.prepareStatement(deactivateLineSQL);
            deactivateLine.setInt(1,page);
            deactivateLine.setInt(2,line);
            deactivateLine.setString(3, sanitize(user));
            deactivateLine.executeUpdate();
            deactivateLine.close();
            dbc.close();
            activeLine = null;
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (saveWords != null) {
                    saveWords.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (deactivateLine != null) {
                    deactivateLine.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
        }
    }

    private void loadWordDB(){
        Connection dbc = null;
        PreparedStatement loadWord = null;
        ResultSet loadWordRes = null;
        Page out = null;
        try {
            dbc = getConnection();
            String loadWordSQL = "SELECT * \n" +
                    "FROM word\n" +
                    "WHERE transID = ? AND used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadWord = dbc.prepareStatement(loadWordSQL);
            loadWord.setString(1, sanitize(user));
            loadWordRes = loadWord.executeQuery();
            if (loadWordRes.next()) {
                activeWord = new Word(loadWordRes.getString(1), loadWordRes.getInt(2), loadWordRes.getInt(3), loadWordRes.getInt(4),loadWordRes.getString(5),loadWordRes.getString(6));
            }
            loadWordRes.close();
            loadWord.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadWordRes != null) {
                    loadWordRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadWord != null) {
                    loadWord.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }

        }
    }

    private void saveLettersDB(int page, int line, int word, String annotation, String[] letters){
        Connection dbc = null;
        PreparedStatement saveLetters = null;
        PreparedStatement deactivateWord = null;
        try{
            dbc = getConnection();
            String saveLettersSQL = "INSERT INTO letter (transID,docID,lineNo,wordNo,letterNo,URN) VALUES (?,?,?,?,?,?)";
            saveLetters = dbc.prepareStatement(saveLettersSQL);
            for(int x = 0; x < letters.length; x++){
                saveLetters.setString(1,sanitize(user));
                saveLetters.setInt(2,page);
                saveLetters.setInt(3,line);
                saveLetters.setInt(4,word);
                saveLetters.setInt(5,x);
                saveLetters.setString(6,letters[x]);
                saveLetters.executeUpdate();
            }
            saveLetters.close();
            String deactivateWordSQL = "UPDATE word SET used = true, annotation = ? WHERE docID = ? AND lineNo = ? AND wordNo = ? AND transID = ?";
            deactivateWord = dbc.prepareStatement(deactivateWordSQL);
            deactivateWord.setString(1,annotation);
            deactivateWord.setInt(2,page);
            deactivateWord.setInt(3,line);
            deactivateWord.setInt(4,word);
            deactivateWord.setString(5,sanitize(user));
            deactivateWord.executeUpdate();
            deactivateWord.close();
            dbc.close();
            activeWord = null;
        } catch (SQLException se) {
            log.severe("SQL Exception (saving letters): " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (saveLetters != null) {
                    saveLetters.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (deactivateWord != null) {
                    deactivateWord.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
        }

    }

    private void loadLetterDB(){
        Connection dbc = null;
        PreparedStatement loadLetter = null;
        ResultSet loadLetterRes = null;
        try {
            dbc = getConnection();
            String loadLetterSQL = "SELECT * \n" +
                    "FROM letter\n" +
                    "WHERE transID = ? AND annotation IS NULL\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLetter = dbc.prepareStatement(loadLetterSQL);
            loadLetter.setString(1, sanitize(user));
            loadLetterRes = loadLetter.executeQuery();
            if (loadLetterRes.next()) {
                activeLetter = new Letter(loadLetterRes.getString(1), loadLetterRes.getInt(2), loadLetterRes.getInt(3), loadLetterRes.getInt(4),loadLetterRes.getInt(5),loadLetterRes.getString(6),"");
            }
            loadLetterRes.close();
            loadLetter.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadLetterRes != null) {
                    loadLetterRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadLetter != null) {
                    loadLetter.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }

        }
    }

    private void saveAnnotationDB(int timer, String annotation, int difficulty){
        Connection dbc = null;
        PreparedStatement saveAnnotation = null;
        PreparedStatement getCount = null;
        ResultSet getCountRes = null;
        try{
            dbc = getConnection();
            String saveAnnotationSQL = "UPDATE letter SET annotation = ?, timer = ?, difficulty = ?  WHERE transID = ? AND docID = ? AND lineNo = ? AND wordNo = ? AND letterNo = ?";
            saveAnnotation = dbc.prepareStatement(saveAnnotationSQL);
            saveAnnotation.setString(1,annotation);
            saveAnnotation.setInt(2,timer);
            saveAnnotation.setInt(3,difficulty);
            saveAnnotation.setString(4,sanitize(user));
            saveAnnotation.setInt(5,activeLetter.getDocID());
            saveAnnotation.setInt(6,activeLetter.getLineNo());
            saveAnnotation.setInt(7,activeLetter.getWordNo());
            saveAnnotation.setInt(8,activeLetter.getLetterNo());
            saveAnnotation.executeUpdate();
            activeLetter = null;
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        }finally {
            try {
                if (saveAnnotation != null) {
                    saveAnnotation.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }

        }


    }

    private boolean hasPageDB(){
        Connection dbc = null;
        PreparedStatement loadPage = null;
        ResultSet loadPageRes = null;
        boolean out = false;
        try {
            dbc = getConnection();
            String loadPageSQL = "SELECT ID,URN \n" +
                    "FROM unusedDocuments\n" +
                    "INNER JOIN doc \n" +
                    "ON doc.ID=unusedDocuments.docID \n" +
                    "WHERE transID = ? \n" +
                    "ORDER BY RAND() LIMIT 1";
            loadPage = dbc.prepareStatement(loadPageSQL);
            loadPage.setString(1, sanitize(user));
            loadPageRes = loadPage.executeQuery();
            if (loadPageRes.next()) {
                out = true;
            }
            loadPageRes.close();
            loadPage.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadPageRes != null) {
                    loadPageRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadPage != null) {
                    loadPage.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            return out;

        }
    }

    private boolean hasLineDB(){
        Connection dbc = null;
        PreparedStatement loadLine = null;
        ResultSet loadLineRes = null;
        boolean out = false;
        try {
            dbc = getConnection();
            String loadLineSQL = "SELECT * \n" +
                    "FROM line\n" +
                    "WHERE transID = ? AND used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
            loadLine.setString(1, sanitize(user));
            loadLineRes = loadLine.executeQuery();
            if (loadLineRes.next()) {
                out = true;
            }
            loadLineRes.close();
            loadLine.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadLineRes != null) {
                    loadLineRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadLine != null) {
                    loadLine.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            return out;
        }
    }

    private boolean hasWordDB(){
        Connection dbc = null;
        PreparedStatement loadWord = null;
        ResultSet loadWordRes = null;
        boolean out = false;
        try {
            dbc = getConnection();
            String loadWordSQL = "SELECT * \n" +
                    "FROM word\n" +
                    "WHERE transID = ? AND used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadWord = dbc.prepareStatement(loadWordSQL);
            loadWord.setString(1, sanitize(user));
            loadWordRes = loadWord.executeQuery();
            if (loadWordRes.next()) {
                out = true;
            }
            loadWordRes.close();
            loadWord.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
        } catch (Exception e) {
            log.info("Got an exception. " + e.getMessage());
        } finally {
            try {
                if (loadWordRes != null) {
                    loadWordRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadWord != null) {
                    loadWord.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            return out;
        }
    }

    private boolean hasLetterDB(){
        Connection dbc = null;
        PreparedStatement loadLetter = null;
        ResultSet loadLetterRes = null;
        boolean out = false;
        try {
            dbc = getConnection();
            String loadLetterSQL = "SELECT * \n" +
                    "FROM letter\n" +
                    "WHERE transID = ? AND annotation IS NULL\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLetter = dbc.prepareStatement(loadLetterSQL);
            loadLetter.setString(1, sanitize(user));
            loadLetterRes = loadLetter.executeQuery();
            if (loadLetterRes.next()) {
                out = true;
            }
            loadLetterRes.close();
            loadLetter.close();
            dbc.close();
        } catch (SQLException se) {
            log.severe("SQL Exception: " + se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadLetterRes != null) {
                    loadLetterRes.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (loadLetter != null) {
                    loadLetter.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                log.severe("SQL Exception: " + se.getMessage());
                se.printStackTrace();
            }
            return out;
        }
    }


}
