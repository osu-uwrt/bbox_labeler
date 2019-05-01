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

}
