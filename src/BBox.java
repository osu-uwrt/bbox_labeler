/**
 * Class for a bounding box just holds 4 ints
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

    private int x1 = 0;
    private int y1 = 0;
    private int x2 = 0;
    private int y2 = 0;

    public int x1() {
        return this.x1;
    }

    public int y1() {
        return this.y1;
    }

    public int x2() {
        return this.x2;
    }

    public int y2() {
        return this.y2;
    }

    public void setx1(int x) {
        this.x1 = x;
    }

    public void sety1(int y) {
        this.y1 = y;
    }

    public void setx2(int x) {
        this.x2 = x;
    }

    public void sety2(int y) {
        this.y2 = y;
    }

    /*
     * Constructors
     */
    public BBox() {

    }

    public BBox(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

}
