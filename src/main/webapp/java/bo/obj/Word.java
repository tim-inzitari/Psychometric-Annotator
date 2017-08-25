package bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class Word {
    private String transID;
    private int docID;
    private int lineNo;
    private int wordNo;
    private String URN;

    public Word(String transID, int docID, int lineNo, int wordNo, String URN) {
        this.transID = transID;
        this.docID = docID;
        this.lineNo = lineNo;
        this.wordNo = wordNo;
        this.URN = URN;
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

    public String getURN() {
        return URN;
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

    public void setURN(String URN) {
        this.URN = URN;
    }
}
