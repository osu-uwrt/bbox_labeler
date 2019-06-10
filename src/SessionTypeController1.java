import java.io.File;

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
        this.view.disposeFrame();
    }

    @Override
    public void processLoadEvent() {
        this.view.swapToLoadView();
    }

    @Override
    public void processStartEvent() {
        String fileName = this.view.getSelectedVideo();
        File file = new File(FileHelper.userVideoUrl() + fileName);
        //TODO get class name

        class myTask implements Runnable {
            BoxAPIConnection api;
            String fileName;
            File file;

            myTask(BoxAPIConnection api, SessionTypeView view, String fileName,
                    File file) {
                this.api = api;
                this.fileName = fileName;
                this.file = file;
            }

            @Override
            public void run() {
                //Open the next GUI and close this one
                /*
                 * Create instances of the model, view, and controller objects,
                 * and initialize them; view needs to know about controller, and
                 * controller needs to know about model and view
                 */
                YOLOBboxModel model = new YOLOBboxModel1(this.api,
                        this.fileName, this.file);
                YOLOBboxView view = new YOLOBboxView1();
                YOLOBboxController controller = new YOLOBboxController1(model,
                        view);
                view.registerObserver(controller);
                //TODO Close this window

            }

        }
        SwingUtilities.invokeLater(
                new myTask(this.model.api(), this.view, fileName, file));
        this.view.disposeFrame();
    }

    @Override
    public void processBackEvent() {
        this.view.swapToChoiceView();
    }
}
