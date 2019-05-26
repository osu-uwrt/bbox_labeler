/**
 *
 * @author Derek Opdycke
 *
 */

public class YOLOBboxGUI {

    public static void main(String[] args) {
        /*
         * Create instances of the model, view, and controller objects, and
         * initialize them; view needs to know about controller, and controller
         * needs to know about model and view
         */
        YOLOBboxModel model = new YOLOBboxModel1();
        YOLOBboxView view = new YOLOBboxView1();
        YOLOBboxController controller = new YOLOBboxController1(model, view);

        view.registerObserver(controller);

        YOLOBboxModel model2 = new YOLOBboxModel1();
        YOLOBboxView view2 = new YOLOBboxView1();
        YOLOBboxController controller2 = new YOLOBboxController1(model2, view2);

        view2.registerObserver(controller2);
    }
}
