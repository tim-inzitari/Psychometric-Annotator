package edu.nd.sgrieggs.PsychometricAnnotator.bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class LineSeg {
    int docID;
    int lineNo;
    String URN;
    String lineString;

    public LineSeg( int docID, int lineNo, String URN, String lineString) {
        this.docID = docID;
        this.lineNo = lineNo;
        this.URN = URN;
        this.lineString = lineString;
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

    public String getLineString()
    {
        return lineString;
    }
    public void setLineString(String lineString)
    {
        this.lineString = lineString;
    }
}
