import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class FileHelper {

    /**
     * Builds a string for the absolute path in the user's program folder where
     * the file will be downloaded to.
     *
     * @return the path of the directory to put the video in
     */
    public static String userSaveUrl() {
        return System.getProperty("user.home") + File.separator + "Scylla"
                + File.separator + Config.local_save_folder_name
                + File.separator;
    }

    /**
     * Checks if the file has already been done.
     *
     * @param file
     * @return false if the video has not been done and true otherwise
     * @throws IOException
     */
    public static Boolean hasVideoBeenDone(String videoName)
            throws IOException {
        File videopfile = new File(
                FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
        BufferedReader pFileBufferedReader = new BufferedReader(
                new FileReader(videopfile));
        String nextLine;
        while ((nextLine = pFileBufferedReader.readLine()) != null) {
            if (nextLine.contains(videoName)) {
                pFileBufferedReader.close();
                return true;
            }
        }
        pFileBufferedReader.close();
        return false;
    }

    /**
     * Delete a given file or folder. If it is a folder delete all files and
     * folders in it recursively.
     *
     *
     * @param file
     * @return True if it was deleted and false otherwise
     */
    public static boolean deleteFolder(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteFolder(children[i]);
                if (!success) {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * Creates folders on the local machine that are not yet created and then
     * creates the file if needed.
     *
     * @param videoFileUrl
     */
    public static void createFile(File videoFile) {
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
     * Builds a string for the absolute path next to where the program is where
     * the file will be downloaded to.
     *
     * @return the string that was built
     */
    public static String buildLocalVideoUrl() {
        String videoFileUrl = "";
        try {
            videoFileUrl = YOLOBboxController1.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath().toString();
            //get rid of the executable at the end
            videoFileUrl = videoFileUrl.substring(0,
                    videoFileUrl.lastIndexOf(File.separator) + 1);
            //add the name of the folder to put the file in
            videoFileUrl += Config.local_video_folder_name + File.separator;
        } catch (URISyntaxException e) {
            System.err.println("Error getting the location of the program");
            e.printStackTrace();
        }
        return videoFileUrl;
    }

    /**
     * Builds a string for the absolute path to the user's program folder.
     *
     * @return the path of the directory to put the files to be exported
     */
    public static String userProgramUrl() {
        return System.getProperty("user.home") + File.separator + "Scylla"
                + File.separator;
    }

    /**
     * Builds a string for the absolute path in the user's program folder where
     * the file will be downloaded to.
     *
     * @return the path of the directory to put the video in
     */
    public static String userVideoUrl() {
        return System.getProperty("user.home") + File.separator + "Scylla"
                + File.separator + Config.local_video_folder_name
                + File.separator;
    }

    /**
     * Builds a string for the absolute path in the user's program folder where
     * the file will be downloaded to.
     *
     * @return the path of the directory to put the video in
     */
    public static String userOutputUrl() {
        return System.getProperty("user.home") + File.separator + "Scylla"
                + File.separator + Config.local_export_folder_name
                + File.separator;
    }

    /**
     * Builds a string for the absolute path to where the file will be
     * downloaded to.
     *
     * @return the string that was built
     */
    public static String buildLocalExportUrl() {
        String exportFileUrl = "";
        try {
            exportFileUrl = YOLOBboxController1.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath().toString();
            //get rid of the executable at the end
            exportFileUrl = exportFileUrl.substring(0,
                    exportFileUrl.lastIndexOf(File.separator) + 1);
            //add the name of the folder to put the file in
            exportFileUrl += Config.local_export_folder_name + File.separator;
        } catch (URISyntaxException e) {
            System.err.println("Error getting the location of the program");
            e.printStackTrace();
        }
        return exportFileUrl;
    }

}
