import java.awt.Color;
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
    String nameOfSelectedVideo; //the name of the file currently selected
    //the RGB values for a video that is not selected
    int[] RGBNeutral;
    //the RGB values for a video that is selected
    int[] RGBSelected;
    Color colorSelected;
    Color colorNeutral;

    /**
     * Default constructor.
     */
    public NewSessionModel1(BoxAPIConnection api) {
        /*
         * Initialize model
         */
        this.api = api;
        this.videoList = new LinkedList<JPanel>();
        this.pathToYOLO = Config.path_to_yolo;
        this.pathToData = Config.path_to_data;
        this.pathToVideos = Config.path_to_videos;
        this.nameOfSelectedVideo = "";
        this.RGBNeutral = new int[3];
        this.RGBNeutral[0] = 1;
        this.RGBNeutral[1] = 64;
        this.RGBNeutral[2] = 255;
        this.RGBSelected = new int[3];
        this.RGBSelected[0] = 0;
        this.RGBSelected[1] = 255;
        this.RGBSelected[2] = 36;
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

    @Override
    public String getNameOfSelectedVideo() {
        return this.nameOfSelectedVideo;
    }

    @Override
    public void setNameOfSelectedVideo(String name) {
        this.nameOfSelectedVideo = name;
    }

    @Override
    public int[] getRGBNeutral() {
        return this.RGBNeutral;
    }

    @Override
    public int[] getRGBSelected() {
        return this.RGBSelected;
    }

    @Override
    public Color getColorNeutral() {
        return new Color(this.RGBNeutral[0], this.RGBNeutral[1],
                this.RGBNeutral[2]);
    }

    @Override
    public Color getColorSelected() {
        return new Color(this.RGBSelected[0], this.RGBSelected[1],
                this.RGBSelected[2]);
    }
}
