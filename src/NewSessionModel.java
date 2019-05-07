import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;

import com.box.sdk.BoxAPIConnection;

/**
 * Model interface.
 *
 * @author Derek Opdycke
 */
public interface NewSessionModel {

    public BoxAPIConnection api();

    public List<JPanel> videoList();

    public String[] pathToYOLO();

    public String[] pathToVideos();

    public String[] pathToData();

    public String getNameOfSelectedVideo();

    public void setNameOfSelectedVideo(String name);

    public int[] getRGBNeutral();

    public int[] getRGBSelected();

    public Color getColorNeutral();

    public Color getColorSelected();

}
