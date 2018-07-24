package org.gdocument.gtracergps.launcher.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.gdocument.gtracergps.GpsConstant;
import org.gdocument.gtracergps.launcher.log.Logger;
import org.gdocument.gtracergps.launcher.util.NumberUtil;

import android.os.Parcel;
import android.os.Parcelable;

public class Localisation implements Parcelable {
	private static final String TAG = Localisation.class.getCanonicalName();

	/**
	 * Meter/Sec to Km/Hour conversion value
	 */
	private static final float METER_SEC_TO_KM_HOUR = 3.6f;

	private long id;
	private long idGenereted;
	private long idSend;
	private Session session;
	private float speed;
	private float speedKmH;
	private double longitude;
	private double latitude;
	private double altitude;
	private float accuracy;
	private float bearing;
	private long time;
	private String addressLine;
	private String postalCode;
	private String locality;
	private long timeSend;

	private double calculateDistance;
	private double calculateDistanceCumul;
	private long calculateElapsedTime;
	private long calculateElapsedTimeCumul;
	private float calculateSpeed;
	private float calculateSpeedKmH;
	private float calculateSpeedAverage;
	private float calculateSpeedAverageKmH;

	/**
	 * 
	 */
	public Localisation() {
		super();
		generateId();
	}

	/**
	 * 
	 */
	public Localisation(long id) {
		super();
		this.id = id;
		generateId();
	}
	
	public Localisation(Parcel parcel) {
		readFromParcel(parcel);
	}

	private void generateId() {
		this.idGenereted = new Random().nextLong();
		if (this.idGenereted<0)
			this.idGenereted = -this.idGenereted;
	}

	/**
	 * 
	 * @return
	 */
	public String getTimeFormated() {
		return new SimpleDateFormat(GpsConstant.DATETIME_FORMAT).format(new Date(time));
	}

	/**
	 * Return a simple address formated : addressLine, postalCode, locality
	 * @return a String with a simple address formated
	 */
	public String getSimpleAddress() {
		String simpleAddress = "";
		if (addressLine!=null)
			simpleAddress = addressLine;
		if (postalCode!=null && !"".equals(postalCode)) {// Android ICS 4.1//!postalCode.isEmpty()) {
			simpleAddress += ((!"".equals(simpleAddress)) ? "," : "") + postalCode;// Android ICS 4.1//((!simpleAddress.isEmpty()) ? "," : "") + postalCode;
		}
		if (locality!=null && !"".equals(locality)) {// Android ICS 4.1//!locality.isEmpty()) {
			simpleAddress += ((!"".equals(simpleAddress)) ? "," : "") + locality;// Android ICS 4.1//((!simpleAddress.isEmpty()) ? "," : "") + locality;
		}
		return simpleAddress;
	}

	@Override
	public String toString() {
		return "Localisation [idGenereted: "+idGenereted+" id: "+id+" Long/Lat/Alt: " + longitude + "," + latitude + "," + altitude + " Time: " + getTimeFormated() + " (" + time + ")" 
//				+ " Accuracy: " + accuracy + " Bearing: " + bearing + " Device Speed: " + getSpeedKmHRounded() + " (Km/H) " + getSpeedRounded() + " (Min/Sec)"
//				+ " Address: " + addressLine + " Postal Code: " + postalCode + " City Name: " + locality + " Time Send: " + timeSend
//				+ " calculateDistance: " + calculateDistance + " calculateDistanceCumul: " + calculateDistanceCumul 
//				+ " calculateElapsedTime: " + calculateElapsedTime + " calculateElapsedTimeCumul: " + calculateElapsedTimeCumul
//				+ " calculateSpeed: " + calculateSpeed + " calculateSpeedKmH: " + calculateSpeedKmH + " calculateSpeedAverage: " + calculateSpeedAverage 
//				+ " calculateSpeedAverageKmH: " + calculateSpeedAverageKmH
				+ "]";
	}

	public String toReportHtml() {
		return "<table width='100%'>"
				+ "<tr><td colspan=2><font size='3'>Localisation</font></td></tr>"
				+ "<tr><td width='100pt'>Address</td><td width='*'>" + getSimpleAddress() + "</td></tr>"
				+ "<tr><td>Device Speed (Mt/Sec)</td><td>" + getSpeedRounded() + "</td></tr>"
				+ "<tr><td>Device Speed (Km/H)</td><td>" + getSpeedKmHRounded() + "</td></tr>"
				+ "<tr><td>Longitude</td><td>" + longitude + "</td></tr>" 
				+ "<tr><td>Latitude</td><td>" + latitude + "</td></tr>" 
				+ "<tr><td>Altitude</td><td>" + altitude + "</td></tr>"
				+ "<tr><td>Accuracy</td><td>" + accuracy + "</td></tr>"
				+ "<tr><td>Bearing</td><td>" + bearing + "</td></tr>"
				+ "<tr><td>Time</td><td>" + time + "</td></tr>" 
				+ "<tr><td>Date Time</td><td>" + getTimeFormated() + "</td></tr>"
				+ "<tr><td>Time Send</td><td>" + timeSend + "</td></tr>" 
				+ "</table>";
	}

	public String toFile() {
		return  "[id: "+idGenereted+"]\t" + 
				"[Address: " + addressLine + "]\t" +
				"[PostalCode: " + postalCode + "]\t" +
				"[Locality: " + locality + "]\t" +
				"[Speed (Mt/Sec): " + getSpeedRounded() + "]\t" +
				"[Speed (Km/H): " + getSpeedKmHRounded() + "]\t" +
				"[Longitude: " + longitude + "]\t" +
				"[Latitude: " + latitude + "]\t" +
				"[Altitude: " + altitude + "]\t" +
				"[Accuracy: " + accuracy + "]\t" +
				"[Bearing: " + bearing + "]\t" +
				"[Time: " + time + "]\t" +
				"[TimeSend: " + timeSend + "]";
	}

	public String toSimpleMarker() {
		return "lat='"+latitude+"' lng='"+longitude+"'"+"' alt='"+altitude+"' html='"+getTimeFormated()+"'";
	}

	public String toGmapMarker() {
		return "<marker lat='"+latitude+"' lng='"+longitude+"'"+"' alt='"+altitude+"' html='"+getTimeFormated()+"'/>";
	}
	
	public String toKml() {
		return "<coordinates>"+latitude+","+longitude+","+altitude+"</coordinates>";
	}
	
	public String toKmlPath() {
		return longitude+","+latitude+","+altitude;
	}

	private void logMe(String msg) {
		com.crashlytics.android.Crashlytics.log(msg);
		Logger.logMe(TAG, msg);
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		String msg = "writeToParcel START id:" + id + " time:" + time;// + " idGenereted:" + this.idGenereted + " flags:" + flags;
		try {
//			if (flags==Parcelable.PARCELABLE_WRITE_RETURN_VALUE) {
//				msg += " writeLong id";
				dest.writeLong(id);
//				msg += " writeLong idGenereted";
				dest.writeLong(idGenereted);
//				msg += " writeLong idSend";
				dest.writeLong(idSend);
//				msg += " writeLong session.id";
				dest.writeLong(session==null ? 0 : session.getId());
//				msg += " writeFloat speed";
				dest.writeFloat(speed);
//				msg += " writeFloat speedKmh";
				dest.writeFloat(speedKmH);
//				msg += " writeDouble longitude";
				dest.writeDouble(longitude);
//				msg += " writeDouble latitude";
				dest.writeDouble(latitude);
//				msg += " writeDouble altitude";
				dest.writeDouble(altitude);
//				msg += " writeFloat accuracy";
				dest.writeFloat(accuracy);
//				msg += " writeFloat bearing";
				dest.writeFloat(bearing);
//				msg += " writeLong time";
				dest.writeLong(time);
//				msg += " writeString addressLine";
				dest.writeString(addressLine);
//				msg += " writeString postalCode";
				dest.writeString(postalCode);
//				msg += " writeString locality";
				dest.writeString(locality);
//				msg += " writeLong timeSend";
				dest.writeLong(timeSend);
//				msg += " writeDouble calculateDistance";
				dest.writeDouble(calculateDistance);
//				msg += " writeDouble calculateDistanceCumul";
				dest.writeDouble(calculateDistanceCumul);
//				msg += " writeLong calculateElapsedTime";
				dest.writeLong(calculateElapsedTime);
//				msg += " writeLong calculateElapsedTimeCumul";
				dest.writeLong(calculateElapsedTimeCumul);
//				msg += " writeFloat calculateSpeed";
				dest.writeFloat(calculateSpeed);
//				msg += " writeFloat calculateSpeedKmh";
				dest.writeFloat(calculateSpeedKmH);
//				msg += " writeFloat calculateSpeedAverage";
				dest.writeFloat(calculateSpeedAverage);
//				msg += " writeFloat calculateSpeedAverageKmH";
				dest.writeFloat(calculateSpeedAverageKmH);

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
			id = in.readLong();
			idGenereted = in.readLong();
			idSend = in.readLong();
			long idSession = in.readLong();
			session = (idSession==0) ? null : new Session(idSession);
			speed = in.readFloat();
			speedKmH = in.readFloat();
			longitude = in.readDouble();
			latitude = in.readDouble();
			altitude = in.readDouble();
			accuracy = in.readFloat();
			bearing = in.readFloat();
			time = in.readLong();
			addressLine = in.readString();
			postalCode = in.readString();
			locality = in.readString();
			timeSend = in.readLong();
			calculateDistance = in.readDouble();
			calculateDistanceCumul = in.readDouble();
			calculateElapsedTime = in.readLong();
			calculateElapsedTimeCumul = in.readLong();
			calculateSpeed = in.readFloat();
			calculateSpeedKmH = in.readFloat();
			calculateSpeedAverage = in.readFloat();
			calculateSpeedAverageKmH = in.readFloat();

			msg += " id:" + id + " time:" + time;//" idGenereted:" + this.idGenereted;
			msg += " END";
		}
		finally {
			logMe(msg);
		}
	}

    public long getId() {
		return id;
	}

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
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
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
    public static final Parcelable.Creator<Localisation> CREATOR = new Parcelable.Creator<Localisation>() {
    	private final String TAG = Localisation.TAG + " Parcelable.Creator<Localisation>";

        public Localisation createFromParcel(Parcel in) {
        	Localisation ret = null;
    		String msg = "readFromParcel";
    		try {
    			ret = new Localisation(in);
    		}
    		catch(Exception ex) {
    			msg += " Exception:" + ex.getMessage();
    		}
    		finally {
    			msg += " Localisation:" + (ret==null ? null : ret.getId()) + " Parcel:" + in;
    			logMe(msg);
    		}
            return ret;
        }

        public Localisation[] newArray(int size) {
    		String msg = "newArray size:"+size;
    		logMe(msg);
            return new Localisation[size];
        }

    	private void logMe(String msg) {
			com.crashlytics.android.Crashlytics.log(msg);
    		Logger.logMe(TAG, msg);
        }
    };

	/**
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(float speed) {
		this.speed = speed;
		
		this.speedKmH = speed * METER_SEC_TO_KM_HOUR;
	}

	/**
	 * @return the speed
	 */
	public float getSpeedRounded() {
		return NumberUtil.formatFloat(speed);
	}

	/**
	 * 
	 * @return
	 */
	public float getSpeedKmH() {
		return speedKmH;
	}

	/**
	 * 
	 * @param speedKmH
	 */
	public void setSpeedKmH(float speedKmH) {
		this.speedKmH = speedKmH;

		speed = speedKmH / METER_SEC_TO_KM_HOUR;
	}

	/**
	 * 
	 * @return
	 */
	public float getSpeedKmHRounded() {
		return NumberUtil.formatFloat(speedKmH);
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	/**
	 * @return the altitude
	 */
	public double getAltitude() {
		return altitude;
	}
	/**
	 * @param altitude the altitude to set
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	/**
	 * @return the accuracy
	 */
	public float getAccuracy() {
		return accuracy;
	}
	/**
	 * @param accuracy the accuracy to set
	 */
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	/**
	 * @return the bearing
	 */
	public float getBearing() {
		return bearing;
	}

	/**
	 * @param bearing the bearing to set
	 */
	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @return the timeSend
	 */
	public long getTimeSend() {
		return timeSend;
	}
	/**
	 * @param timeSend the timeSend to set
	 */
	public void setTimeSend(long timeSend) {
		this.timeSend = timeSend;
	}
	/**
	 * @return the addressLine
	 */
	public String getAddressLine() {
		return addressLine;
	}
	/**
	 * @param addressLine the addressLine to set
	 */
	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	/**
	 * @return the locality
	 */
	public String getLocality() {
		return locality;
	}
	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality) {
		this.locality = locality;
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
	 * @return the calculateDistanceCumul
	 */
	public double getCalculateDistanceCumul() {
		return calculateDistanceCumul;
	}

	/**
	 * @param calculateDistanceCumul the calculateDistanceCumul to set
	 */
	public void setCalculateDistanceCumul(double calculateDistanceCumul) {
		this.calculateDistanceCumul = calculateDistanceCumul;
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
	 * @return the calculateElapsedTimeCumul
	 */
	public long getCalculateElapsedTimeCumul() {
		return calculateElapsedTimeCumul;
	}

	/**
	 * @param calculateElapsedTimeCumul the calculateElapsedTimeCumul to set
	 */
	public void setCalculateElapsedTimeCumul(long calculateElapsedTimeCumul) {
		this.calculateElapsedTimeCumul = calculateElapsedTimeCumul;
	}

	/**
	 * @return the calculateSpeed
	 */
	public float getCalculateSpeed() {
		return calculateSpeed;
	}

	/**
	 * @param calculateSpeed the calculateSpeed to set
	 */
	public void setCalculateSpeed(float calculateSpeed) {
		this.calculateSpeed = calculateSpeed;

		this.calculateSpeedKmH = calculateSpeed * METER_SEC_TO_KM_HOUR;
	}

	/**
	 * @return the calculateSpeedKmH
	 */
	public float getCalculateSpeedKmH() {
		return calculateSpeedKmH;
	}

	/**
	 * 
	 * @return
	 */
	public float getCalculateSpeedKmHRounded() {
		return NumberUtil.formatFloat(calculateSpeedKmH);
	}

	/**
	 * @param calculateSpeedKmH the calculateSpeedKmH to set
	 */
	public void setCalculateSpeedKmH(float calculateSpeedKmH) {
		this.calculateSpeedKmH = calculateSpeedKmH;

		this.calculateSpeed = calculateSpeedKmH / METER_SEC_TO_KM_HOUR;
	}

	/**
	 * @return the calculateSpeedAverage
	 */
	public float getCalculateSpeedAverage() {
		return calculateSpeedAverage;
	}

	/**
	 * @param calculateSpeedAverage the calculateSpeedAverage to set
	 */
	public void setCalculateSpeedAverage(float calculateSpeedAverage) {
		this.calculateSpeedAverage = calculateSpeedAverage;

		this.calculateSpeedAverageKmH = calculateSpeedAverage * METER_SEC_TO_KM_HOUR;
	}

	/**
	 * @return the calculateSpeedAverageKmH
	 */
	public float getCalculateSpeedAverageKmH() {
		return calculateSpeedAverageKmH;
	}

	/**
	 * 
	 * @return
	 */
	public float getCalculateSpeedAverageKmHRounded() {
		return NumberUtil.formatFloat(calculateSpeedAverageKmH);
	}

	/**
	 * @param calculateSpeedAverageKmH the calculateSpeedAverageKmH to set
	 */
	public void setCalculateSpeedAverageKmH(float calculateSpeedAverageKmH) {
		this.calculateSpeedAverageKmH = calculateSpeedAverageKmH;

		this.calculateSpeedAverage = calculateSpeedAverageKmH / METER_SEC_TO_KM_HOUR;
	}
}
