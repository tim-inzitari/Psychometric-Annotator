package edu.nd.sgrieggs.PsychometricAnnotator.bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class Line {
    int docID;
    int lineNo;
    String URN;

    public Line( int docID, int lineNo, String URN) {
        this.docID = docID;
        this.lineNo = lineNo;
        this.URN = URN;
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
