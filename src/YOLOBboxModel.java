import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import com.box.sdk.BoxAPIConnection;

/**
 * Model interface.
 *
 * @author Derek Opdycke
 */
public interface YOLOBboxModel {

    String videoLocation();

    void setVideoLocation(String vl);

    int itemIndex();

    void setItemIndex(int x);

    int currentFrame();

    void setCurrentFrame(int x);

    int frameRate();

    void setFrameRate(int x);

    int frameJump();

    void setFrameJump(int x);

    int totalFrames();

    void setTotalFrames(int x);

    List<BBox> bbox();

    List<YOLO> yolo();

    File file();

    void setFile(File file);

    FFmpegFrameGrabber frameGrabber();

    void setFrameGrabber(FFmpegFrameGrabber frameGrabber);

    Image master();

    void setMaster(Image image);

    BufferedImage scaled();

    void setScaled(BufferedImage image);

    BufferedImage lines();

    void setLines(BufferedImage image);

    int videoHeight();

    int videoWidth();

    double lastKnownX();

    void setLastKnownX(double x);

    double lastKnownY();

    void setLastKnownY(double y);

    BoxAPIConnection api();

    void setApi(BoxAPIConnection api);

    String className();

    void setClassName(String className);

    String videoName();

    void setVideoName(String videoName);

}
