import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import com.box.sdk.BoxAPIConnection;

/**
 * Model class.
 *
 * @author Derek Opdycke
 */
public final class NewSessionModel1 implements NewSessionModel {

    /**
     * Model variables.
     */
    BoxAPIConnection api; //Connection to Box
    List<JPanel> videoList; //List of videos
    String[] pathToYOLO; //path in box to find the YOLO folder
    String[] pathToVideos; //path in box to find videos from the YOLO folder
    String[] pathToData; //path in box to find data from the YOLO folder

    /**
     * Default constructor.
     */
    public NewSessionModel1(BoxAPIConnection api) {
        /*
         * Initialize model
         */
        this.api = api;
        this.videoList = new LinkedList<JPanel>();
        this.pathToYOLO = new String[3];
        this.pathToYOLO[0] = "The Underwater Robotics Team";
        this.pathToYOLO[1] = "Software";
        this.pathToYOLO[2] = "YOLO";
        this.pathToData = new String[1];
        this.pathToData[0] = "Training Data";
        this.pathToVideos = new String[1];
        this.pathToVideos[0] = "Raw";
    }

    @Override
    public BoxAPIConnection api() {
        return this.api;
    }

    @Override
    public List<JPanel> videoList() {
        return this.videoList;
    }

    @Override
    public String[] pathToYOLO() {
        return this.pathToYOLO;
    }

    @Override
    public String[] pathToVideos() {
        return this.pathToVideos;
    }

    @Override
    public String[] pathToData() {
        return this.pathToData;
    }

}
