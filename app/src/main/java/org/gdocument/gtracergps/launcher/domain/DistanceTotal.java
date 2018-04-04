package org.gdocument.gtracergps.launcher.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.gdocument.gtracergps.launcher.log.Logger;
import org.gdocument.gtracergps.launcher.util.NumberUtil;

import android.os.Parcel;
import android.os.Parcelable;

public class DistanceTotal implements Parcelable {
	private static final String TAG = "DistanceTotal";

	private long id;
	private Localisation localisationStart;
	private Localisation localisationFinish;

	private List<Distance> listDistance = new ArrayList<Distance>();

	private double distanceTotal;

	public DistanceTotal() {
		generateId();
	}

	public DistanceTotal(Parcel in) {
		generateId();
		readFromParcel(in);
	}

	private void generateId() {
		this.id = new Random().nextLong();
		if (this.id<0)
			this.id = -this.id;
	}

	public void addDisctance(Distance distance) {
		//TODO Ne pas reinitialiser la liste
		//     Comportement pour contourner un pb de sauvegarde des Distance
		//     Mettre en place une base de donnee SQLite pour sauvegarder les Distance
		listDistance = new ArrayList<Distance>();
		listDistance.add(distance);
		
		distanceTotal+=distance.getDistance();
	}

	public long getElapsedTime() {
//		return getLocalisationFinish().getTime()-getLocalisationStart().getTime();
		return elapsedTime;
	}

	public String getElapsedTimeFormated() {
		long time = getElapsedTime();

		String ret = "";
		int iCheck = 3600000;
		if (time>=iCheck) {
			long h = (time - (time % iCheck)) / iCheck;
			time -= h * iCheck;
			ret += Long.toString(h) + "h";
		}
		iCheck = 60000;
		if (time>=iCheck) {
			long m = (time - (time % iCheck)) / iCheck;
			time -= m * iCheck;
			ret += ((!ret.isEmpty()) ? ":" : "") + Long.toString(m) + "m";
		}
		iCheck = 1000;
		if (time>=iCheck) {
			long s = (time - (time % iCheck)) / iCheck;
			time -= s * iCheck;
			ret += ((!ret.isEmpty()) ? ":" : "") + Long.toString(s) + "s";
		}
		
		ret += ((!ret.isEmpty()) ? ":" : "") + Long.toString(time) + "ms";
		
		return ret;
	}

	public double getSpeedAverage() {
		return speedAverage;
	}

	public double getSpeedAverageRounded() {
		return NumberUtil.formatDouble(speedAverage);
	}

	public String toReportHtml() {
		double distTotal = getDistanceTotalRounded();
		double speedAverage = getSpeedAverageRounded();
		String szTime = getElapsedTimeFormated();

		return "<table width='100%'>"
				+ "<tr><td colspan=2><font size='3'>Summary</font></td></tr>"
				+ "<tr><td width='100pt'>Elapsed Time</td><td width='*'>" + szTime + "</td></tr>"
				+ "<tr><td width='100pt'>Total Distance</td><td width='*'>" + Double.toString(distTotal) + "</td></tr>"
				+ "<tr><td>Speed Average (Km/H)</td><td>" + Double.toString(speedAverage) + "</td></tr>"
				+ "</table>";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DistanceTotal [id: " + id
				+ " Elapsed Time: " + getElapsedTimeFormated() + " Speed Average: " + getSpeedAverageRounded() 
				+ " Total Distance: " + getDistanceTotalRounded() + "]";
	}

	/**
	 * @return the localisationStart
	 */
	public Localisation getLocalisationStart() {
		return localisationStart;
	}

	/**
	 * @param localisation the localisationStart to set
	 */
	public void setLocalisationStart(Localisation localisation) {
		this.localisationStart = localisation;
	}

	/**
	 * @return the localisationFinish
	 */
	public Localisation getLocalisationFinish() {
		return localisationFinish;
	}

	/**
	 * @param localisation the localisationFinish to set
	 */
	public void setLocalisationFinish(Localisation localisation) {
		this.localisationFinish = localisation;
		
		elapsedTime = localisationFinish.getTime()-localisationStart.getTime();
		speedAverage = (distanceTotal==0 || elapsedTime==0) ? 0 : ((distanceTotal / elapsedTime) * 3600000);
	}

	/**
	 * @return the listDistance
	 */
	public List<Distance> getListDistance() {
		return listDistance;
	}

	/**
	 * @param listDistance the listDistance to set
	 */
	public void setListDistance(List<Distance> listDistance) {
		this.listDistance = listDistance;
	}

	/**
	 * @return the distanceTotal
	 */
	public double getDistanceTotal() {
		return distanceTotal;
	}

	/**
	 * @param distanceTotal the distanceTotal to set
	 */
	public void setDistanceTotal(double distanceTotal) {
		this.distanceTotal = distanceTotal;
	}

	/**
	 * @return the distanceTotal
	 */
	public double getDistanceTotalRounded() {
		return NumberUtil.formatDouble(distanceTotal);
	}

	private void logMe(String msg) {
		Logger.logMe(TAG, "id: " + id + "|" + msg);
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		String msg = "writeToParcel START " + toString();
		logMe(msg);

		dest.writeParcelable(localisationStart, flags);
		dest.writeParcelable(localisationFinish, flags);
		dest.writeDouble(distanceTotal);
		dest.writeDouble(speedAverage);
		dest.writeLong(elapsedTime);
		
		dest.writeList(listDistance);
		
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
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		String msg = "readFromParcel START " + toString();
		try {
			// We just need to read back each
			// field in the order that it was
			// written to the parcel
			ClassLoader loader = this.getClass().getClassLoader();
			localisationStart = in.readParcelable(loader);
			localisationFinish = in.readParcelable(loader);
			distanceTotal = in.readDouble();
			speedAverage = in.readDouble();
			elapsedTime = in.readLong();
			
			listDistance = in.readArrayList(loader);
	
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
    public static final Parcelable.Creator<DistanceTotal> CREATOR = new Parcelable.Creator<DistanceTotal>() {
    	private static final String TAG = "Parcelable.Creator<DistanceTotal>";

        public DistanceTotal createFromParcel(Parcel in) {
        	DistanceTotal ret = null;
    		String msg = "readFromParcel";
    		try {
    			ret = new DistanceTotal(in);
    		}
    		catch(Exception ex) {
    			msg += " Exception:" + ex.getMessage();
    		}
    		finally {
    			msg += " DistanceTotal:" + ret + " Parcel:" + in;
    			logMe(msg);
    		}
            return ret;
        }

        public DistanceTotal[] newArray(int size) {
    		String msg = "newArray size:"+size;
    		logMe(msg);
            return new DistanceTotal[size];
        }

    	private void logMe(String msg) {
    		Logger.logMe(TAG, msg);
        }
    };

	private long elapsedTime;

	private double speedAverage;
}