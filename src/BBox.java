/**
 * Class for a bounding box just holds 4 doubles
 *
 * @x1: the xth pixel of the frame for the first corner
 * @y1: the yth pixel of the frame for the first corner
 * @x2: the xth pixel of the frame for the second corner
 * @y2: the yth pixel of the frame for the second corner
 *
 * @author Derek Opdycke
 *
 */
public class BBox {

    private double x1 = 0;
    private double y1 = 0;
    private double x2 = 0;
    private double y2 = 0;

    public double x1() {
        return this.x1;
    }

    public double y1() {
        return this.y1;
    }

    public double x2() {
        return this.x2;
    }

    public double y2() {
        return this.y2;
    }

    public void setx1(double x) {
        this.x1 = x;
    }

    public void sety1(double y) {
        this.y1 = y;
    }

    public void setx2(double x) {
        this.x2 = x;
    }

    public void sety2(double y) {
        this.y2 = y;
    }

    /*
     * Constructors
     */
    public BBox() {

    }

    public BBox(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

}
