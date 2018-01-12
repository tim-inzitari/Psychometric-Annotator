package edu.nd.sgrieggs.PsychometricAnnotator.bo.obj;

/**
 * Created by smgri on 7/13/2017.
 */
public class Page {
    int ID;
    String URN;

    public Page(int ID, String URN) {
        this.ID = ID;
        this.URN = URN;
    }

    public int getID() {
        return ID;
    }

    public String getURN() {
        return URN;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setURN(String URN) {
        this.URN = URN;
    }
}
