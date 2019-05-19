import javax.swing.SwingUtilities;

import com.box.sdk.BoxAPIConnection;

/**
 * Controller class.
 *
 * @author Derek Opdycke
 */
public final class SessionTypeController1 implements SessionTypeController {

    /**
     * Model object.
     */
    private final SessionTypeModel model;

    /**
     * View object.
     */
    private final SessionTypeView view;

    /**
     * Constructor; connects {@code this} to the model and view it coordinates.
     *
     * @param model
     *            model to connect to
     * @param view
     *            view to connect to
     */
    public SessionTypeController1(SessionTypeModel model,
            SessionTypeView view) {
        this.model = model;
        this.view = view;
        this.view.setWelcomeLabel("<html>Welcome, " + this.model.username()
                + "<br>What would you like to do?</html>");
    }

    @Override
    public void processNewEvent() {
        System.out.println("Begin New Session Button Pressed");

        class myTask implements Runnable {
            BoxAPIConnection api;

            myTask(BoxAPIConnection api) {
                this.api = api;
            }

            @Override
            public void run() {
                //Open the next GUI and close this one
                /*
                 * Create instances of the model, view, and controller objects,
                 * and initialize them; view needs to know about controller, and
                 * controller needs to know about model and view
                 */
                NewSessionModel model = new NewSessionModel1(this.api);
                NewSessionView view = new NewSessionView1();
                NewSessionController controller = new NewSessionController1(
                        model, view);
                view.registerObserver(controller);
                //TODO Close this window
            }

        }
        SwingUtilities.invokeLater(new myTask(this.model.api()));
    }

    @Override
    public void processLoadEvent() {
        System.out.println("Load Previous Session Button Pressed");
    }
}
