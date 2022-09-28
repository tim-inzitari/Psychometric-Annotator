package edu.nd.sgrieggs.PsychometricAnnotator.io;

import edu.nd.sgrieggs.PsychometricAnnotator.bo.obj.*;

import java.sql.*;
import java.util.logging.Logger;


import java.io.PrintWriter;
import java.io.StringWriter;
/**
 * Created by smgri on 6/28/2017.
 */
public class User {

    // Line seg is the new line annotation system

    private static final Logger log = Logger.getLogger(User.class.getName());
    private String user;
    private Letter activeLetter;
    private Line activeLine;
    private LineSeg activeLineSeg;
    private Page activePage;
    private Word activeWord;


    public User(String user, int ID){
        log.info("Connecting to database...");
        //this.pass = System.getProperty("cred");
        this.user = user;
        initializeDB();
        this.activePage = null;
        this.activeLine = null;
        this.activeWord = null;
        this.activeLetter = null;
        this.activeLineSeg = null;
    }

    public String toString(){
        return "User: " + user;
    }

    public String getNextPage(){
        if(this.activePage != null){
            return this.activePage.getURN();
        }else{
            loadPageDB();
            if(this.activePage != null) {
                return this.activePage.getURN();
            }
        }
        return null;
    }

    public String getNextLine(){
        if(this.activeLine != null){
            return this.activeLine.getURN()+"-"+this.activeLine.getLineNo();
        }else{
            loadLineDB();
            if(this.activeLine != null) {
                return this.activeLine.getURN()+"-"+this.activeLine.getLineNo();
            }
        }
        return null;
    }

    public String getNextLineSeg(){
        if(this.activeLineSeg != null){
            return this.activeLineSeg.getURN()+"-"+this.activeLineSeg.getLineNo();
        }else{
            loadLineSegDB();
            if(this.activeLineSeg != null) {
                return this.activeLineSeg.getURN()+"-"+this.activeLineSeg.getLineNo();
            }
        }
        return null;
    }

    public String getNextWord(){
        if(this.activeWord != null){
            return this.activeWord.getURN()+"-"+this.activeWord.getLineNo()+"-"+this.activeWord.getWordNo();
        }else{
            loadWordDB();
            if(this.activeWord != null){
                return this.activeWord.getURN()+"-"+this.activeWord.getLineNo()+"-"+this.activeWord.getWordNo();
            }
            return null;
        }
    }

    public Word getActiveWord(){
        return this.activeWord;
    }

    public String getNextLetter(){
        loadLetterDB();
        if (this.activeLetter != null) {
            return this.activeLetter.getURN()+"-"+this.activeLetter.getLineNo()+"-"+this.activeLetter.getWordNo()+"-"+this.activeLetter.getLetterNo();
        } else {
            return null;
        }
    }

    public boolean returnPage(String[] plines){
        String urn = plines[0].split("@")[0];
        if(!this.activePage.getURN().equals(urn)){
            int id = this.lookupIdDB(urn);
            if (id == -1){
                return true;
            }else{
                this.activePage = new Page(id,urn);
            }
        }
        saveLinesDB(this.activePage.getID(), plines);
        return hasPageDB();
    }
    // Line String
    public boolean returnPage(String[] plines, String[] annotations){
        String urn = plines[0].split("@")[0];
        log.severe(urn);
        if(!this.activePage.getURN().equals(urn)){
            int id = this.lookupIdDB(urn);
            if (id == -1){
                return true;
            }else{
                this.activePage = new Page(id,urn);
            }
        }
        saveLinesSegDB(this.activePage.getID(), plines, annotations);
        return hasPageDB();
    }

    public boolean returnLine(String[] lwords, int lineNo){
        String urn = lwords[0].split("@")[0];
        if(lineNo != this.activeLine.getLineNo() || !this.activeLine.getURN().split("@")[0].equals(urn)){
            int id = this.lookupIdDB(urn);
            if(id == -1){
                return true;
            }else{
                this.activeLine = new Line(id,lineNo,urn);
            }
        }
        new Line(3,3,"");
        saveWordsDB(this.activeLine.getDocID(),this.activeLine.getLineNo(),lwords);
        return hasLineDB();
    }

    public boolean returnLineSeg(String[] lwords, int lineNo){
        String urn = lwords[0].split("@")[0];
        if(lineNo != this.activeLineSeg.getLineNo() || !this.activeLineSeg.getURN().split("@")[0].equals(urn)){
            int id = this.lookupIdDB(urn);
            if(id == -1){
                return true;
            }else{
                this.activeLineSeg = new LineSeg(id,lineNo,urn, null);
            }
        }
        new LineSeg(3,3,"", "");
        saveWordsDB(this.activeLine.getDocID(),this.activeLine.getLineNo(),lwords);
        return hasLineSegDB();
    }


    public boolean returnWord(String annotation, String[] wletters, int lineNo, int wordNo) {
        String urn = wletters[0].split("@")[0];
        if(this.activeWord.getLineNo() != lineNo || this.activeWord.getWordNo() != wordNo || !this.activeWord.getURN().split("@")[0].equals(urn)){
            int id = this.lookupIdDB(urn);
            if(id == -1){
                return true;
            }else{
                this.activeWord = new Word(id, lineNo, wordNo, urn, null);
            }
        }
        saveLettersDB(this.activeWord.getDocID(),this.activeWord.getLineNo(), this.activeWord.getWordNo(),annotation,wletters);
        return hasWordDB();
    }

    public boolean returnLetter(int timer, String annotation, int difficulty, String urn, int lineNo, int wordNo, int letterNo){
        if(this.activeLetter.getLineNo() != lineNo || this.activeLetter.getWordNo() != wordNo || this.activeLetter.getLetterNo() != letterNo || this.activeLetter.getURN().split("@")[0].equals(urn.split("@")[0])){
            int id = this.lookupIdDB(urn.split("@")[0]);
            if(id == -1){
                return true;
            }else{
                this.activeLetter = new Letter(this.user, id, lineNo, wordNo, letterNo, urn, null);
            }
        }
        saveAnnotationDB(timer,annotation,difficulty);
        return hasLetterDB();
    }

    public boolean returnLineAnno(int timer, String annotation, int difficulty, String urn, int lineNo){
        if(this.activeLineSeg.getLineNo() != lineNo || this.activeLetter.getURN().split("@")[0].equals(urn.split("@")[0])){
            int id = this.lookupIdDB(urn.split("@")[0]);
            if(id == -1){
                return true;
            }else{
                this.activeLineSeg = new LineSeg(this.user, id, lineNo, urn, null);
            }
        }
        saveLineAnnotationDB(timer,annotation,difficulty);
        return hasLineSegDB();
    }

    public boolean[] initalCheck(){
        boolean out[] = new boolean[5];
        out[0] = hasPageDB();
        out[1] = hasLineDB();
        out[2] = hasWordDB();
        out[3] = hasLetterDB();
        out[4] = hasLineSegDB();
        return out;
    }





    //////////////////////////////////////////////////////////////////////////////////////
    //DB functions below!
    //////////////////////////////////////////////////////////////////////////////////////

    private Connection getConnection(){
        try {
              Class.forName(DocumentDatabase.getJdbcDriver());
              return DriverManager.getConnection(DocumentDatabase.getDbLoc(),DocumentDatabase.getDbUser(), DocumentDatabase.getDbPassword());
            }catch(Exception e){
              log.severe("Exception while getting the connection: " + e.getMessage());
              log.severe(e+"");
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
        ResultSet checkTransRes = null;
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
                String addTransSQL = "INSERT IGNORE INTO trans(ID) VALUES (?)";
                addTrans = dbc.prepareStatement(addTransSQL);
                addTrans.setString(1,dbTrans);
                addTrans.executeUpdate();
                addTrans.close();
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
                if (checkTransRes != null) {
                    checkTransRes.close();
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
                    "FROM doc \n" +
                    "WHERE used = false \n" +
                    "ORDER BY RAND() LIMIT 1";
            loadPage = dbc.prepareStatement(loadPageSQL);
            loadPageRes = loadPage.executeQuery();
            if (loadPageRes.next()) {
                this.activePage = new Page(loadPageRes.getInt(1), loadPageRes.getString(2));
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
            String saveLinesSQL = "INSERT IGNORE INTO line (docID,lineNo,URN) VALUES (?,?,?)";
            saveLines = dbc.prepareStatement(saveLinesSQL);
            for(int x = 0; x < lines.length; x++){
                saveLines.setInt(1,page);
                saveLines.setInt(2,x);
                saveLines.setString(3,lines[x]);
                saveLines.executeUpdate();
            }
            saveLines.close();
            String usePageSQL = "UPDATE doc SET used = true WHERE ID = ?";
            usePage = dbc.prepareStatement(usePageSQL);
            usePage.setInt(1,this.activePage.getID());
            usePage.executeUpdate();
            usePage.close();
            dbc.close();
            this.activePage = null;
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


    private void saveLinesSegDB(int page, String[] lines, String[] annotations){
        Connection dbc = null;
        PreparedStatement saveLines = null;
        PreparedStatement usePage = null;
        try {
            dbc = getConnection();
            String saveLinesSQL = "INSERT IGNORE INTO lineSeg (docID,lineSegNo,URN, lineString) VALUES (?,?,?,?)";
            
            saveLines = dbc.prepareStatement(saveLinesSQL);
            for(int x = 0; x < lines.length; x++){
                
                saveLines.setInt(1,page);
                saveLines.setInt(2,x);
                saveLines.setString(3,lines[x]);
                saveLines.setString(4,annotations[x]);
                saveLines.executeUpdate();
            }
            saveLines.close();
            String usePageSQL = "UPDATE doc SET used = true WHERE ID = ?";
            usePage = dbc.prepareStatement(usePageSQL);
            usePage.setInt(1,this.activePage.getID());
            usePage.executeUpdate();
            usePage.close();
            dbc.close();
            this.activePage = null;
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
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
            loadLineRes = loadLine.executeQuery();

            if (loadLineRes.next()) {
                this.activeLine = new Line(loadLineRes.getInt(1), loadLineRes.getInt(2),loadLineRes.getString(3));
            }else{
                log.info(loadLineSQL+" resulted in no return");
            }
            log.info("["+this.activeLine.getDocID() + " , " + this.activeLine.getLineNo() + "]:" + this.activeLine.getURN());
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

    private void loadLineSegDB(){
        Connection dbc = null;
        PreparedStatement loadLine = null;
        ResultSet loadLineRes = null;
        try {
            dbc = getConnection();

            String loadLineSQL = "SELECT * \n" +
                    "FROM lineSeg\n" +
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
            loadLineRes = loadLine.executeQuery();

            if (loadLineRes.next()) {
                this.activeLineSeg = new LineSeg(loadLineRes.getInt(1), loadLineRes.getInt(2),loadLineRes.getString(3), loadLineRes.getString(4));
            }else{
                log.info(loadLineSQL+" resulted in no return");
            }
            log.info("["+this.activeLineSeg.getDocID() + " , " + this.activeLineSeg.getLineNo() + "]:" + this.activeLineSeg.getURN());
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
            String saveWordsSQL = "INSERT IGNORE INTO word (docID,lineNo,wordNo,URN) VALUES (?,?,?,?)";
            saveWords = dbc.prepareStatement(saveWordsSQL);
            for(int x = 0; x < words.length; x ++){
                saveWords.setInt(1,page);
                saveWords.setInt(2,line);
                saveWords.setInt(3,x);
                saveWords.setString(4,words[x]);
                saveWords.executeUpdate();
            }
            saveWords.close();
            String deactivateLineSQL = "UPDATE line SET used = true WHERE docID = ? AND lineNo = ?";
            deactivateLine = dbc.prepareStatement(deactivateLineSQL);
            deactivateLine.setInt(1,page);
            deactivateLine.setInt(2,line);
            deactivateLine.executeUpdate();
            deactivateLine.close();
            dbc.close();
            this.activeLine = null;
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
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadWord = dbc.prepareStatement(loadWordSQL);
            loadWordRes = loadWord.executeQuery();
            if (loadWordRes.next()) {
                this.activeWord = new Word(loadWordRes.getInt(1), loadWordRes.getInt(2), loadWordRes.getInt(3),loadWordRes.getString(4),loadWordRes.getString(5));
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
            String saveLettersSQL = "INSERT IGNORE INTO letter (docID,lineNo,wordNo,letterNo,URN) VALUES (?,?,?,?,?)";
            saveLetters = dbc.prepareStatement(saveLettersSQL);
            for(int x = 0; x < letters.length; x++){
                saveLetters.setInt(1,page);
                saveLetters.setInt(2,line);
                saveLetters.setInt(3,word);
                saveLetters.setInt(4,x);
                saveLetters.setString(5,letters[x]);
                saveLetters.executeUpdate();
            }
            saveLetters.close();
            String deactivateWordSQL = "UPDATE word SET used = true, annotation = ? WHERE docID = ? AND lineNo = ? AND wordNo = ?";
            deactivateWord = dbc.prepareStatement(deactivateWordSQL);
            deactivateWord.setString(1,annotation);
            deactivateWord.setInt(2,page);
            deactivateWord.setInt(3,line);
            deactivateWord.setInt(4,word);
            deactivateWord.executeUpdate();
            deactivateWord.close();
            dbc.close();
            this.activeWord = null;
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
            String loadLetterSQL = "SELECT l.* " +
                    "FROM letter l " +
                    "WHERE NOT EXISTS (SELECT a.* FROM annotation a WHERE a.transID = ? AND a.docID = l.docID AND a.lineNo = l.lineNo AND a.wordNo = l.wordNo AND a.letterNo = l.letterNo) " +
                    "ORDER BY RAND() LIMIT 1 ";
            loadLetter = dbc.prepareStatement(loadLetterSQL);
            loadLetter.setString(1, sanitize(this.user));
            loadLetterRes = loadLetter.executeQuery();
            if (loadLetterRes.next()) {
                this.activeLetter = new Letter(sanitize(this.user), loadLetterRes.getInt(1), loadLetterRes.getInt(2), loadLetterRes.getInt(3),loadLetterRes.getInt(4),loadLetterRes.getString(5),"");
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
            String saveAnnotationSQL = "INSERT IGNORE INTO annotation(transID, docID, lineNo, wordNo, letterNo, annoValue, timer, difficulty) VALUES (?,?,?,?,?,?,?,?)";
            saveAnnotation = dbc.prepareStatement(saveAnnotationSQL);
            saveAnnotation.setString(1,sanitize(this.user));
            saveAnnotation.setInt(2,this.activeLetter.getDocID());
            saveAnnotation.setInt(3,this.activeLetter.getLineNo());
            saveAnnotation.setInt(4,this.activeLetter.getWordNo());
            saveAnnotation.setInt(5,this.activeLetter.getLetterNo());
            saveAnnotation.setString(6,annotation);
            saveAnnotation.setInt(7,timer);
            saveAnnotation.setInt(8,difficulty);

            saveAnnotation.executeUpdate();
            this.activeLetter = null;
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

    private void saveLineAnnotationDB(int timer, String annotation, int difficulty){
        Connection dbc = null;
        PreparedStatement saveAnnotation = null;
        PreparedStatement getCount = null;
        ResultSet getCountRes = null;
        try{
            dbc = getConnection();
            String saveAnnotationSQL = "INSERT IGNORE INTO lineannotation(transID, docID, lineNo, annoValue, timer, difficulty) VALUES (?,?,?,?,?,?,?,?)";
            saveAnnotation = dbc.prepareStatement(saveAnnotationSQL);
            saveAnnotation.setString(1,sanitize(this.user));
            saveAnnotation.setInt(2,this.activeLetter.getDocID());
            saveAnnotation.setInt(3,this.activeLetter.getLineNo());
            saveAnnotation.setString(4,annotation);
            saveAnnotation.setInt(5,timer);
            saveAnnotation.setInt(6,difficulty);

            saveAnnotation.executeUpdate();
            this.activeLetter = null;
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
                    "FROM doc \n" +
                    "WHERE used = false \n" +
                    "ORDER BY RAND() LIMIT 1";
            loadPage = dbc.prepareStatement(loadPageSQL);
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
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
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

    private boolean hasLineSegDB(){
        Connection dbc = null;
        PreparedStatement loadLine = null;
        ResultSet loadLineRes = null;
        boolean out = false;
        try {
            dbc = getConnection();
            String loadLineSQL = "SELECT * \n" +
                    "FROM lineSeg\n" +
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLine = dbc.prepareStatement(loadLineSQL);
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
                    "WHERE used = false\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadWord = dbc.prepareStatement(loadWordSQL);
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
            String loadLetterSQL = "SELECT l.* " +
                    "FROM letter l " +
                    "WHERE NOT EXISTS (SELECT a.* FROM annotation a WHERE a.transID = ? AND a.docID = l.docID AND a.lineNo = l.lineNo AND a.wordNo = l.wordNo AND a.letterNo = l.letterNo) " +
                    "ORDER BY RAND() LIMIT 1 ";
            String loadLetterSQLi = "SELECT * \n" +
                    "FROM letter\n" +
                    "WHERE transID = ? AND annotation IS NULL\n" +
                    "ORDER BY RAND() LIMIT 1";
            loadLetter = dbc.prepareStatement(loadLetterSQL);
            loadLetter.setString(1, sanitize(this.user));
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
                log.severe("SQL Exception: " + se.getMessage()+se.getStackTrace().toString());
                se.printStackTrace();
            }
            return out;
        }
    }

    private int lookupIdDB(String urn){
        Connection dbc = null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        PreparedStatement loadId = null;
        ResultSet loadIdRes = null;
        int out = -1;
        try {
            dbc = getConnection();
            String lookupIdSQL = "SELECT id " +
                    "FROM doc " +
                    "WHERE urn = ? ";
            loadId = dbc.prepareStatement(lookupIdSQL);
            loadId.setString(1,urn);
            loadIdRes = loadId.executeQuery();
            loadIdRes.next();
            out = loadIdRes.getInt(1);
            loadIdRes.close();
            loadId.close();
            dbc.close();
        } catch (SQLException se) {
            se.printStackTrace(pw);
            log.severe("SQL Exception: " + se.getMessage()+ sw.toString());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            log.severe("Exception: " + e.getMessage());
        } finally {
            try {
                if (loadIdRes != null) {
                    loadIdRes.close();
                }
            } catch (SQLException se) {
                se.printStackTrace(pw);
                log.severe("SQL Exception: " + se.getMessage() + sw.toString());
                se.printStackTrace();
            }
            try {
                if (loadId != null) {
                    loadId.close();
                }
            } catch (SQLException se) {
                se.printStackTrace(pw);
                log.severe("SQL Exception: " + se.getMessage()+ sw.toString());
                se.printStackTrace();
            }
            try {
                if (dbc != null) {
                    dbc.close();
                }
            } catch (SQLException se) {
                se.printStackTrace(pw);
                log.severe("SQL Exception: " + se.getMessage()+ sw.toString());
                se.printStackTrace();
            }
            return out;
        }
    }


}
