import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.box.sdk.BoxFolder;
import com.box.sdk.BoxUser;

/**
 * Controller class.
 *
 * @author Derek Opdycke
 */
public final class YOLOBboxController1 implements YOLOBboxController {

    /**
     * Model object.
     */
    private final YOLOBboxModel model;

    /**
     * View object.
     */
    private final YOLOBboxView view;

    /**
     * Timer for quickly cycling through frames. Is not currently used
     */
    private Timer timer;

    /**
     * Updates view to display model.
     *
     * @param model
     *            the model
     * @param view
     *            the view
     */
    private void updateViewToMatchModel(YOLOBboxModel model,
            YOLOBboxView view) {
        /*
         * Get model info
         */
        int currentFrame = model.currentFrame();
        int frameRate = model.frameRate();
        int frameJump = model.frameJump();
        int totalFrames = model.totalFrames();
        BufferedImage image = model.lines();

        /*
         * Update view to reflect changes in model
         */
        view.updateCurrentFrameTextDisplay(currentFrame);
        view.updateFrameRateTextDisplay(frameRate);
        view.updateFrameJumpTextDisplay(frameJump);
        view.updateTotalFramesTextDisplay(totalFrames);
        view.loadFrame(image);

    }

    /**
     * Constructor; connects {@code this} to the model and view it coordinates.
     *
     * @param model
     *            model to connect to
     * @param view
     *            view to connect to
     */
    public YOLOBboxController1(YOLOBboxModel model, YOLOBboxView view) {
        this.model = model;
        this.view = view;
        BufferedImage scaled = (BufferedImage) this
                .getScaledImage(this.model.master());
        this.model.setScaled(scaled);
        BufferedImage lines = deepCopy(scaled);
        this.model.setLines(lines);

        //load the video
        this.loadVideo();
        //set the default frame jump to the frame rate of the video
        this.model.setFrameJump(this.model.frameRate());
        /*
         * Update view to reflect initial value of model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    private void loadVideo() {
        File file = this.model.file();

        //try to load the file and load the first frame
        try {
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(
                    String.valueOf(file));
            this.model.setFrameGrabber(frameGrabber);
            frameGrabber.start();
            //make sure the file is a video file and has frames
            if (frameGrabber.hasVideo()
                    && frameGrabber.getLengthInFrames() > 0) {
                frameGrabber.setAudioChannels(0);
                Java2DFrameConverter j = new Java2DFrameConverter();
                frameGrabber.setFrameNumber(0);
                Image bi = j.convert(frameGrabber.grabImage());
                this.model.setMaster(bi);
                BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
                this.model.setScaled(scaled);
                this.model.setFrameRate((int) frameGrabber.getFrameRate());
                this.model
                        .setTotalFrames(frameGrabber.getLengthInVideoFrames());

            }
            frameGrabber.stop();

            this.model.setCurrentFrame(frameGrabber.getFrameNumber());

        } catch (IOException e) {
            System.out.println("Trouble Loading File");
        }
    }

    /**
     * Processes reset event.
     */
    @Override
    public void processResetEvent() {
        /*
         * Update model in response to this event
         */
        this.model.setVideoLocation("");
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    private void outputFrame(int id, String fileLocation, String className,
            BufferedImage image, YOLO yolo) {
        this.outputImage(id, fileLocation, className, image);
        this.outputData(id, fileLocation, className, yolo);
    }

    /**
     * Takes the given int and returns a string of the int with leading zeros to
     * make the string 5 characters long.
     *
     * @param i
     *            an integer less than 100,000
     * @return
     */
    private String formattedInteger(int i) {
        String s = Integer.toString(i);
        while (s.length() < 5) {
            s = "0" + s;
        }
        return s;
    }

    private void outputImage(int id, String fileLocation, String className,
            BufferedImage image) {
        String formattedID = this.formattedInteger(id);
        String fileExtension = ".jpg";
        String fileName = fileLocation + className + "_" + formattedID
                + fileExtension;

        File imageFile = new File(fileName);
        //build the path to the file
        imageFile.getParentFile().mkdirs();
        //create the file
        FileHelper.createFile(imageFile);

        try {
            ImageIO.write(image, "jpg", imageFile);
        } catch (IOException e) {
            System.err.println("Problem exporting to jpg");
            e.printStackTrace();
        }

    }

    private void outputData(int id, String fileLocation, String className,
            YOLO yolo) {
        PrintWriter writer;

        String formattedID = this.formattedInteger(id);
        String fileExtension = ".txt";

        File dataFile = new File(
                fileLocation + className + "_" + formattedID + fileExtension);
        //build the path to the file
        dataFile.getParentFile().mkdirs();
        //create the file
        FileHelper.createFile(dataFile);

        try {
            writer = new PrintWriter(dataFile, "UTF-8");
            //print [item index] [x] [y] [width] [height]
            writer.println(this.model.itemIndex() + " " + yolo.x() + " "
                    + yolo.y() + " " + yolo.width() + " " + yolo.height());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println("Problem exporting to text file");
        }

    }

    /**
     * Processes export event.
     */
    @Override
    public void processExportEvent() {

        //create the export directory
        String outputDirectory = FileHelper.userOutputUrl();
        File outputFolder = new File(outputDirectory);
        outputFolder.mkdirs();

        //get the data pfile and the last index from it
        int lastIndex = 0;
        if (this.getDataPFile()) {
            lastIndex = this.getLastIndex();
        }

        //get the video pfile
        //if the pfile doesnt exist on box, just create a new local one
        if (!BoxHelper.getVideoPFile(this.model.api(),
                this.model.className())) {
            File file = new File(
                    FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String className = this.model.className();

        BoxFolder classFolder = BoxFolder.getRootFolder(this.model.api());
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_yolo);
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_data);
        String[] classPath = { className };
        classFolder = BoxHelper.getSubFolder(classFolder, classPath);

        List<Integer> exportedIDs = this.createExportFiles(lastIndex);

        //upload the text and image files to box
        this.uploadTextFiles(exportedIDs, outputDirectory, classFolder,
                className);
        this.uploadImageFiles(exportedIDs, outputDirectory, classFolder,
                className);

        //change pfile
        try {
            this.appendPFile(exportedIDs.get(exportedIDs.size() - 1));
        } catch (IOException e) {
            System.err.println("Failed to append pfile");
            e.printStackTrace();
        }

        //send pfile to export folder
        try {
            this.movePFileToExport();
        } catch (IOException e) {
            System.err.println("Problem moving pfile to export");
            e.printStackTrace();
        }

        //re-upload pfile
        BoxHelper.reuploadFile(Config.training_data_pfile_name, classFolder);

        //change video pfile
        try {
            this.appendVideoPFile(exportedIDs.get(0),
                    exportedIDs.get(exportedIDs.size() - 1));
        } catch (IOException e) {
            System.err.println("Failed to append video pfile");
            e.printStackTrace();
        }

        //send video pfile to export folder
        try {
            this.moveVideoPFileToExport();
        } catch (IOException e) {
            System.err.println("Problem moving video pfile to export");
            e.printStackTrace();
        }

        //reupload video pfile
        BoxFolder videoFolder = BoxFolder.getRootFolder(this.model.api());
        videoFolder = BoxHelper.getSubFolder(videoFolder, Config.path_to_yolo);
        videoFolder = BoxHelper.getSubFolder(videoFolder,
                Config.path_to_videos);
        String[] videoPath = { className };
        videoFolder = BoxHelper.getSubFolder(videoFolder, videoPath);
        BoxHelper.reuploadFile(Config.raw_video_pfile_name, videoFolder);

        //delete local files
        int response = JOptionPane.showConfirmDialog(null,
                "Export Successful! Delete Local Files?");
        if (response == JOptionPane.YES_OPTION) {
            //delete export folder
            File exportFolder = new File(FileHelper.userOutputUrl());
            FileHelper.deleteFolder(exportFolder);
            //delete pfiles
            File pfile = new File(FileHelper.userProgramUrl()
                    + Config.training_data_pfile_name);
            pfile.delete();
            File videopfile = new File(
                    FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
            videopfile.delete();
            //delete video
            File videoFile = this.model.file();
            videoFile.delete();
        }
    }

    private void moveVideoPFileToExport() throws IOException {
        File source = new File(
                FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
        Path sourcePath = source.toPath();
        File destination = new File(
                FileHelper.userOutputUrl() + Config.raw_video_pfile_name);
        Path destinationPath = destination.toPath();
        Files.copy(sourcePath, destinationPath,
                StandardCopyOption.REPLACE_EXISTING);
    }

    private void appendVideoPFile(int startIndex, int lastIndex)
            throws IOException {
        //Append the pfile
        BufferedWriter pFileBufferedWriter = new BufferedWriter(new FileWriter(
                FileHelper.userProgramUrl() + Config.raw_video_pfile_name,
                true));
        pFileBufferedWriter.newLine();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String date = df.format(new Date());
        pFileBufferedWriter
                .write(this.model.file().getName() + ", " + date + ", "
                        + BoxUser.getCurrentUser(this.model.api()).getInfo()
                                .getName()
                        + ", " + startIndex + ", " + lastIndex);
        pFileBufferedWriter.close();
    }

    private List<Integer> createExportFiles(int lastIndex) {
        String className = this.model.className();
        int max = this.model.totalFrames() - 1;
        List<YOLO> yolo = this.model.yolo();
        List<Integer> exportedIDs = new LinkedList<Integer>();
        String outputDirectory = FileHelper.userOutputUrl();

        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        try {
            frameGrabber.start();
            Java2DFrameConverter j = new Java2DFrameConverter();

            //counter for iterating through yolo
            int i = 0;
            while (i < max) {
                //get the YOLO values
                this.findYOLOValues(i);
                //iterate through the yolos and create the files for the ones with data
                YOLO next = yolo.get(i);
                if (next.x() > 0.0 && next.y() > 0.0 && next.width() > 0.0
                        && next.height() > 0.0) {
                    frameGrabber.setFrameNumber(i);
                    BufferedImage image = j.convert(frameGrabber.grabImage());
                    int id = lastIndex + 1;
                    lastIndex++;
                    this.outputFrame(id, outputDirectory, className, image,
                            next);
                    exportedIDs.add(id);
                }
                i++;
            }
            frameGrabber.close();
            System.out.println("Output Files created in : " + outputDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exportedIDs;
    }

    private void movePFileToExport() throws IOException {
        File source = new File(
                FileHelper.userProgramUrl() + Config.training_data_pfile_name);
        Path sourcePath = source.toPath();
        File destination = new File(
                FileHelper.userOutputUrl() + Config.training_data_pfile_name);
        Path destinationPath = destination.toPath();
        Files.copy(sourcePath, destinationPath,
                StandardCopyOption.REPLACE_EXISTING);
    }

    private void appendPFile(int lastIndex) throws IOException {
        //Append the pfile
        BufferedWriter pFileBufferedWriter = new BufferedWriter(new FileWriter(
                FileHelper.userProgramUrl() + Config.training_data_pfile_name,
                true));
        pFileBufferedWriter.newLine();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String date = df.format(new Date());
        pFileBufferedWriter.write(date + ", " + lastIndex);
        pFileBufferedWriter.close();
    }

    /**
     * Downloads the pfile from the data folder on box. Returns true if it was
     * successfully downloaded and false if it wasn't.
     *
     * @return
     */
    private boolean getDataPFile() {

        //download the pfile
        //Get the folder in box where the pfile is at
        BoxFolder classFolder = BoxFolder.getRootFolder(this.model.api());
        //to the yolo folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_yolo);
        //to the data folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_data);
        //to the class folder
        String[] classPath = { this.model.className() };
        if (!BoxHelper.pathExists(classFolder, classPath)) {
            //the the class folder did not exist, create it
            BoxHelper.createFolder(classFolder, this.model.className());
        }
        classFolder = BoxHelper.getSubFolder(classFolder, classPath);
        if (BoxHelper.fileExists(classFolder, "pfile.txt")) {
            //download it
            try {
                BoxHelper.DownloadFile(this.model.api(), classFolder,
                        "pfile.txt", FileHelper.userProgramUrl());
            } catch (InvocationTargetException | IOException
                    | InterruptedException e) {
                System.err
                        .println("Error occured trying to download: pfile.txt");
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private int getLastIndex() {
        int lastIndex = 0;
        //parse the file for the line that starts with the name of the video file
        File pFile = new File(FileHelper.userProgramUrl() + "pfile.txt");
        try {
            BufferedReader pFileBufferedReader = new BufferedReader(
                    new FileReader(pFile));
            String nextLine;
            String previousLine = "";
            while ((nextLine = pFileBufferedReader.readLine()) != null) {
                previousLine = nextLine;
            }
            pFileBufferedReader.close();
            //cut off everything before the comma, the comma, and the space
            previousLine = previousLine
                    .substring(previousLine.indexOf(',') + 2);
            //get the last index value from that line
            lastIndex = Integer.parseInt(previousLine);
        } catch (IOException e) {
            System.err.println("pfile.txt could not be found on local machine");
            e.printStackTrace();
        }
        return lastIndex;
    }

    private void uploadTextFiles(List<Integer> exportedIDs,
            String fileDirectory, BoxFolder folder, String className) {

        String fileExtension = ".txt";
        for (int id : exportedIDs) {
            String formattedID = this.formattedInteger(id);
            String fileName = className + "_" + formattedID + fileExtension;
            BoxHelper.uploadFile(fileName, folder);
        }
    }

    private void uploadImageFiles(List<Integer> exportedIDs,
            String fileDirectory, BoxFolder folder, String className) {

        String fileExtension = ".jpg";
        for (int id : exportedIDs) {
            String formattedID = this.formattedInteger(id);
            String fileName = className + "_" + formattedID + fileExtension;
            BoxHelper.uploadFile(fileName, folder);
        }
    }

    @Override
    public void processReviewEvent() {
        /*
         * TODO: Open new window to display the preview in full screen
         */
    }

    @Override
    public void processFillInFramesEvent() {
        this.processCV();
    }

    /**
     * Processes framesBack event.
     */
    @Override
    public void processFramesBackEvent() {
        /*
         * Update model in response to this event
         */

        //update frame jump in the model
        this.model.setFrameJump(this.view.getFrameJump());
        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        int currentFrame = this.model.currentFrame();
        Frame f = new Frame();
        try {
            frameGrabber.start();
            int jump = this.model.frameJump();
            //load the the frameJump-th previous frame
            if (jump <= currentFrame) {
                frameGrabber.setFrameNumber(currentFrame - jump);
            } else {
                frameGrabber.setFrameNumber(0);
            }
            f = frameGrabber.grabImage();
            Java2DFrameConverter j = new Java2DFrameConverter();
            System.out.println(f);
            BufferedImage bi = j.convert(f);
            System.out.println(bi);
            this.model.setMaster(bi);
            BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
            this.model.setScaled(scaled);
            this.model.setCurrentFrame(frameGrabber.getFrameNumber());
            BufferedImage lines = deepCopy(scaled);
            this.redrawLines(lines,
                    this.model.bbox().get(this.model.currentFrame()));
            this.model.setLines(lines);
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes frameForward event.
     */
    @Override
    public void processFramesForwardEvent() {
        /*
         * Update model in response to this event
         */

        //update frame jump in the model
        this.model.setFrameJump(this.view.getFrameJump());
        FFmpegFrameGrabber frameGrabber = this.model.frameGrabber();
        int currentFrame = this.model.currentFrame();
        int distToEnd = (this.model.totalFrames() - 1)
                - this.model.currentFrame();
        Frame f = new Frame();
        try {
            frameGrabber.start();
            int jump = this.model.frameJump();
            //load the the frameJump-th next frame
            if (distToEnd > jump) {
                frameGrabber.setFrameNumber(currentFrame + jump);
            } else {
                frameGrabber.setFrameNumber(this.model.totalFrames() - 1);
            }
            f = frameGrabber.grabImage();
            Java2DFrameConverter j = new Java2DFrameConverter();
            System.out.println(f);
            BufferedImage bi = j.convert(f);
            System.out.println(bi);
            this.model.setMaster(bi);
            BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
            this.model.setScaled(scaled);
            this.model.setCurrentFrame(frameGrabber.getFrameNumber());
            BufferedImage lines = deepCopy(scaled);
            this.redrawLines(lines,
                    this.model.bbox().get(this.model.currentFrame()));
            this.model.setLines(lines);
            frameGrabber.stop();
        } catch (Exception e) {
            System.out.println("Could not load next frame");
        }
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processSaveEvent() {
        /*
         * Save format: {frame index} {BBox}
         */
        File saveFile = new File(FileHelper.userSaveUrl()
                + this.model.file().getName() + ".txt");
        List<BBox> bbox = this.model.bbox();
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(saveFile));
            for (int i = 0; i < bbox.size(); i++) {
                writer.write(
                        i + " " + bbox.toString() + System.lineSeparator());

            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Problem Saving Session Data");
            e.printStackTrace();
        }
    }

    @Override
    public void processResizeEvent() {
        BufferedImage bi = (BufferedImage) this.model.master();
        BufferedImage scaled = (BufferedImage) this.getScaledImage(bi);
        this.model.setScaled(scaled);
        BufferedImage lines = deepCopy(scaled);
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);
        this.view.updateButtonAreaSize();
        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseClickedEvent(int x, int y) {
        List<BBox> bbox = this.model.bbox();
        BBox temp = bbox.get(this.model.currentFrame());
        //get the proportion of the click relative to the frame
        double dx = ((double) x) / this.model.scaled().getWidth();
        double dy = ((double) y) / this.model.scaled().getHeight();
        //set the pair of values based on the last pair that was set
        if (temp.firstIsSetNext()) {
            temp.setx1(dx);
            temp.sety1(dy);
        } else {
            temp.setx2(dx);
            temp.sety2(dy);
        }
        //set the data where it needs to be
        bbox.set(this.model.currentFrame(), temp);
        //Print out the list
        System.out.println("Frame: " + this.model.currentFrame());
        int i = 0;
        while (i < bbox.size()) {
            if (bbox.get(i).x1() + bbox.get(i).x2() + bbox.get(i).y1()
                    + bbox.get(i).y2() > 0.00001) {
                System.out.println("Frame " + i + ": " + bbox.get(i).x1() + ","
                        + bbox.get(i).y1() + " " + bbox.get(i).x2() + ","
                        + bbox.get(i).y2());
            }
            i++;
        }
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseEnteredEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseExitedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMousePressedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseReleasedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseDraggedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void processMouseMovedEvent(int x, int y) {
        this.model.setLastKnownX((double) x / this.model.scaled().getWidth());
        this.model.setLastKnownY((double) y / this.model.scaled().getHeight());
        BufferedImage lines = deepCopy(this.model.scaled());
        this.redrawLines(lines,
                this.model.bbox().get(this.model.currentFrame()));
        this.model.setLines(lines);

        /*
         * Update view to reflect changes in model
         */
        this.updateViewToMatchModel(this.model, this.view);
    }

    /**
     * Processes the CV by taking the given bboxes and filling in the frames
     * between using linear interpolation
     */
    private void processCV() {
        int i = 0;
        List<BBox> bbox = this.model.bbox();

        //get the first frame with a bounding box
        while (i < bbox.size() && (bbox.get(i).x1() < 0 || bbox.get(i).y1() < 0
                || bbox.get(i).x2() < 0 || bbox.get(i).y2() < 0)) {
            i++;
        }
        //if one was found
        if (i != bbox.size()) {
            BBox first = bbox.get(i);
            int firstIndex = i;
            //get the last frame with a bounding box
            i = bbox.size() - 1;
            while (i > firstIndex
                    && (bbox.get(i).x1() < 0 || bbox.get(i).y1() < 0
                            || bbox.get(i).x2() < 0 || bbox.get(i).y2() < 0)) {
                i--;
            }
            //if one other than the first one was found
            if (i != firstIndex) {
                int lastIndex = i;
                int nextIndex;
                do {

                    //get the next frame after the first with a bounding box
                    i = firstIndex + 1;
                    while (i < bbox.size() && (bbox.get(i).x1() < 0
                            || bbox.get(i).y1() < 0 || bbox.get(i).x2() < 0
                            || bbox.get(i).y2() < 0)) {
                        i++;
                    }
                    BBox next = bbox.get(i);
                    nextIndex = i;
                    //get the difference in frame numbers between the frames
                    int indexDifference = nextIndex - firstIndex;
                    //get the differences in height, width, x, and y between the frames
                    //the data for the first and last frame
                    double firstWidth = Math.abs(first.x1() - first.x2());
                    double firstHeight = Math.abs(first.y1() - first.y2());
                    double firstXCenter = (first.x1() + first.x2()) / 2;
                    double firstYCenter = (first.y1() + first.y2()) / 2;
                    double lastWidth = Math.abs(next.x1() - next.x2());
                    double lastHeight = Math.abs(next.y1() - next.y2());
                    double lastXCenter = (next.x1() + next.x2()) / 2;
                    double lastYCenter = (next.y1() + next.y2()) / 2;
                    //the differences between the first and last frame
                    double heightDifference = lastHeight - firstHeight;
                    double widthDifference = lastWidth - firstWidth;
                    double xDifference = lastXCenter - firstXCenter;
                    double yDifference = lastYCenter - firstYCenter;
                    //the differences per frame between the first and last frame
                    double heightDifferenceScaled = heightDifference
                            / indexDifference;
                    double widthDifferenceScaled = widthDifference
                            / indexDifference;
                    double xDifferenceScaled = xDifference / indexDifference;
                    double yDifferenceScaled = yDifference / indexDifference;
                    int currentIndex = firstIndex + 1;
                    while (currentIndex < nextIndex) {
                        //set the scaled differences for the frames inbetween
                        //the values to set the next the next bbox to
                        double xCenter = firstXCenter + xDifferenceScaled
                                * (currentIndex - firstIndex);
                        double yCenter = firstYCenter + yDifferenceScaled
                                * (currentIndex - firstIndex);
                        double width = firstWidth + widthDifferenceScaled
                                * (currentIndex - firstIndex);
                        double height = firstHeight + heightDifferenceScaled
                                * (currentIndex - firstIndex);
                        double x1 = xCenter - (width / 2);
                        double y1 = yCenter - (height / 2);
                        double x2 = xCenter + (width / 2);
                        double y2 = yCenter + (height / 2);
                        bbox.set(currentIndex, new BBox(x1, y1, x2, y2));
                        currentIndex++;
                    }
                    firstIndex = nextIndex;
                    first = next;
                } while (firstIndex < lastIndex);
            } else {
                //no other bboxes were set
            }
        } else {
            //no bboxes were set
        }
        System.out.println("CV Proccessed");
    }

    /**
     * Uses the bboxes find the center x and y and the width and height for a
     * given frame.
     */
    private void findYOLOValues(int frame) {
        List<BBox> bbox = this.model.bbox();
        List<YOLO> yolo = this.model.yolo();
        BBox p = bbox.get(frame);
        //calculate the values for YOLO from the bbox
        double width = (Math.abs(p.x1() - p.x2()));
        double height = (Math.abs(p.y1() - p.y2()));
        double x = (p.x1() + p.x2()) / 2;
        double y = (p.y1() + p.y2()) / 2;
        //add the values to the yolo map
        YOLO ny = new YOLO(x, y, width, height);
        yolo.add(frame, ny);
    }

    /*
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
     * Converts a buffered image to a Mat needed for opencv
     */
    private static Mat img3Mat(BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer())
                .getData();
        mat.put(0, 0, data);
        return mat;
    }

    /*
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
     */
    public static Mat img2Mat(BufferedImage in) {
        Mat out;
        byte[] data;
        int r, g, b;

        if (in.getType() == BufferedImage.TYPE_INT_RGB) {
            out = new Mat(240, 320, CvType.CV_8UC3);
            data = new byte[320 * 240 * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 320, 240, null, 0, 320);
            for (int i = 0; i < dataBuff.length; i++) {
                data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
                data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
                data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
            }
        } else {
            out = new Mat(240, 320, CvType.CV_8UC1);
            data = new byte[320 * 240 * (int) out.elemSize()];
            int[] dataBuff = in.getRGB(0, 0, 320, 240, null, 0, 320);
            for (int i = 0; i < dataBuff.length; i++) {
                r = (byte) ((dataBuff[i] >> 16) & 0xFF);
                g = (byte) ((dataBuff[i] >> 8) & 0xFF);
                b = (byte) ((dataBuff[i] >> 0) & 0xFF);
                data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); //luminosity
            }
        }
        out.put(0, 0, data);
        return out;
    }

    /*
     * This is not my code. I found it online and just removed some unneeded
     * parts. Not currently used but might be needed for better object tracking.
     * Converts a Mat to a buffered image
     */
    private static BufferedImage mat2Img(Mat in) {
        BufferedImage out;
        byte[] data = new byte[320 * 240 * (int) in.elemSize()];
        int type = BufferedImage.TYPE_3BYTE_BGR;
        ;
        in.get(0, 0, data);

        out = new BufferedImage(320, 240, type);

        out.getRaster().setDataElements(0, 0, 320, 240, data);
        return out;
    }

    /*
     * Returns an image of the appropriately scaled height for the window
     */
    private Image getScaledImage(Image srcImg) {
        double heightRatio = (double) this.model.master().getHeight(null)
                / (double) this.view.getFrameAreaHeight();
        double widthRatio = (double) this.model.master().getWidth(null)
                / (double) this.view.getFrameAreaWidth();
        int newWidth;
        int newHeight;
        if (heightRatio > widthRatio) {
            newHeight = (int) (this.model.master().getHeight(null)
                    / heightRatio);
            newWidth = (int) (this.model.master().getWidth(null) / heightRatio);
        } else {
            newHeight = (int) (this.model.master().getHeight(null)
                    / widthRatio);
            newWidth = (int) (this.model.master().getWidth(null) / widthRatio);
        }
        BufferedImage resizedImg = new BufferedImage(newWidth, newHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, newWidth, newHeight, null);
        g2.dispose();
        return resizedImg;
    }

    private void redrawLines(BufferedImage image, BBox bbox) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        //draw lines for bounding boxes
        int x = (int) (bbox.x1() * image.getWidth());
        g.drawLine(x, 0, x, image.getHeight());
        int y = (int) (bbox.y1() * image.getHeight());
        g.drawLine(0, y, image.getWidth(), y);
        x = (int) (bbox.x2() * image.getWidth());
        g.drawLine(x, 0, x, image.getHeight());
        y = (int) (bbox.y2() * image.getHeight());
        g.drawLine(0, y, image.getWidth(), y);
        //Print out the list
        System.out.println("Frame: ?");
        System.out.println("Frame ?: " + bbox.x1() + "," + bbox.y1() + " "
                + bbox.x2() + "," + bbox.y2());
        //draw lines for cursor
        if (this.model.lastKnownX() >= 0.0 && this.model.lastKnownY() >= 0) {
            drawCrosshairs(image,
                    (int) (this.model.lastKnownX() * image.getWidth()),
                    (int) (this.model.lastKnownY() * image.getHeight()));
        }
    }

    private static void drawCrosshairs(BufferedImage image, int x, int y) {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLACK);
        int dashStart = x;
        final int DASH_LENGTH = 20;
        //Drawing dashed line at: x = x);
        //print the crosshairs
        if (dashStart + (DASH_LENGTH / 2) < image.getWidth()) {
            g.drawLine(dashStart, y, dashStart + (DASH_LENGTH / 2), y);
        } else {
            g.drawLine(dashStart, y, image.getWidth(), y);
        }
        if (dashStart - (DASH_LENGTH / 2) > 0) {
            g.drawLine(dashStart, y, dashStart - (DASH_LENGTH / 2), y);
        } else {
            g.drawLine(dashStart, y, 0, y);
        }
        //print the rest of the dashed line
        dashStart = x + (DASH_LENGTH / 2) + DASH_LENGTH;
        while (dashStart + DASH_LENGTH < image.getWidth()) {
            g.drawLine(dashStart, y, dashStart + DASH_LENGTH, y);
            dashStart += DASH_LENGTH * 2;
        }
        g.drawLine(dashStart, y, image.getWidth(), y);
        dashStart = (x - (DASH_LENGTH / 2)) - DASH_LENGTH;
        while (dashStart - DASH_LENGTH > 0) {
            g.drawLine(dashStart, y, dashStart - DASH_LENGTH, y);
            dashStart -= DASH_LENGTH * 2;
        }
        g.drawLine(dashStart, y, 0, y);

        //Drawing dashed line at: x = x);
        dashStart = y;
        //print the crosshairs
        if (dashStart + (DASH_LENGTH / 2) < image.getHeight()) {
            g.drawLine(x, dashStart, x, dashStart + (DASH_LENGTH / 2));
        } else {
            g.drawLine(x, dashStart, x, image.getHeight());
        }
        if (dashStart - (DASH_LENGTH / 2) > 0) {
            g.drawLine(x, dashStart, x, dashStart - (DASH_LENGTH / 2));
        } else {
            g.drawLine(x, dashStart, x, 0);
        }
        //print the rest of the dashed line
        dashStart = y + (DASH_LENGTH / 2) + DASH_LENGTH;
        while (dashStart + DASH_LENGTH < image.getHeight()) {
            g.drawLine(x, dashStart, x, dashStart + DASH_LENGTH);
            dashStart += DASH_LENGTH * 2;
        }
        g.drawLine(x, dashStart, x, image.getHeight());
        dashStart = (y - (DASH_LENGTH / 2)) - DASH_LENGTH;
        while (dashStart - DASH_LENGTH > 0) {
            g.drawLine(x, dashStart, x, dashStart - DASH_LENGTH);
            dashStart -= DASH_LENGTH * 2;
        }
        g.drawLine(x, dashStart, x, 0);

    }

    /*
     * This is not my code. I found it online.
     */
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
