import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxItem.Info;

public class BoxHelper {

    /**
     * Downloads the pfile from the data folder on box. Returns true if it was
     * successfully downloaded and false if it wasn't.
     *
     * @return
     */
    public static boolean getValidationFile(BoxAPIConnection api,
            String validationFileName) {

        //download the pfile
        //Get the folder in box where the pfile is at
        BoxFolder classFolder = BoxFolder.getRootFolder(api);
        //to the yolo folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_yolo);
        //to the data folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_data);
        if (BoxHelper.fileExists(classFolder, validationFileName)) {
            //download it
            try {
                BoxHelper.DownloadFile(api, classFolder, validationFileName,
                        FileHelper.userProgramUrl());
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

    /**
     * Downloads the pfile from the data folder on box. Returns true if it was
     * successfully downloaded and false if it wasn't.
     *
     * @return
     */
    public static boolean getTrainingFile(BoxAPIConnection api,
            String trainingFileName) {

        //download the pfile
        //Get the folder in box where the pfile is at
        BoxFolder classFolder = BoxFolder.getRootFolder(api);
        //to the yolo folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_yolo);
        //to the data folder
        classFolder = BoxHelper.getSubFolder(classFolder, Config.path_to_data);
        if (BoxHelper.fileExists(classFolder, trainingFileName)) {
            //download it
            try {
                BoxHelper.DownloadFile(api, classFolder, trainingFileName,
                        FileHelper.userProgramUrl());
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

    /**
     * Downloads the file with the name {fileName} in the first level of
     * {folder} and puts it in the location on the local machine given by {url}.
     * The {api} is needed to have access to box.
     *
     * @param api
     * @param folder
     * @param fileName
     * @param url
     *            Can be relative or absolute but must end with a file separator
     * @throws IOException
     * @throws InterruptedException
     * @throws InvocationTargetException
     */
    public static void DownloadFile(BoxAPIConnection api, BoxFolder folder,
            String fileName, String url) throws IOException,
            InvocationTargetException, InterruptedException {
        Iterator<Info> it = folder.getChildren().iterator();
        while (it.hasNext()) {
            Info info = it.next();
            //if its a file and has the name of the selected video
            if (info instanceof BoxFile.Info
                    && info.getName().equals(fileName)) {
                BoxFile file = new BoxFile(api, info.getID());
                //download the file and put it in the output stream
                FileOutputStream os;
                os = new FileOutputStream(url + file.getInfo().getName());
                file.download(os);
                os.close();
            }
        }
    }

    /**
     * Downloads the pfile from the given class in the videos folder on Box to
     * the local machine. If it is successfully downloaded, it returns true;
     * otherwise it returns false.
     *
     * @param api
     * @param className
     * @return
     */
    public static boolean getVideoPFile(BoxAPIConnection api,
            String className) {

        //download the pfile
        //Get the folder in box where the pfile is at
        BoxFolder videoFolder = BoxFolder.getRootFolder(api);
        //to the yolo folder
        videoFolder = BoxHelper.getSubFolder(videoFolder, Config.path_to_yolo);
        //to the data folder
        videoFolder = BoxHelper.getSubFolder(videoFolder,
                Config.path_to_videos);
        //to the class folder
        String[] videoPath = { className };
        videoFolder = BoxHelper.getSubFolder(videoFolder, videoPath);
        if (BoxHelper.fileExists(videoFolder, Config.raw_video_pfile_name)) {
            //download it
            try {
                DownloadFile(api, videoFolder, Config.raw_video_pfile_name,
                        FileHelper.userProgramUrl());
            } catch (InvocationTargetException | IOException
                    | InterruptedException e) {
                System.err.println("Error occured trying to download: "
                        + Config.raw_video_pfile_name);
                e.printStackTrace();
            }
        }
        return true;
    }

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
                if (info.getName().equals(path[i])) {
                    folderFound = true;
                    folder = (BoxFolder) info.getResource();
                }
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
