/**
 * Class for a YOLO file just holds 4 ints
 *
 * @x: the x pixel of the center of the bbox
 * @y: the y pixel of the center of the bbox
 * @width: the width of the bbox
 * @height: the height of the bbox
 *
 * @author Derek Opdycke
 *
 */
public class YOLO {
    private int x = 0;
    private int y = 0;
    private int width = 0;
    private int height = 0;

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    /*
     * Constructors
     */
    public YOLO() {

    }

    public YOLO(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
