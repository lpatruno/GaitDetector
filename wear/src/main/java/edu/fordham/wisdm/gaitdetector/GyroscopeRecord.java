package edu.fordham.wisdm.gaitdetector;

/**
 * Class to hold gyroscope records
 *
 * @author Luigi Patruno
 */
public class GyroscopeRecord {

    /**
     * The timestamp of when the record
     * @serial
     */
    private long ts;

    /**
     * The angular velocity in the x-axis
     * @serial
     */
    private float x;

    /**
     * The angular velocity in the y-axis
     * @serial
     */
    private float y;
    /**
     * The angular velocity in the z-axis
     * @serial
     */
    private float z;


    /**
     * Constructor to initialize acceleration records.
     *
     * @param ts
     * @param x
     * @param y
     * @param z
     */
    public GyroscopeRecord(long ts, float x, float y, float z){
        this.ts = ts;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return ts + "," + x + "," + y + "," + z;
    }
}
