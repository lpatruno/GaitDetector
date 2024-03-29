package edu.fordham.wisdm.gaitdetector;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Class to save sensor data to the phone filesystem
 *
 * @author Luigi Patruno
 */
public class FileSaver {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "FileSaver";

    /**
     * Username for file labeling purposes
     */
    private final String USERNAME;

    /**
     * Reference to the GaitDetector/<username> root directory
     */
    private final File rootDirectory;

    /**
     * Label for phone accelerometer data files
     */
    protected static final String PHONE_ACCEL = "_phone_accel.txt";

    /**
     * Label for phone gyroscope data files
     */
    protected static final String PHONE_GYRO = "_phone_gyro.txt";

    /**
     * Label for watch accelerometer data files
     */
    protected static final String WATCH_ACCEL = "_watch_accel.txt";

    /**
     * Label for watch gyroscope data files
     */
    protected static final String WATCH_GYRO = "_watch_gyro.txt";

    /**
     * Constructor for FileSaver class
     */
    public FileSaver(String username) {
        USERNAME = username;

        final String rootPath = Environment.getExternalStorageDirectory() + File.separator +
                "GaitDetector" + File.separator + USERNAME;

        rootDirectory = createRootDirectory(rootPath);
    }

    /**
     * Method to create the GaitDetector/<username>/ directory
     */
    private File createRootDirectory(String rootPath) {

        File rootDirectory = new File(rootPath);

        // Create root directory if does not exist
        if(!rootDirectory.isDirectory()) {
            rootDirectory.mkdirs();
        }

        return rootDirectory;
    }

    /**
     * Create a subdirectory for a particular BBS task within a user's file directory
     * i.e. GaitDetector/luigi_patruno/BBS10/
     *
     * @param task
     */
    private File createSubdirectory(String task) {

        final String subdirectoryPath = rootDirectory.getPath() + File.separator + task;

        File subdirectory = new File(subdirectoryPath);

        // Create sub directory if does not exist
        if(!subdirectory.isDirectory()) {
            subdirectory.mkdirs();
        }

        return subdirectory;
    }

    /**
     * Write a List of records to a file on the file system.
     *
     * @param task
     * @param deviceSensor
     * @param dataRecords
     */
    public void writeFile(String task, String deviceSensor,
                           ArrayList<ThreeTupleRecord> dataRecords) {

        Log.d(TAG, USERNAME + task);

        File subdirectory = createSubdirectory(task);

        final String recordFilePath = subdirectory.getPath() + File.separator + USERNAME +
                "_" + task + deviceSensor;

        File recordFile = new File(recordFilePath);

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(recordFile);

            for (ThreeTupleRecord record : dataRecords) {
                writer.println(record.toString());
            }

            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
