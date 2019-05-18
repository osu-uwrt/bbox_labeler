import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
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
        if (this.DEBUG) {
            System.out.println("Folder: " + videoFolder);
        }
        if (this.pathExists(videoFolder, this.model.pathToYOLO())) {
            videoFolder = this.getSubFolder(videoFolder,
                    this.model.pathToYOLO());
            dataFolder = this.getSubFolder(dataFolder, this.model.pathToYOLO());
            if (this.DEBUG) {
                System.out.println("Folder: " + videoFolder);
                listFolder(videoFolder);
            }
            if (this.pathExists(videoFolder, this.model.pathToVideos())) {
                if (this.pathExists(dataFolder, this.model.pathToData())) {
                    videoFolder = this.getSubFolder(videoFolder,
                            this.model.pathToVideos());
                    dataFolder = this.getSubFolder(dataFolder,
                            this.model.pathToData());
                    //populate dropdown box
                    this.populateDropdownBox(videoFolder);

                    //find videos for that class
                    String selectedClass = this.view.getSelectedClass();
                    String[] classPath = { selectedClass };
                    videoFolder = this.getSubFolder(videoFolder, classPath);
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
                    this.view.closeWindow();
                }
            } else {
                //couldnt find the path
                System.out.print("Could not find path to "
                        + this.model.pathToVideos().toString());
                //Close the window
                this.view.closeWindow();
            }

        } else {
            //couldnt find the path
            System.out.print("Could not find path to "
                    + this.model.pathToYOLO().toString());
            //Close the window
            this.view.closeWindow();
        }
    }

    /**
     * Takes each item in the given folder and gives the thumbnail and the name
     * of the file to the view.
     *
     * @param videoFolder
     */
    private void addVideosToView(BoxFolder videoFolder) {
        BoxAPIConnection api = this.model.api();
        Iterator<Info> it = videoFolder.getChildren().iterator();
        while (it.hasNext()) {
            Info info = it.next();
            if (info instanceof BoxFile.Info) {
                BoxFile file = new BoxFile(api, info.getID());
                this.addVideoToView(file);
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
            for (int i = 1; i <= 10; i++) {
                InputStream bais = new ByteArrayInputStream(thumbnail);
                java.awt.Color c = this.model.getColorNeutral();
                this.view.addVideo(ImageIO.read(bais), name, true, c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the file has already been done.
     *
     * @param file
     * @return false if the video has not been done and true otherwise
     */
    private Boolean hasVideoBeenDone(BoxFile file) {
        //TODO: fill in method
        return false;
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
        String videoDirectory = this.buildLocalUrl();

        File videoFile = new File(
                videoDirectory + this.model.getNameOfSelectedVideo());
        //build the path to the file
        videoFile.getParentFile().mkdirs();
        //create the file
        this.createFile(videoFile);

        BoxAPIConnection api = this.model.api();
        BoxFolder boxVideoFolder = BoxFolder.getRootFolder(api);
        boxVideoFolder = this.getSubFolder(boxVideoFolder,
                this.model.pathToYOLO());
        boxVideoFolder = this.getSubFolder(boxVideoFolder,
                this.model.pathToVideos());
        String selectedClass = this.view.getSelectedClass();
        String[] classPath = { selectedClass };
        boxVideoFolder = this.getSubFolder(boxVideoFolder, classPath);

        this.DownloadFile(api, boxVideoFolder,
                this.model.getNameOfSelectedVideo(), videoDirectory);

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
     * Creates folders on the local machine that are not yet created and then
     * creates the file if needed.
     *
     * @param videoFileUrl
     */
    private void createFile(File videoFile) {
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating local file");
                e.printStackTrace();
            }
        }
    }

    /**
     * Builds a string for the absolute path to where the file will be
     * downloaded to.
     *
     * @return the string that was built
     */
    private String buildLocalUrl() {
        String videoFileUrl = "";
        try {
            videoFileUrl = YOLOBboxController1.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath().toString();
            //get rid of the executable at the end
            videoFileUrl = videoFileUrl.substring(0,
                    videoFileUrl.lastIndexOf("/") + 1);
            //add the name of the folder to put the file in
            videoFileUrl += Config.local_video_folder_name + "/";
        } catch (URISyntaxException e) {
            System.err.println("Error getting the location of the program");
            e.printStackTrace();
        }
        return videoFileUrl;
    }

    /**
     * checks if the path exists in the current folder and returns true if it
     * does and false otherwise
     *
     * @param folder
     * @param path
     * @return true if the sub folder is found
     */
    private boolean pathExists(BoxFolder folder, String[] path) {
        int i = 0;
        Boolean folderFound = true;
        while (i < path.length && folderFound) {
            Iterator<Info> it = folder.getChildren().iterator();
            folderFound = false;
            while (!folderFound && it.hasNext()) {
                Info info = it.next();
                if (this.DEBUG) {
                    System.out.println("Next item: " + info.getName());
                    System.out.println("Matching to: " + path[i]);
                }
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
                if (this.DEBUG) {
                    System.out.println("Matched?: " + folderFound);
                }
            }
            i++;
        }
        System.out.println(folder.toString());
        if (folderFound) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the folder at the end of the given path. Make sure the path
     * exists first with pathExists()
     *
     * @param folder
     *            the root folder where the path starts at
     * @param path
     *            an array of folder names that make the path to find the
     *            subfolder. do not include things like "\" separators
     * @return the subfolder
     */
    private BoxFolder getSubFolder(BoxFolder folder, String[] path) {
        int i = 0;
        Boolean folderFound = true;
        while (i < path.length && folderFound) {
            Iterator<Info> it = folder.getChildren().iterator();
            folderFound = false;
            while (!folderFound && it.hasNext()) {
                Info info = it.next();
                if (this.DEBUG) {
                    System.out.println("Next item: " + info.getName());
                    System.out.println("Matching to: " + path[i]);
                }
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
                if (this.DEBUG) {
                    System.out.println("Matched: " + folderFound);
                }
            }
            i++;
        }
        if (this.DEBUG) {
            System.out.println(folder.toString());
        }
        return folder;
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
     *            Can be relative or absolute but should end with "/"
     */
    private void DownloadFile(BoxAPIConnection api, BoxFolder folder,
            String fileName, String url) {
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
                try {
                    os = new FileOutputStream(url + file.getInfo().getName());

                    //change the gui to show a progress bar
                    System.out
                            .println("File Size: " + file.getInfo().getSize());
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

                    class ProgressListener
                            implements com.box.sdk.ProgressListener {

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
                            double percentComplete = numBytes / totalBytes
                                    * 100;

                            this.count++;
                            if (this.count > 100) {
                                this.count = 0;
                                System.out
                                        .println("Current Bytes: " + numBytes);
                                //System.out.println("Total Bytes: " + totalBytes);
                                //System.out.println("Downloaded " + percentComplete + "%");
                                class SetProgressTask implements Runnable {
                                    private NewSessionView view;

                                    public SetProgressTask(
                                            NewSessionView view) {
                                        this.view = view;
                                    }

                                    @Override
                                    public void run() {
                                        System.out
                                                .println("Try to set progress");
                                        this.view.setProgress(numBytes);

                                    }

                                }
                                SwingUtilities.invokeLater(
                                        new SetProgressTask(this.view));
                            }
                        }
                    }

                    try {
                        if (SwingUtilities.isEventDispatchThread()) {
                            file.download(os, new ProgressListener(this.view));
                        } else {
                            SwingUtilities.invokeAndWait(new DownloadTask(os,
                                    new ProgressListener(this.view)));
                        }
                    } catch (InvocationTargetException
                            | InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println("Download Complete");
                    os.close();
                } catch (IOException e) {
                    System.err.println(
                            "Error occured trying to download: " + fileName);
                    e.printStackTrace();
                }
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

    private static void listFolder(BoxFolder folder) {
        System.out.println("Printing files");
        for (BoxItem.Info itemInfo : folder) {
            System.out.println(itemInfo.getName());
        }
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
        videoFolder = this.getSubFolder(videoFolder, this.model.pathToYOLO());
        videoFolder = this.getSubFolder(videoFolder, this.model.pathToVideos());
        //find videos for that class
        String selectedClass = this.view.getSelectedClass();
        String[] classPath = { selectedClass };
        videoFolder = this.getSubFolder(videoFolder, classPath);
        //add each video to the view
        this.addVideosToView(videoFolder);
        this.model.setNameOfSelectedVideo("");
    }
}
