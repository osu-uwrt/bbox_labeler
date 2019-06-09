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
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this.model.setFrameJump(this.model.frameRate() / 2);
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
            System.out.println("Trouble Loading Video");
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

    /**
     * Processes export event.
     */
    @Override
    public void processExportEvent() {
        try {
            BoxFolder yoloFolder = BoxFolder.getRootFolder(this.model.api());
            yoloFolder = BoxHelper.getSubFolder(yoloFolder,
                    Config.path_to_yolo);
            this.downloadFiles(yoloFolder);
            int lastIndex = this.getLastIndex();
            List<File> fileList = this.createExportFiles(lastIndex);
            BoxFolder dataClassFolder = BoxHelper.getSubFolder(yoloFolder,
                    Config.path_to_data);
            if (!BoxHelper.fileExists(dataClassFolder,
                    this.model.className())) {
                BoxHelper.createFolder(dataClassFolder, this.model.className());
            }
            dataClassFolder = BoxHelper.getSubFolder(dataClassFolder,
                    this.model.pathToClass());
            this.uploadFiles(fileList, dataClassFolder);
            this.appendPFile(lastIndex + (fileList.size() / 2));
            this.appendVideoPFile(lastIndex + 1,
                    lastIndex + (fileList.size() / 2));
            DateFormat dateFormatForYear = new SimpleDateFormat("yyyy");
            String currentYear = dateFormatForYear.format(new Date());
            String trainingFileName = "train_RS" + currentYear + ".txt";
            String validationFileName = "valid_RS" + currentYear + ".txt";
            File trainingFile = new File(
                    FileHelper.userProgramUrl() + trainingFileName);
            File validationFile = new File(
                    FileHelper.userProgramUrl() + validationFileName);
            File outputValidationFile = new File(
                    FileHelper.userOutputUrl() + validationFileName);
            File outputTrainingFile = new File(
                    FileHelper.userOutputUrl() + trainingFileName);
            File dataPFile = new File(FileHelper.userProgramUrl()
                    + Config.training_data_pfile_name);
            File videoPFile = new File(
                    FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
            File outputDataPFile = new File(FileHelper.userOutputUrl()
                    + Config.training_data_pfile_name);
            File outputVideoPFile = new File(
                    FileHelper.userOutputUrl() + Config.raw_video_pfile_name);
            File indexFile = new File(
                    FileHelper.userProgramUrl() + Config.index_file_name);
            File outputIndexFile = new File(
                    FileHelper.userOutputUrl() + Config.index_file_name);
            File saveFile = new File(
                    FileHelper.userSaveUrl() + this.model.videoName() + ".txt");
            this.updateTrainingFiles(trainingFile, validationFile, fileList);
            this.copyFile(dataPFile, outputDataPFile);
            this.copyFile(videoPFile, outputVideoPFile);
            this.copyFile(trainingFile, outputTrainingFile);
            this.copyFile(validationFile, outputValidationFile);
            this.copyFile(indexFile, outputIndexFile);
            BoxFolder dataFolder = BoxHelper.getSubFolder(yoloFolder,
                    Config.path_to_data);
            BoxFolder videoClassFolder = BoxHelper.getSubFolder(yoloFolder,
                    Config.path_to_videos);
            videoClassFolder = BoxHelper.getSubFolder(videoClassFolder,
                    this.model.pathToClass());
            BoxHelper.reuploadFile(trainingFileName, dataFolder);
            BoxHelper.reuploadFile(validationFileName, dataFolder);
            BoxHelper.reuploadFile(Config.training_data_pfile_name,
                    dataClassFolder);

            BoxHelper.reuploadFile(Config.raw_video_pfile_name,
                    videoClassFolder);
            BoxHelper.reuploadFile(Config.index_file_name, yoloFolder);
            File videoFile = this.model.file();
            Collections.addAll(fileList, trainingFile, outputTrainingFile,
                    validationFile, outputValidationFile, dataPFile,
                    outputDataPFile, videoPFile, outputVideoPFile, videoFile,
                    indexFile, outputIndexFile, saveFile);
            this.deleteFiles(fileList);
            this.view.dispose();
        } catch (IOException e1) {
            System.out.println("Problem exporting");
            e1.printStackTrace();
        }
    }

    /**
     * Creates jpg and txt files in the export folder and returns a list of the
     * files.
     *
     * @param id
     *            the number that is given to the file
     * @param fileLocation
     *            the folder to put the data files in
     * @param className
     *            the name of the class the files are for
     * @param image
     *            the image to be ouput
     * @param yolo
     *            the data to be output
     * @param index
     *            the index for the item being labelled
     * @return the files that were output
     */
    private List<File> outputFrame(int id, String fileLocation,
            String className, BufferedImage image, YOLO yolo, int index) {
        List<File> fileList = new LinkedList<File>();
        fileList.addAll(this.outputImage(id, fileLocation, className, image));
        fileList.addAll(
                this.outputData(id, fileLocation, className, yolo, index));
        return fileList;
    }

    /**
     * Takes the given int and returns a string of the int with leading zeros to
     * make the string 5 characters long.
     *
     * @param i
     *            an integer less than 100,000
     * @return the given integer as a string with leading zeros
     */
    private String formattedInteger(int i) {
        String s = Integer.toString(i);
        while (s.length() < 5) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * Outputs the given image to the given file location formatted with the
     * class name and the given id as a jpg.
     *
     * @param id
     *            the id to give the file name
     * @param fileLocation
     *            the location to output the file to
     * @param className
     *            the name of the class being worked on
     * @param image
     *            the image to be output
     * @return the image file that was output as a list of files
     */
    private List<File> outputImage(int id, String fileLocation,
            String className, BufferedImage image) {
        List<File> fileList = new LinkedList<File>();
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
        fileList.add(imageFile);
        return fileList;

    }

    /**
     * Outputs the given yolo to the given file location formatted with the
     * class name and the given id as a text file.
     *
     * @param id
     *            the id to give the file name
     * @param fileLocation
     *            the location to output the file to
     * @param className
     *            the name of the class being worked on
     * @param yolo
     *            the yolo data to be output
     * @param index
     *            the index of the item being labelled
     * @return the text file that was output as a list of files
     */
    private List<File> outputData(int id, String fileLocation, String className,
            YOLO yolo, int index) {
        List<File> fileList = new LinkedList<File>();
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
            writer.println(index + " " + yolo.x() + " " + yolo.y() + " "
                    + yolo.width() + " " + yolo.height());
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            System.err.println("Problem exporting to text file");
        }
        fileList.add(dataFile);
        return fileList;

    }

    /**
     * Deletes all files in the fileList.
     *
     * @param fileList
     *            the list of files to be deleted
     */
    private void deleteFiles(List<File> fileList) {
        int response = JOptionPane.showConfirmDialog(null,
                "Export Successful! Delete Local Files?");
        if (response == JOptionPane.YES_OPTION) {
            //delete export folder
            for (File file : fileList) {
                file.delete();
            }
        }
    }

    /**
     * Adds each of the jpg files in the file list to the training and
     * validation files. At random, 15% are added to the validation file and the
     * rest are added to the training file.
     *
     * @param trainingFile
     * @param validationFile
     * @param fileList
     * @throws IOException
     */
    private void updateTrainingFiles(File trainingFile, File validationFile,
            List<File> fileList) throws IOException {
        Writer trainingFileWriter = new FileWriter(trainingFile, true);
        Writer validationFileWriter = new FileWriter(validationFile, true);
        for (File file : fileList) {
            //if the file is a jpg
            if (file.getName().indexOf(".jpg") >= 0) {
                double rand = Math.random();
                if (rand <= 0.15) {
                    //put it in the validation file
                    validationFileWriter.append(Config.training_file_data_prefix
                            + this.model.className() + "/" + file.getName()
                            + System.lineSeparator());
                } else {
                    //put it in the training file
                    trainingFileWriter.append(Config.training_file_data_prefix
                            + this.model.className() + "/" + file.getName()
                            + System.lineSeparator());
                }
            }
        }
        trainingFileWriter.close();
        validationFileWriter.close();
    }

    /**
     * Downloads the video pfile, data pfile, class index file, training file,
     * and validation files. If any of them don't exist on box create a new
     * local version.
     *
     * @param yoloFolder
     * @throws IOException
     */
    private void downloadFiles(BoxFolder yoloFolder) throws IOException {
        //download data pfile
        BoxFolder dataClassFolder = BoxHelper.getSubFolder(yoloFolder,
                Config.path_to_data);
        String[] classPath = this.model.pathToClass();
        dataClassFolder = BoxHelper.getSubFolder(dataClassFolder, classPath);
        try {
            if (!BoxHelper.DownloadFile(this.model.api(), dataClassFolder,
                    Config.training_data_pfile_name,
                    FileHelper.userProgramUrl())) {
                System.out.println(
                        "Data pfile could not be downloaded. Creating new file.");
                File file = new File(FileHelper.userProgramUrl()
                        + Config.training_data_pfile_name);
                while (!file.createNewFile()) {
                    file.delete();
                }
            }
        } catch (InvocationTargetException | IOException
                | InterruptedException e) {
            System.out.println(
                    "Data pfile could not be downloaded. Creating new file.");
            File file = new File(FileHelper.userProgramUrl()
                    + Config.training_data_pfile_name);
            while (!file.createNewFile()) {
                file.delete();
            }
        }

        //download video pfile
        BoxFolder videoClassFolder = BoxHelper.getSubFolder(yoloFolder,
                Config.path_to_videos);
        videoClassFolder = BoxHelper.getSubFolder(videoClassFolder, classPath);
        try {
            if (!BoxHelper.DownloadFile(this.model.api(), videoClassFolder,
                    Config.raw_video_pfile_name, FileHelper.userProgramUrl())) {
                System.out.println(
                        "Video pfile could not be downloaded. Creating new file.");
                File file = new File(FileHelper.userProgramUrl()
                        + Config.raw_video_pfile_name);
                while (!file.createNewFile()) {
                    file.delete();
                }
            }
        } catch (InvocationTargetException | InterruptedException e) {
            System.out.println(
                    "Video pfile could not be downloaded. Creating new file.");
            File file = new File(
                    FileHelper.userProgramUrl() + Config.raw_video_pfile_name);
            while (!file.createNewFile()) {
                file.delete();
            }
        }

        //download training file
        DateFormat dateFormatForYear = new SimpleDateFormat("yyyy");
        String currentYear = dateFormatForYear.format(new Date());
        String trainingFileName = "train_RS" + currentYear + ".txt";
        BoxFolder dataFolder = BoxHelper.getSubFolder(yoloFolder,
                Config.path_to_data);
        try {
            if (!BoxHelper.DownloadFile(this.model.api(), dataFolder,
                    trainingFileName, FileHelper.userProgramUrl())) {
                System.out.println(
                        "training file could not be downloaded. Creating new file.");
                File file = new File(
                        FileHelper.userProgramUrl() + trainingFileName);
                while (!file.createNewFile()) {
                    file.delete();
                }
            }
        } catch (InvocationTargetException | InterruptedException e) {
            System.out.println(
                    "training file could not be downloaded. Creating new file.");
            File file = new File(
                    FileHelper.userProgramUrl() + trainingFileName);
            while (!file.createNewFile()) {
                file.delete();
            }
        }

        //download validation file
        String validationFileName = "valid_RS" + currentYear + ".txt";
        try {
            if (!BoxHelper.DownloadFile(this.model.api(), dataFolder,
                    validationFileName, FileHelper.userProgramUrl())) {
                System.out.println(
                        "validation file could not be downloaded. Creating new file.");
                File file = new File(
                        FileHelper.userProgramUrl() + validationFileName);
                while (!file.createNewFile()) {
                    file.delete();
                }
            }
        } catch (InvocationTargetException | InterruptedException e) {
            System.out.println(
                    "validation file could not be downloaded. Creating new file.");
            File file = new File(
                    FileHelper.userProgramUrl() + validationFileName);
            while (!file.createNewFile()) {
                file.delete();
            }
        }

        //download class index file
        String classIndexFileName = Config.index_file_name;
        try {
            BoxHelper.DownloadFile(this.model.api(), yoloFolder,
                    classIndexFileName, FileHelper.userProgramUrl());
        } catch (InvocationTargetException | InterruptedException e) {
            System.out.println(
                    "validation file could not be downloaded. Creating new file.");
            File file = new File(
                    FileHelper.userProgramUrl() + classIndexFileName);
            while (!file.createNewFile()) {
                file.delete();
            }
        }
    }

    /**
     * Adds a log of the new data to the pfile for the videos
     *
     * @param startIndex
     *            the lowest index of the files added in this session
     * @param lastIndex
     *            the highest index of the files added in this session
     * @throws IOException
     */
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
                .append(this.model.file().getName() + ", " + date + ", "
                        + BoxUser.getCurrentUser(this.model.api()).getInfo()
                                .getName()
                        + ", " + startIndex + ", " + lastIndex);
        pFileBufferedWriter.close();
    }

    /**
     * Gets the index for the class from the index file. If it can't find one,
     * it asks the user and adds it to the file.
     *
     * @return
     */
    private int getIndex() {
        int index = -1;
        File indexFile = new File(
                FileHelper.userProgramUrl() + Config.index_file_name);
        try {
            BufferedReader indexFileReader = new BufferedReader(
                    new FileReader(indexFile));
            String line;
            Boolean classFound = false;
            while (!classFound && (line = indexFileReader.readLine()) != null) {
                if (line.startsWith(this.model.className())) {
                    line = line.substring(this.model.className().length());
                    String regex = "([0-9]+)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        index = Integer.parseInt(matcher.group());
                        classFound = true;
                    }
                }
            }
            indexFileReader.close();
            if (index == -1) {
                index = this.askForIndex();
            }
        } catch (IOException e) {
            index = this.askForIndex();
        }
        return index;
    }

    /**
     * Creates a pop-up window asking the user for an integer to use as the
     * index for the class. Keeps asking until a valid integer is given or
     * nothing is given. If a valid integer is given, it writes a line on the
     * index file for the class and then returns the index. If nothing is given
     * it returns -1.
     *
     * @return the integer given or -1 if nothing is given
     */
    private int askForIndex() {
        int index = -1;
        String input = "";
        while (input == "") {
            input = JOptionPane.showInputDialog("Could not find an index for "
                    + this.model.className()
                    + ". Please enter an integer for this class or leave it"
                    + " empty to stop exporting.");
            if (input != null && input.length() > 0) {
                String regex = "([0-9]+)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(input);
                if (matcher.find()) {
                    index = Integer.parseInt(matcher.group());
                }
                if (index > 0) {
                    File indexFile = new File(FileHelper.userProgramUrl()
                            + Config.index_file_name);
                    try {
                        FileWriter writer = new FileWriter(indexFile);
                        writer.append(this.model.className() + ": " + index
                                + System.lineSeparator());
                        writer.close();
                    } catch (IOException e) {
                        System.err.println("Index file could not be written.");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(
                            "Input: " + input + " is not valid. Asking again.");
                    input = "";
                }
            }
        }
        return index;
    }

    /**
     * Creates the text and data files and puts them in the export folder.
     *
     * @param lastIndex
     * @return the list of files that were created
     */
    private List<File> createExportFiles(int lastIndex) {
        String className = this.model.className();
        int max = this.model.totalFrames() - 1;
        List<YOLO> yolo = this.model.yolo();
        List<File> exportedFiles = new LinkedList<File>();
        String outputDirectory = FileHelper.userOutputUrl();
        int index;
        if ((index = this.getIndex()) > 0) {
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
                        BufferedImage image = j
                                .convert(frameGrabber.grabImage());
                        int id = lastIndex + 1;
                        lastIndex++;
                        exportedFiles
                                .addAll(this.outputFrame(id, outputDirectory,
                                        className, image, next, index));
                    }
                    i++;
                }
                frameGrabber.close();
                System.out.println(
                        "Output Files created in : " + outputDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return exportedFiles;
    }

    /**
     * Copies the source file to the destination file. If the destination file
     * already exists, it is overwritten.
     *
     * @param source
     *            the file to be copied
     * @param destination
     *            the file to copy to
     * @throws IOException
     */
    private void copyFile(File source, File destination) throws IOException {
        Path sourcePath = source.toPath();
        Path destinationPath = destination.toPath();
        Files.copy(sourcePath, destinationPath,
                StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Adds a log of the last index of the files that were added this session to
     * the pfile.
     *
     * @param lastIndex
     *            the last index of the files that were added this session
     * @throws IOException
     */
    private void appendPFile(int lastIndex) throws IOException {
        //Append the pfile
        BufferedWriter pFileBufferedWriter = new BufferedWriter(new FileWriter(
                FileHelper.userProgramUrl() + Config.training_data_pfile_name,
                true));
        pFileBufferedWriter.newLine();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String date = df.format(new Date());
        pFileBufferedWriter.append(date + ", " + lastIndex);
        pFileBufferedWriter.close();
    }

    /**
     * Parses the pfile and finds the last index for files added to Box.
     *
     * @return the last index for files added to Box
     */
    private int getLastIndex() {
        int lastIndex = 0;
        //parse the file for the line that starts with the name of the video file
        File pFile = new File(
                FileHelper.userProgramUrl() + Config.training_data_pfile_name);
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
            if (previousLine.length() > 3) {
                previousLine = previousLine
                        .substring(previousLine.indexOf(',') + 2);
                //get the last index value from that line
                lastIndex = Integer.parseInt(previousLine);
            }
        } catch (IOException e) {
            System.err.println("pfile.txt could not be found on local machine");
            e.printStackTrace();
        }
        return lastIndex;
    }

    /**
     * Uploads each file in the file list to box in the BoxFolder given.
     *
     * @param fileList
     *            the list of files to be uploaded
     * @param folder
     *            the folder the files will be added to
     */
    private void uploadFiles(List<File> fileList, BoxFolder folder) {
        for (File file : fileList) {
            BoxHelper.uploadFile(file.getName(), folder);
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
            BufferedImage bi = j.convert(f);
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
            BufferedImage bi = j.convert(f);
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
        saveFile.getParentFile().mkdirs();
        List<BBox> bbox = this.model.bbox();
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(saveFile));
            writer.write(this.model.className() + System.lineSeparator());
            for (int i = 0; i < bbox.size(); i++) {
                writer.write(i + " " + bbox.get(i).toString()
                        + System.lineSeparator());

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
        while (i < bbox.size() && !(bbox.get(i).isSet())) {
            i++;
        }
        //if one was found
        if (i != bbox.size()) {
            BBox first = bbox.get(i);
            int firstIndex = i;
            //get the last frame with a bounding box
            i = bbox.size() - 1;
            while (i > firstIndex && !(bbox.get(i).isSet())) {
                i--;
            }
            //if one other than the first one was found
            if (i != firstIndex) {
                int lastIndex = i;
                int nextIndex;
                do {

                    //get the next frame after the first with a bounding box
                    i = firstIndex + 1;
                    while (i < bbox.size() && !(bbox.get(i).isSet())) {
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

    @Override
    public void incrmentFrameJump() {
        this.model.setFrameJump(this.model.frameJump() + 1);
        this.updateViewToMatchModel(this.model, this.view);
    }

    @Override
    public void decrementFrameJump() {
        this.model.setFrameJump(this.model.frameJump() + 1);
        if (this.model.frameJump() < 1) {
            this.model.setFrameJump(1);
        }
        this.updateViewToMatchModel(this.model, this.view);
    }

}
