package bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class Line {
    String transID;
    int docID;
    int lineNo;
    String URN;

    public Line(String transID, int docID, int lineNo, String URN) {
        this.transID = transID;
        this.docID = docID;
        this.lineNo = lineNo;
        this.URN = URN;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public String getURN() {
        return URN;
    }

    public void setURN(String URN) {
        this.URN = URN;
    }
}
