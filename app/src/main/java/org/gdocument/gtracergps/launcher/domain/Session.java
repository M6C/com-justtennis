package org.gdocument.gtracergps.launcher.domain;

import java.util.Date;
import java.util.Random;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.os.Parcel;
import android.os.Parcelable;

public class Session implements Parcelable {
	private static final String TAG = Session.class.getCanonicalName();

	private long idGenereted;
	private long id;
	private long idSend;
	private Localisation start;
	private Localisation end;
	private Date timeStart;
	private Date timeStop;
	private Date timeSend;
	private byte[] pngMapScreenShoot;

	private double calculateDistance;
	private long calculateElapsedTime;

	/**
	 */
	public Session() {
		super();
		generateId();
	}

	/**
	 * @param id
	 */
	public Session(long id) {
		super();
		this.id = id;
		generateId();
	}

	/**
	 * 
	 * @param parcel
	 */
	public Session(Parcel parcel) {
		readFromParcel(parcel);
	}

	private void generateId() {
		this.idGenereted = new Random().nextLong();
		if (this.idGenereted<0)
			this.idGenereted = -this.idGenereted;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		String msg = "writeToParcel START id:" + id + " timeStart:" + timeStart;// + " idGenereted:" + this.idGenereted + " flags:" + flags;
		try {
//			if (flags==Parcelable.PARCELABLE_WRITE_RETURN_VALUE) {
//				msg += " writeLong idGenereted";
				dest.writeLong(idGenereted);
//				msg += " writeLong id";
				dest.writeLong(id);
//				msg += " writeLong idSend";
				dest.writeLong(idSend);
//				msg += " writeLong start.id";
//				dest.writeLong(start==null ? 0 : start.getId());
				dest.writeParcelable(start, flags);
//				msg += " writeLong end.id";
//				dest.writeLong(end==null ? 0 : end.getId());
				dest.writeParcelable(end, flags);
//				msg += " writeLong timeStart";
				dest.writeLong(timeStart==null ? 0 : timeStart.getTime());
//				msg += " writeLong timeStop";
				dest.writeLong(timeStop==null ? 0 : timeStop.getTime());
//				msg += " writeLong timeSend";
				dest.writeLong(timeSend==null ? 0 : timeSend.getTime());
//				msg += " writeDouble calculateDistance";
				dest.writeDouble(calculateDistance);
//				msg += " writeLong calculateElapsedTime";
				dest.writeLong(calculateElapsedTime);
//			}
//			else {
//				msg += " no write";
//			}
			msg += " - END";
		}
		catch (Exception ex) {
			msg += " Exception Message:" + ex.getMessage();
		}
		finally {
			logMe(msg);
		}
	}

	/**
	 *
	 * Called from the constructor to create this
	 * object from a parcel.
	 *
	 * @param in parcel from which to re-create object
	 */
	private void readFromParcel(Parcel in) {
		String msg = "readFromParcel START";// + toString();
		try {
			// We just need to read back each
			// field in the order that it was
			// written to the parcel
			idGenereted = in.readLong();
			id = in.readLong();
			idSend = in.readLong();
//			long idStart = in.readLong();
//			start = (idStart==0) ? null : new Localisation(idStart);
			start = in.readParcelable(Session.class.getClassLoader());
//			long idEnd = in.readLong();
//			end = (idEnd==0) ? null : new Localisation(idEnd);
			end = in.readParcelable(Session.class.getClassLoader());
			long lTimeStart = in.readLong();
			timeStart = (lTimeStart==0) ? null : new Date(lTimeStart);
			long lTimeEnd = in.readLong();
			timeStop = (lTimeEnd==0) ? null : new Date(lTimeEnd);
			long lTimeSend = in.readLong();
			timeSend = (lTimeSend==0) ? null : new Date(lTimeSend);
			calculateDistance = in.readDouble();
			calculateElapsedTime = in.readLong();
			
			msg += " id:" + id + " timeStart:" + timeStart;//" idGenereted:" + this.idGenereted;
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
    public static final Parcelable.Creator<Session> CREATOR = new Parcelable.Creator<Session>() {
    	private final String TAG = Session.TAG + " Parcelable.Creator<Session>";

        public Session createFromParcel(Parcel in) {
        	Session ret = null;
    		String msg = "readFromParcel";
    		try {
    			ret = new Session(in);
    		}
    		catch(Exception ex) {
    			msg += " Exception:" + ex.getMessage();
    		}
    		finally {
    			msg += " Session:" + ret + " Parcel:" + in;
    			logMe(msg);
    		}
            return ret;
        }

        public Session[] newArray(int size) {
    		String msg = "newArray size:"+size;
    		logMe(msg);
            return new Session[size];
        }

    	private void logMe(String msg) {
    		Logger.logMe(TAG, msg);
        }
    };

	private void logMe(String msg) {
		Logger.logMe(TAG, "id: " + idGenereted + "|" + msg);
    }

	@Override
	public String toString() {
		return "Session [idGenereted=" + idGenereted + ", id=" + id
//				+ ", idSend=" + idSend + ", start=" + start + ", end=" + end + ", timeStart=" + timeStart
//				+ ", timeStop=" + timeStop + ", timeSend=" + timeSend + ", pngMapScreenShoot=" + Arrays.toString(pngMapScreenShoot) + ", calculateDistance="
//				+ calculateDistance + ", calculateElapsedTime=" + calculateElapsedTime
				+ "]";
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the idSend
	 */
	public long getIdSend() {
		return idSend;
	}

	/**
	 * @param idSend the idSend to set
	 */
	public void setIdSend(long idSend) {
		this.idSend = idSend;
	}

	/**
	 * @return the start
	 */
	public Localisation getStart() {
		return start;
	}
	/**
	 * @param start the start to set
	 */
	public void setStart(Localisation localisation) {
		this.start = localisation;
	}
	/**
	 * @return the end
	 */
	public Localisation getEnd() {
		return end;
	}
	/**
	 * @param end the end to set
	 */
	public void setEnd(Localisation localisation) {
		this.end = localisation;
	}
	/**
	 * @return the date
	 */
	public Date getTimeStart() {
		return timeStart;
	}
	/**
	 * @param date the date to set
	 */
	public void setTimeStart(Date date) {
		this.timeStart = date;
	}

	/**
	 * @return the timeSend
	 */
	public Date getTimeSend() {
		return timeSend;
	}

	/**
	 * @param timeSend the timeSend to set
	 */
	public void setTimeSend(Date timeSend) {
		this.timeSend = timeSend;
	}

	/**
	 * @return the timeStop
	 */
	public Date getTimeStop() {
		return timeStop;
	}

	/**
	 * @param timeStop the timeStop to set
	 */
	public void setTimeStop(Date timeStop) {
		this.timeStop = timeStop;
	}

	/**
	 * @return the calculateDistance
	 */
	public double getCalculateDistance() {
		return calculateDistance;
	}

	/**
	 * @param calculateDistance the calculateDistance to set
	 */
	public void setCalculateDistance(double calculateDistance) {
		this.calculateDistance = calculateDistance;
	}

	/**
	 * @return the calculateElapsedTime
	 */
	public long getCalculateElapsedTime() {
		return calculateElapsedTime;
	}

	/**
	 * @param calculateElapsedTime the calculateElapsedTime to set
	 */
	public void setCalculateElapsedTime(long calculateElapsedTime) {
		this.calculateElapsedTime = calculateElapsedTime;
	}

	/**
	 * @return the pngMapScreenShoot
	 */
	public byte[] getPngMapScreenShoot() {
		return pngMapScreenShoot;
	}

	/**
	 * @param pngMapScreenShoot the pngMapScreenShoot to set
	 */
	public void setPngMapScreenShoot(byte[] pngMapScreenShoot) {
		this.pngMapScreenShoot = pngMapScreenShoot;
	}
}
