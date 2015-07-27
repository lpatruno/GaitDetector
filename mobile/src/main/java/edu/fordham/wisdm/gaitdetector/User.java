package edu.fordham.wisdm.gaitdetector;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class that holds individual user information.
 */
public class User implements Parcelable {
    private String name;
    private String gender;
    private String height;
    private String specialConditions;
    private String handedness;

    // Constructor
    public User() {
        name = "";
        gender = "";
        height = "";
        specialConditions = "";
        handedness = "";
    }

    public User(String name) {
        this.name = name;
        gender = "";
        height = "";
        specialConditions = "";
        handedness = "";
    }

    // Methods
    public void setAttributes(String gender, String height,
                               String specialConditions, String handedness) {
        this.gender = gender;
        this.height = height;
        this.specialConditions = specialConditions;
        this.handedness = handedness;
    }

    public String getName() {
        return this.name;
    }

    public String getGender() {
        return this.gender;
    }

    public String getHeight() {
        return this.height;
    }

    public String getSpecialConditions() {
        return this.specialConditions;
    }

    public String getHandedness() {
        return this.handedness;
    }

    // For implementing Parcelable
    public int describeContents() {
        return 0;
    }

    // Write user data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(gender);
        out.writeString(height);
        out.writeString(specialConditions);
        out.writeString(handedness);
    }

    // Method used to regenerate an object once passed to an activity
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    // Constructor that takes a Parcel and recreates the User object
    private User(Parcel in) {
        name = in.readString();
        gender = in.readString();
        height = in.readString();
        specialConditions = in.readString();
        handedness = in.readString();
    }
}
