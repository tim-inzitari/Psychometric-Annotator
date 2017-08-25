package bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class Letter {
    private String transID;
    private int docID;
    private int lineNo;
    private int wordNo;
    private int letterNo;
    private String URN;
    private String annotation;

    public Letter(String transID, int docID, int lineNo, int wordNo, int letterNo, String URN, String annotation) {
        this.transID = transID;
        this.docID = docID;
        this.lineNo = lineNo;
        this.wordNo = wordNo;
        this.letterNo = letterNo;
        this.URN = URN;
        this.annotation = annotation;
    }

    public String getTransID() {
        return transID;
    }

    public int getDocID() {
        return docID;
    }

    public int getLineNo() {
        return lineNo;
    }

    public int getWordNo() {
        return wordNo;
    }

    public int getLetterNo() {
        return letterNo;
    }

    public String getURN() {
        return URN;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public void setWordNo(int wordNo) {
        this.wordNo = wordNo;
    }

    public void setLetterNo(int letterNo) {
        this.letterNo = letterNo;
    }

    public void setURN(String URN) {
        this.URN = URN;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
}
