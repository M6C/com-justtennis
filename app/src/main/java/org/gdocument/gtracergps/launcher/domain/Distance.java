package org.gdocument.gtracergps.launcher.domain;

import java.util.Random;

import org.gdocument.gtracergps.launcher.log.Logger;
import org.gdocument.gtracergps.launcher.util.GpsUtil;

import android.os.Parcel;
import android.os.Parcelable;

public class Distance implements Parcelable {
	private static final String TAG = "Distance";

	private long id;
	private Localisation localisation1;
	private Localisation localisation2;

	private double distance;

	public Distance(Localisation localisation1, Localisation localisation2) {
		super();
		generateId();
		this.localisation1 = localisation1;
		this.localisation2 = localisation2;
		
		double lat1 =  localisation1.getLatitude();
		double lon1 =  localisation1.getLongitude();
		double lat2 =  localisation2.getLatitude();
		double lon2 =  localisation2.getLongitude();
		double alt1 =  localisation1.getAltitude();
		double alt2 =  localisation2.getAltitude();

		this.distance = GpsUtil.calDistanceKM2(lat1, lon1, lat2, lon2, alt1, alt2);
	}

	public Distance(Parcel in) {
		generateId();
		readFromParcel(in);
	}

	private void generateId() {
		this.id = new Random().nextLong();
		if (this.id<0)
			this.id = -this.id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Distance [id: " + id + " localisation1=" + localisation1 + ", localisation2=" + localisation2 + ", distance=" + distance + "]";
	}

	/**
	 * @return the localisation1
	 */
	public Localisation getLocalisation1() {
		return localisation1;
	}

	/**
	 * @param localisation1 the localisation1 to set
	 */
	public void setLocalisation1(Localisation localisation1) {
		this.localisation1 = localisation1;
	}

	/**
	 * @return the localisation2
	 */
	public Localisation getLocalisation2() {
		return localisation2;
	}

	/**
	 * @param localisation2 the localisation2 to set
	 */
	public void setLocalisation2(Localisation localisation2) {
		this.localisation2 = localisation2;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, "id: " + id + "|" + msg);
    }

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		String msg = "writeToParcel START " + toString();
		logMe(msg);

		dest.writeParcelable(localisation1, flags);
		dest.writeParcelable(localisation2, flags);
		dest.writeDouble(distance);
		
		msg = "writeToParcel END";
		logMe(msg);
	}

	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in) {
		String msg = "readFromParcel START " + toString();
		try {
			// We just need to read back each
			// field in the order that it was
			// written to the parcel
			ClassLoader loader = this.getClass().getClassLoader();
			localisation1 = in.readParcelable(loader);
			localisation2 = in.readParcelable(loader);
			distance = in.readDouble();
	
			msg += " END";
		}
		finally {
			logMe(msg);
		}
	}

    /**
     *
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays.
     *
     * This also means that you can use use the default
     * constructor to create the object and use another
     * method to hyrdate it as necessary.
     *
     * I just find it easier to use the constructor.
     * It makes sense for the way my brain thinks ;-)
     *
     */
    public static final Parcelable.Creator<Distance> CREATOR = new Parcelable.Creator<Distance>() {
    	private static final String TAG = "Parcelable.Creator<Distance>";

        public Distance createFromParcel(Parcel in) {
        	Distance ret = null;
    		String msg = "readFromParcel";
    		try {
    			ret = new Distance(in);
    		}
    		catch(Exception ex) {
    			msg += " Exception:" + ex.getMessage();
    		}
    		finally {
    			msg += " Distance:" + ret + " Parcel:" + in;
    			logMe(msg);
    		}
            return ret;
        }

        public Distance[] newArray(int size) {
    		String msg = "newArray size:"+size;
    		logMe(msg);
            return new Distance[size];
        }

    	private void logMe(String msg) {
    		Logger.logMe(TAG, msg);
        }
    };
}