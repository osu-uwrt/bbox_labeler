/**
 * Class for a YOLO file just holds 4 ints
 *
 * @x: a decimal between 0 and 1 for how far the center of the BBox is from the
 *     left side of the frame
 * @y: a decimal between 0 and 1 for how far the center of the BBox is from the
 *     top of the frame
 * @width: a decimal between 0 and 1 for the width of the bbox
 * @height: a decimal between 0 and 1 for the height of the bbox
 *
 * @author Derek Opdycke
 *
 */
public class YOLO {
    private double x = 0;
    private double y = 0;
    private double width = 0;
    private double height = 0;

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double width() {
        return this.width;
    }

    public double height() {
        return this.height;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setHeight(double h) {
        this.height = h;
    }

    public void setWidth(double w) {
        this.width = w;
    }

    /*
     * Constructors
     */
    public YOLO() {

    }

    public YOLO(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return this.x + " " + this.y + " " + this.width + " " + this.height;
    }
}
