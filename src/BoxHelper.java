import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxItem.Info;

public class BoxHelper {

    /**
     * Creates a new subfolder in the directory of the given BoxFolder.
     *
     * @param folder
     *            The folder in box where the new folder will be created
     * @param newSubFolder
     *            the name to give to the new subfolder
     */
    public static void createFolder(BoxFolder folder, String newSubFolder) {
        folder.createFolder(newSubFolder);
    }

    /**
     * Checks the given folder for a file with the given name in the highest
     * directory.
     *
     * @param folder
     *            The folder to search in
     * @param name
     *            The name of the file to search for
     * @return True if the file is found; False otherwise
     */
    public static boolean fileExists(BoxFolder folder, String name) {
        Iterator<Info> it = folder.getChildren().iterator();
        while (it.hasNext()) {
            Info info = it.next();
            if (info.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uploads the file with the given name from the exports folder to box.
     *
     * @param fileName
     *            the name of the file to be uploaded
     */
    public static void uploadFile(String fileName, BoxFolder folder) {
        try {
            System.out.println("Trying to upload file: " + fileName);
            File file = new File(FileHelper.userOutputUrl() + fileName);
            folder.canUpload(fileName, file.length());

            //If it can be uploaded, upload the file
            InputStream is = new FileInputStream(file);
            folder.uploadFile(is, fileName);
            is.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found to upload.");
            e.printStackTrace();
        } catch (BoxAPIException e) {
            System.out.println(
                    "File can not uploaded, likely due to insufficient space.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(
                    "I/O Exception while attempting to upload file to box.");
            e.printStackTrace();
        }
    }

    /**
     * Uploads the file with the given name from the exports folder to box. If
     * the file already exists on Box, replace it.
     *
     * @param fileName
     *            the name of the file to be uploaded
     */
    public static void reuploadFile(String fileName, BoxFolder folder) {
        try {
            System.out.println("Trying to upload file: " + fileName);
            File file = new File(FileHelper.userOutputUrl() + fileName);

            //If it can be uploaded, upload the file
            InputStream is = new FileInputStream(file);
            if (BoxHelper.fileExists(folder, fileName)) {
                Iterator<Info> it = folder.getChildren().iterator();
                boolean fileFound = false;
                while (!fileFound && it.hasNext()) {
                    Info info = it.next();
                    if (info.getName().equals(fileName)) {
                        fileFound = true;
                        BoxFile boxFile = (BoxFile) info.getResource();
                        boxFile.canUploadVersion(fileName, file.length());
                        boxFile.uploadNewVersion(is);
                        fileFound = true;
                    }
                }
            } else {
                folder.canUpload(fileName, file.length());
                folder.uploadFile(is, fileName);
            }
            is.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found to upload.");
            e.printStackTrace();
        } catch (BoxAPIException e) {
            System.out.println(
                    "File can not uploaded, likely due to insufficient space.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(
                    "I/O Exception while attempting to upload file to box.");
            e.printStackTrace();
        }
    }

    /**
     * checks if the path exists in the current folder and returns true if it
     * does and false otherwise
     *
     * @param folder
     * @param path
     * @return true if the sub folder is found
     */
    public static boolean pathExists(BoxFolder folder, String[] path) {
        int i = 0;
        Boolean folderFound = true;
        while (i < path.length && folderFound) {
            Iterator<Info> it = folder.getChildren().iterator();
            folderFound = false;
            while (!folderFound && it.hasNext()) {
                Info info = it.next();
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
            }
            i++;
        }
        if (folderFound) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the file has already been done.
     *
     * @param file
     * @return false if the video has not been done and true otherwise
     */
    public static Boolean hasVideoBeenDone(BoxFile file) {
        //TODO: fill in method
        return false;
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
    public static BoxFolder getSubFolder(BoxFolder folder, String[] path) {
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

    public static void listFolder(BoxFolder folder) {
        System.out.println("Printing files");
        for (BoxItem.Info itemInfo : folder) {
            System.out.println(itemInfo.getName());
        }
    }

}
