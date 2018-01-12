package edu.nd.sgrieggs.PsychometricAnnotator.io;

/**
 * Created by smgri on 1/2/2018.
 */

public class DocumentDatabase {

    private static String JDBC_DRIVER = null;
    private static String DB_LOC = null;
    private static String DB_USER = null;
    private static String DB_PASSWORD = null;

    public static void initialize(String driver, String location, String user, String password){
        JDBC_DRIVER = driver;
        DB_LOC = location;
        DB_USER = user;
        DB_PASSWORD = password;
    }

    public static String getJdbcDriver() {
        return JDBC_DRIVER;
    }

    public static String getDbLoc() {
        return DB_LOC;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }



}
