package bo.obj;

/**
 * Created by smgri on 8/30/2017.
 */
public class ImageURL {
    private String imageURL;
    private String URN;
    private int x;
    private int y;

    public ImageURL(int x, int y, String imageURL, String URN){
        this.imageURL = imageURL;
        this.URN = URN;
        this.x = x;
        this.y = y;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getURN() {
        return URN;
    }

    public void setURN(String URN) {
        this.URN = URN;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
