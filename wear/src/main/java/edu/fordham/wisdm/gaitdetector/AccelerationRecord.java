package edu.fordham.wisdm.gaitdetector;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class to hold acceleration records
 *
 * @author Luigi Patruno
 */
public class AccelerationRecord implements Serializable {

    /**
     * The timestamp of when the record
     * @serial
     */
    private long ts;

    /**
     * The acceleration in the x-axis
     * @serial
     */
    private float x;

    /**
     * The acceleration in the y-axis
     * @serial
     */
    private float y;
    /**
     * The acceleration in the z-axis
     * @serial
     */
    private float z;

    /**
     * Automatically generated serial number for ensuring that a object of this type can be safely
     * deserialized.
     */
    private static final long serialVersionUID = 2345673456543874764L;

    /**
     * Used for serialization of the class
     * @param outputStream the output stream to write to
     */
    private void writeObject(ObjectOutputStream outputStream) {
        try {
            outputStream.defaultWriteObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Constructor to initialize acceleration records.
     *
     * @param ts
     * @param x
     * @param y
     * @param z
     */
    public AccelerationRecord(long ts, float x, float y, float z){
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
