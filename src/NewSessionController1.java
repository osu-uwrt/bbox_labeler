import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem.Info;
import com.box.sdk.ProgressListener;

/**
 * Controller class.
 *
 * @author Derek Opdycke
 */
public final class NewSessionController1 implements NewSessionController {

    /**
     * Model object.
     */
    private final NewSessionModel model;

    /**
     * View object.
     */
    private final NewSessionView view;

    private final Boolean DEBUG = true;

    /**
     * Can be 32, 64, 128, and 256 while using png
     */
    private int thumbnailSize = 128;

    /**
     * Constructor; connects {@code this} to the model and view it coordinates.
     *
     * @param model
     *            model to connect to
     * @param view
     *            view to connect to
     */
    public NewSessionController1(NewSessionModel model, NewSessionView view) {
        this.model = model;
        this.view = view;
        BoxAPIConnection api = this.model.api();
        BoxFolder videoFolder = BoxFolder.getRootFolder(api);
        BoxFolder dataFolder = BoxFolder.getRootFolder(api);
        System.out.println("Folder: " + videoFolder);
        if (BoxHelper.pathExists(videoFolder, this.model.pathToYOLO())) {
            videoFolder = BoxHelper.getSubFolder(videoFolder,
                    this.model.pathToYOLO());
            dataFolder = BoxHelper.getSubFolder(dataFolder,
                    this.model.pathToYOLO());
            System.out.println("Folder: " + videoFolder);
            BoxHelper.listFolder(videoFolder);
            if (BoxHelper.pathExists(videoFolder, this.model.pathToVideos())) {
                if (BoxHelper.pathExists(dataFolder, this.model.pathToData())) {
                    videoFolder = BoxHelper.getSubFolder(videoFolder,
                            this.model.pathToVideos());
                    dataFolder = BoxHelper.getSubFolder(dataFolder,
                            this.model.pathToData());
                    //populate dropdown box
                    this.populateDropdownBox(videoFolder);

                    //find videos for that class
                    String selectedClass = this.view.getSelectedClass();
                    String[] classPath = { selectedClass };
                    videoFolder = BoxHelper.getSubFolder(videoFolder,
                            classPath);
                    //add each video to the view
                    this.addVideosToView(videoFolder);
                    if (this.DEBUG) {
                        System.out.println();
                    }
                } else {
                    //couldnt find the path
                    System.out.print("Could not find path to "
                            + this.model.pathToData().toString());
                    //Close the window
                    this.view.disposeFrame();
                }
            } else {
                //couldnt find the path
                System.out.print("Could not find path to "
                        + this.model.pathToVideos().toString());
                //Close the window
                this.view.disposeFrame();
            }

        } else {
            //couldnt find the path
            System.out.print("Could not find path to "
                    + this.model.pathToYOLO().toString());
            //Close the window
            this.view.disposeFrame();
        }
    }

    /**
     * Takes each item in the given folder and gives the thumbnail and the name
     * of the file to the view.
     *
     * @param videoFolder
     */
    private void addVideosToView(BoxFolder videoFolder) {
        //download the video pfile
        BoxHelper.getVideoPFile(this.model.api(), this.view.getSelectedClass());
        BoxAPIConnection api = this.model.api();
        Iterator<Info> it = videoFolder.getChildren().iterator();
        while (it.hasNext()) {
            Info info = it.next();
            if (info instanceof BoxFile.Info) {
                //if it is not a pfile
                if (info.getName().indexOf("pfile") < 0) {
                    BoxFile file = new BoxFile(api, info.getID());
                    this.addVideoToView(file);
                }
            }
        }
    }

    /**
     * Adds the given video to the view.
     *
     * @param file
     */
    private void addVideoToView(BoxFile file) {
        //get the thumbnail
        byte[] thumbnail = file.getThumbnail(BoxFile.ThumbnailFileType.PNG,
                this.thumbnailSize, this.thumbnailSize, this.thumbnailSize,
                this.thumbnailSize);
        if (this.DEBUG) {
            System.out.println("Thumbnail found");
            System.out.println(thumbnail.toString());
        }
        //get the name of the file
        String name = file.getInfo().getName();
        //add the video
        try {
            InputStream bais = new ByteArrayInputStream(thumbnail);
            java.awt.Color c = this.model.getColorNeutral();
            boolean inColor = !FileHelper.hasVideoBeenDone(name);
            this.view.addVideo(ImageIO.read(bais), name, inColor, c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processBeginLabellingEvent() {
        //Open the BBox GUI and give it the API, class, and video name
        if (this.DEBUG) {
            System.out.println("Processing BeginLabellingEvent");
        }
        /**
         * download the video
         */
        //get the directory the program was launched in
        String videoDirectory = FileHelper.userVideoUrl();

        File videoFile = new File(
                videoDirectory + this.model.getNameOfSelectedVideo());
        //build the path to the file
        videoFile.getParentFile().mkdirs();
        //create the file
        FileHelper.createFile(videoFile);

        BoxAPIConnection api = this.model.api();
        BoxFolder boxVideoFolder = BoxFolder.getRootFolder(api);
        boxVideoFolder = BoxHelper.getSubFolder(boxVideoFolder,
                this.model.pathToYOLO());
        boxVideoFolder = BoxHelper.getSubFolder(boxVideoFolder,
                this.model.pathToVideos());
        String selectedClass = this.view.getSelectedClass();
        String[] classPath = { selectedClass };
        boxVideoFolder = BoxHelper.getSubFolder(boxVideoFolder, classPath);

        try {
            this.DownloadFile(api, boxVideoFolder,
                    this.model.getNameOfSelectedVideo(), videoDirectory);
        } catch (InvocationTargetException | IOException
                | InterruptedException e) {
            System.err.println("Error occured trying to download: "
                    + this.model.getNameOfSelectedVideo());
            e.printStackTrace();
        }

        this.loadNextWindow(videoFile);
    }

    /**
     * Loads the next window and closes this one.
     */
    private void loadNextWindow(File file) {
        /*
         * Create instances of the model, view, and controller objects, and
         * initialize them; view needs to know about controller, and controller
         * needs to know about model and view
         */
        YOLOBboxModel model = new YOLOBboxModel1(this.model.api(),
                this.view.getSelectedClass(),
                this.model.getNameOfSelectedVideo(), file);
        YOLOBboxView view = new YOLOBboxView1();
        YOLOBboxController controller = new YOLOBboxController1(model, view);
        view.registerObserver(controller);
        //TODO Close this window
    }

    /**
     * Downloads the file with the name {fileName} in the first level of
     * {folder} and puts it in the location on the local machine given by {url}.
     * The {api} is needed to have access to box.
     *
     * @param api
     * @param folder
     * @param fileName
     * @param url
     *            Can be relative or absolute but end end with "/"
     * @throws IOException
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    private void DownloadFile(BoxAPIConnection api, BoxFolder folder,
            String fileName, String url) throws IOException,
            InvocationTargetException, InterruptedException {
        System.out.println("Attempting to download: " + fileName);
        Iterator<Info> it = folder.getChildren().iterator();
        while (it.hasNext()) {
            Info info = it.next();
            System.out.println("Found file: " + info.getName());
            //if its a file and has the name of the selected video
            if (info instanceof BoxFile.Info
                    && info.getName().equals(fileName)) {
                BoxFile file = new BoxFile(api, info.getID());
                //download the file and put it in the output stream

                FileOutputStream os;
                os = new FileOutputStream(url + file.getInfo().getName());

                //change the gui to show a progress bar
                System.out.println("File Size: " + file.getInfo().getSize());
                this.view.changeToProgressBar(file.getInfo().getSize());
                System.out.println("Downloading File");
                class DownloadTask implements Runnable {
                    FileOutputStream os;
                    ProgressListener pl;

                    public DownloadTask(FileOutputStream os,
                            ProgressListener pl) {
                        this.os = os;
                        this.pl = pl;
                    }

                    @Override
                    public void run() {
                        file.download(this.os, this.pl);
                    }

                }

                class ProgressListener implements com.box.sdk.ProgressListener {

                    private NewSessionView view;
                    private int count;

                    //Constructor
                    ProgressListener(NewSessionView view) {
                        this.view = view;
                        this.count = 0;
                    }

                    @Override
                    public void onProgressChanged(long numBytes,
                            long totalBytes) {
                        double percentComplete = numBytes / totalBytes * 100;

                        this.count++;
                        if (this.count > 100) {
                            this.count = 0;
                            System.out.println("Current Bytes: " + numBytes);
                            //System.out.println("Total Bytes: " + totalBytes);
                            //System.out.println("Downloaded " + percentComplete + "%");
                            class SetProgressTask implements Runnable {
                                private NewSessionView view;

                                public SetProgressTask(NewSessionView view) {
                                    this.view = view;
                                }

                                @Override
                                public void run() {
                                    System.out.println("Try to set progress");
                                    this.view.setProgress(numBytes);

                                }

                            }
                            SwingUtilities.invokeLater(
                                    new SetProgressTask(this.view));
                        }
                    }
                }

                if (SwingUtilities.isEventDispatchThread()) {
                    file.download(os, new ProgressListener(this.view));
                } else {
                    SwingUtilities.invokeAndWait(new DownloadTask(os,
                            new ProgressListener(this.view)));
                }
                System.out.println("Download Complete");
                os.close();
            }
        }
    }

    /**
     * Takes the given box folder and populates the dropdown box with the name
     * of each folder found
     *
     * @param folder
     */
    private void populateDropdownBox(BoxFolder folder) {
        Iterator<Info> it = folder.getChildren().iterator();
        ArrayList<String> classes = new ArrayList<String>();
        while (it.hasNext()) {
            Info info = it.next();
            if (this.DEBUG) {
                System.out.println("Next item: " + info.getName());
            }
            classes.add(info.getName());
        }
        Collections.sort(classes);
        //insert each of the classes to the dropdown box in order
        int i = 0;
        while (i < classes.size()) {
            this.view.addDropdownItem(classes.get(i).toString());
            i++;
        }
        this.view.addListenerToComboBox();
    }

    @Override
    public void processPanelSelect(JPanel jpanel) {
        //change the previously selected panel, if there is one,
        //to the neutral border
        String name = jpanel.getName();
        for (JPanel panel : this.view.getVideoPanelsList()) {
            if (panel.getName().equals(name)) {
                this.view.colorBorder(panel, this.model.getRGBNeutral());
            }
        }
        //change the newly selected panel to the selected border
        this.view.colorBorder(jpanel, this.model.getRGBSelected());
        this.model.setNameOfSelectedVideo(jpanel.getName());
        this.view.enableButton();
    }

    @Override
    public void processClassSelect() {
        //erase all videos in the video panel
        this.view.removeAllVideos();

        /*
         * load all videos for the newly selected class
         */
        BoxAPIConnection api = this.model.api();
        BoxFolder videoFolder = BoxFolder.getRootFolder(api);
        videoFolder = BoxHelper.getSubFolder(videoFolder,
                this.model.pathToYOLO());
        videoFolder = BoxHelper.getSubFolder(videoFolder,
                this.model.pathToVideos());
        //find videos for that class
        String selectedClass = this.view.getSelectedClass();
        String[] classPath = { selectedClass };
        videoFolder = BoxHelper.getSubFolder(videoFolder, classPath);
        //add each video to the view
        this.addVideosToView(videoFolder);
        this.model.setNameOfSelectedVideo("");
    }
}
