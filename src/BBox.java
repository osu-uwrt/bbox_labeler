/**
 * Class for a bounding box just holds 4 doubles
 *
 * @x1: the proportion of the frame from the left for the first corner
 * @y1: the proportion of the frame from the top for the first corner
 * @x2: the proportion of the frame from the left for the second corner
 * @y2: the proportion of the frame from the top for the second corner
 *
 * @author Derek Opdycke
 *
 */
public class BBox {

    private double x1 = -1.0;
    private double y1 = -1.0;
    private double x2 = -1.0;
    private double y2 = -1.0;
    private boolean firstIsSetNext = true;

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
        this.firstIsSetNext = false;
        this.x1 = x;
    }

    public void sety1(double y) {
        this.firstIsSetNext = false;
        this.y1 = y;
    }

    public void setx2(double x) {
        this.firstIsSetNext = true;
        this.x2 = x;
    }

    public void sety2(double y) {
        this.firstIsSetNext = true;
        this.y2 = y;
    }

    public boolean firstIsSetNext() {
        return this.firstIsSetNext;
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

    @Override
    public String toString() {
        return this.x1 + " " + this.y1 + " " + this.x2 + " " + this.y2;
    }

    public boolean isSet() {
        return this.x1 > 0 || this.x2 > 0 || this.y1 > 0 || this.y2 > 0;
    }
}
