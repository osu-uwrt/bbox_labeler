import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.ImageIO;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxItem.Info;

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
        if (this.pathExists(videoFolder, this.model.pathToYOLO())) {
            videoFolder = this.getSubFolder(videoFolder,
                    this.model.pathToYOLO());
            dataFolder = this.getSubFolder(dataFolder, this.model.pathToYOLO());
            System.out.println("Folder: " + videoFolder);
            listFolder(videoFolder);
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
                    Iterator<Info> it = videoFolder.getChildren().iterator();
                    System.out.println("1");
                    while (it.hasNext()) {
                        System.out.println("2");
                        Info info = it.next();
                        if (info instanceof BoxFile.Info) {
                            BoxFile file = new BoxFile(api, info.getID());
                            System.out.println("3");
                            //get the thumbnail
                            byte[] thumbnail = file.getThumbnail(
                                    BoxFile.ThumbnailFileType.PNG,
                                    this.thumbnailSize, this.thumbnailSize,
                                    this.thumbnailSize, this.thumbnailSize);
                            System.out.println("Thumbnail found");
                            System.out.println(thumbnail.toString());
                            //get the name of the file
                            String name = info.getName();
                            //add the video
                            try {
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                                this.view.addVideo(ImageIO.read(
                                        new ByteArrayInputStream(thumbnail)),
                                        name, true);
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
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

    @Override
    public void processBeginLabellingEvent() {
        //Make sure a class is selected

        //Make sure a video is selected

        //Open the BBox GUI and give it the API, class, and video name
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
                System.out.println("Next item: " + info.getName());
                System.out.println("Matching to: " + path[i]);
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
                System.out.println("Matched?: " + folderFound);
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
                System.out.println("Next item: " + info.getName());
                System.out.println("Matching to: " + path[i]);
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
                System.out.println("Matched: " + folderFound);
            }
            i++;
        }
        System.out.println(folder.toString());
        return folder;
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
            System.out.println("Next item: " + info.getName());
            classes.add(info.getName());
        }
        Collections.sort(classes);
        //insert each of the classes to the dropdown box in order
        int i = 0;
        while (i < classes.size()) {
            this.view.addDropdownItem(classes.get(i).toString());
            i++;
        }
    }

    private static void listFolder(BoxFolder folder) {
        System.out.println("Printing files");
        for (BoxItem.Info itemInfo : folder) {
            System.out.println(itemInfo.getName());
        }
    }
}
