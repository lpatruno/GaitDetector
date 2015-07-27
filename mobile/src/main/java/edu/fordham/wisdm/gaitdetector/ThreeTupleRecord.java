package edu.fordham.wisdm.gaitdetector;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Data structure to hold 3-tuple time series data. An extra field for the timestamp is an
 * additional parameter. This implements the Serializable interface for data streaming from
 * the Android Wear device to the phone.
 *
 * Note: Although not yet necessary, a base NTupleRecord could be created and this can be a
 * subclass.
 *
 * @author Luigi Patruno
 */
public class ThreeTupleRecord implements Serializable {

    /**
     * Timestamp of the record
     * @serial
     */
    private long ts;

    /**
     * x-axis sensor recording
     * @serial
     */
    private float x;

    /**
     * y-axis sensor recording
     * @serial
     */
    private float y;
    /**
     * z-axis sensor recording
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
    public ThreeTupleRecord(long ts, float x, float y, float z){
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
