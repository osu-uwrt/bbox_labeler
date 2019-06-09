import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import com.box.sdk.BoxAPIConnection;

/**
 * Model class.
 *
 * @author Derek Opdycke
 */
public final class YOLOBboxModel1 implements YOLOBboxModel {

    /**
     * Model variables.
     */
    private String videoLocation;
    private int itemIndex, currentFrame, frameRate, frameJump, totalFrames,
            videoWidth, videoHeight;
    private List<BBox> bbox;
    private List<YOLO> yolo;
    private File file;
    private FFmpegFrameGrabber frameGrabber;
    private Image master;
    private BufferedImage scaled;
    private BufferedImage lines;
    private double lastKnownX, lastKnownY;

    private BoxAPIConnection api; //Connection to Box
    private String className, videoName;

    private boolean shouldLoadData;

    public YOLOBboxModel1(BoxAPIConnection api, String className,
            String videoName, File file) {
        /*
         * Initialize model
         */
        this.api = api;
        this.videoName = videoName;
        this.className = className;
        this.videoLocation = "";//File location of the video
        this.itemIndex = 0;//index of the item being identified
        this.currentFrame = 0;//the index of the frame that is being shown
        this.frameRate = 0;//the frame rate of the given video
        this.frameJump = 2;//the number of frames to jump forward or backward
        this.bbox = new LinkedList<BBox>();//holds the bbox values for each frame
        this.totalFrames = 0;//the total number of frames in the video
        this.yolo = new LinkedList<YOLO>();//holds the volo values for each frame
        this.file = file;//the video file
        //used to grab individual frames from the video
        this.frameGrabber = new FFmpegFrameGrabber(String.valueOf(this.file));
        try {
            //the untouched version of a frame
            this.master = ImageIO.read(new File("data/default.png"));
        } catch (IOException e) {
            System.out.println("Default image not found");
            this.master = new BufferedImage(200, 200,
                    BufferedImage.TYPE_INT_RGB);
        }
        //the scaled version of a frame
        this.scaled = (BufferedImage) this.master;
        //the scaled version of a frame with crosshairs drawn on it
        this.lines = this.scaled;
        //the height of the video
        this.videoHeight = this.frameGrabber.getImageHeight();
        //the width of the video
        this.videoWidth = this.frameGrabber.getImageWidth();
        //the last known x-coordinate of the mouse on the video
        this.lastKnownX = -1.0;
        //the last known y-coordinate of the mouse on the video
        this.lastKnownY = -1.0;
        this.shouldLoadData = false;
    }

    public YOLOBboxModel1(BoxAPIConnection api, String videoName, File file) {
        /*
         * Initialize model
         */
        this.api = api;
        this.videoName = videoName;
        File loadFile = new File(
                FileHelper.userSaveUrl() + this.videoName + ".txt");
        BufferedReader reader;
        this.className = "";
        try {
            reader = new BufferedReader(new FileReader(loadFile));
            this.className = reader.readLine();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        this.videoLocation = "";//File location of the video
        this.itemIndex = 0;//index of the item being identified
        this.currentFrame = 0;//the index of the frame that is being shown
        this.frameRate = 0;//the frame rate of the given video
        this.frameJump = 2;//the number of frames to jump forward or backward
        this.bbox = new LinkedList<BBox>();//holds the bbox values for each frame
        this.totalFrames = 0;//the total number of frames in the video
        this.yolo = new LinkedList<YOLO>();//holds the volo values for each frame
        this.file = file;//the video file
        //used to grab individual frames from the video
        this.frameGrabber = new FFmpegFrameGrabber(String.valueOf(this.file));
        try {
            //the untouched version of a frame
            this.master = ImageIO.read(new File("data/default.png"));
        } catch (IOException e) {
            System.out.println("Default image not found");
            this.master = new BufferedImage(200, 200,
                    BufferedImage.TYPE_INT_RGB);
        }
        //the scaled version of a frame
        this.scaled = (BufferedImage) this.master;
        //the scaled version of a frame with crosshairs drawn on it
        this.lines = this.scaled;
        //the height of the video
        this.videoHeight = this.frameGrabber.getImageHeight();
        //the width of the video
        this.videoWidth = this.frameGrabber.getImageWidth();
        //the last known x-coordinate of the mouse on the video
        this.lastKnownX = -1.0;
        //the last known y-coordinate of the mouse on the video
        this.lastKnownY = -1.0;
        this.shouldLoadData = true;
    }

    @Override
    public void loadData() throws NumberFormatException, IOException {
        File loadFile = new File(
                FileHelper.userSaveUrl() + this.videoName + ".txt");
        BufferedReader reader = new BufferedReader(new FileReader(loadFile));
        String text;
        this.className = reader.readLine();
        while ((text = reader.readLine()) != null) {
            System.out.println("Line read: " + text);
            String regex = "([0-9]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);
            matcher.find();
            int index = Integer.parseInt(matcher.group());
            regex = "(-?[0-9]+[.][0-9]+)";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(text);
            matcher.find();
            double firstValue = Double.parseDouble(matcher.group());
            matcher.find();
            double secondValue = Double.parseDouble(matcher.group());
            matcher.find();
            double thirdValue = Double.parseDouble(matcher.group());
            matcher.find();
            double fourthValue = Double.parseDouble(matcher.group());
            matcher.find();
            this.bbox.set(index,
                    new BBox(firstValue, secondValue, thirdValue, fourthValue));
        }
        reader.close();
    }

    @Override
    public void setVideoLocation(String vl) {
        this.videoLocation = vl;
    }

    @Override
    public String videoLocation() {
        return this.videoLocation;
    }

    @Override
    public void setItemIndex(int x) {
        this.itemIndex = x;
    }

    @Override
    public int itemIndex() {
        return this.itemIndex;
    }

    @Override
    public void setCurrentFrame(int x) {
        this.currentFrame = x;
        System.out.println("Current Frame: " + this.currentFrame);
    }

    @Override
    public int currentFrame() {
        return this.currentFrame;
    }

    @Override
    public void setFrameRate(int x) {
        this.frameRate = x;
    }

    @Override
    public int frameRate() {
        return this.frameRate;
    }

    @Override
    public void setFrameJump(int x) {
        this.frameJump = x;
    }

    @Override
    public int frameJump() {
        return this.frameJump;
    }

    @Override
    public void setTotalFrames(int x) {
        this.totalFrames = x;
        while (this.bbox.size() < x) {
            this.bbox.add(new BBox());
        }
        if (this.shouldLoadData) {
            try {
                this.loadData();
            } catch (Exception e) {
                System.err.println("Error loading data");
                e.printStackTrace();
            }
        }

    }

    @Override
    public int totalFrames() {
        return this.totalFrames;
    }

    @Override
    public List<BBox> bbox() {
        return this.bbox;
    }

    @Override
    public List<YOLO> yolo() {
        return this.yolo;
    }

    @Override
    public File file() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public FFmpegFrameGrabber frameGrabber() {
        return this.frameGrabber;
    }

    @Override
    public void setFrameGrabber(FFmpegFrameGrabber frameGrabber) {
        this.frameGrabber = frameGrabber;
        this.videoHeight = this.frameGrabber.getImageHeight();
        this.videoWidth = this.frameGrabber.getImageWidth();
    }

    @Override
    public Image master() {
        return this.master;
    }

    @Override
    public void setMaster(Image image) {
        this.master = image;
    }

    @Override
    public BufferedImage scaled() {
        return this.scaled;
    }

    @Override
    public void setScaled(BufferedImage image) {
        this.scaled = image;
        this.videoHeight = image.getHeight();
        this.videoWidth = image.getWidth();
    }

    @Override
    public int videoHeight() {
        return this.videoHeight;
    }

    @Override
    public int videoWidth() {
        return this.videoWidth;
    }

    @Override
    public BufferedImage lines() {
        return this.lines;
    }

    @Override
    public void setLines(BufferedImage lines) {
        this.lines = lines;
    }

    @Override
    public double lastKnownX() {
        return this.lastKnownX;
    }

    @Override
    public void setLastKnownX(double x) {
        this.lastKnownX = x;
    }

    @Override
    public double lastKnownY() {
        return this.lastKnownY;
    }

    @Override
    public void setLastKnownY(double y) {
        this.lastKnownY = y;
    }

    @Override
    public BoxAPIConnection api() {
        return this.api;
    }

    @Override
    public void setApi(BoxAPIConnection api) {
        this.api = api;
    }

    @Override
    public String className() {
        return this.className;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String videoName() {
        return this.videoName;
    }

    @Override
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    @Override
    public String[] pathToClass() {
        String[] path = { this.className };
        return path;
    }

}
