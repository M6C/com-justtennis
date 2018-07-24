package com.justtennis.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.support.v4.app.ActivityCompat;

import com.justtennis.ApplicationConfig;
import com.justtennis.domain.Invite.STATUS;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.util.TimeZone;

public class GCalendarHelper {

    private static final String TAG = GCalendarHelper.class.getSimpleName();

    public enum EVENT_STATUS {
        UNKNOW(0, Attendees.ATTENDEE_STATUS_TENTATIVE),
        CONFIRMED(1, Attendees.ATTENDEE_STATUS_ACCEPTED),
        CANCELED(2, Attendees.ATTENDEE_STATUS_DECLINED);

        int value = 0;
        int attentee = 0;

        EVENT_STATUS(int value, int attentee) {
            this.value = value;
            this.attentee = attentee;
        }
    }

    public static final int DEFAULT_CALENDAR_ID = 1;
    public static final int EVENT_ID_NO_CREATED = -1;

    private static GCalendarHelper instance;
    private Context context;

    private GCalendarHelper() {
    }

    private GCalendarHelper(Context context) {
        this.context = context;
    }

    public static GCalendarHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GCalendarHelper(context);
        }
        return instance;
    }

    public long addEvent(String title, String description, String location, long startTime, long endTime, boolean allDay, boolean hasAlarm, int calendarId, int selectedReminderValue, EVENT_STATUS status) {

        if (!ApplicationConfig.CALENDAR_ADD_EVENT)
            return EVENT_ID_NO_CREATED;

        if (!ApplicationConfig.CALENDAR_ADD_EVENT_CONFIRMED && (EVENT_STATUS.CONFIRMED == status))
            return EVENT_ID_NO_CREATED;

        if (!ApplicationConfig.CALENDAR_ADD_EVENT_CANCELED && (EVENT_STATUS.CANCELED == status))
            return EVENT_ID_NO_CREATED;

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startTime);
        values.put(Events.DTEND, endTime);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, calendarId);

        if (allDay) {
            values.put(Events.ALL_DAY, true);
        }

        if (hasAlarm) {
            values.put(Events.HAS_ALARM, true);
        }
        values.put(Attendees.SELF_ATTENDEE_STATUS, status.attentee);

        //Get current timezone
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        logMe("Timezone retrieved=>" + TimeZone.getDefault().getID());
        @SuppressLint("MissingPermission") Uri uri = cr.insert(Events.CONTENT_URI, values);
        logMe("Uri returned=>" + uri.toString());
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());

        if (hasAlarm) {
            try {
                ContentValues reminders = new ContentValues();
                reminders.put(Reminders.EVENT_ID, eventID);
                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                reminders.put(Reminders.MINUTES, selectedReminderValue);

                @SuppressLint("MissingPermission") Uri uri2 = cr.insert(Reminders.CONTENT_URI, reminders);
                Cursor c = CalendarContract.Reminders.query(cr, eventID, new String[]{CalendarContract.Reminders.MINUTES});
                if (c.moveToFirst()) {
                    System.out.println("calendar"
                            + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
                }
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return eventID;
	}

	@SuppressWarnings("unused")
	public void addAttendee(int eventId, EVENT_STATUS status, String name) {
		
		if (!ApplicationConfig.CALENDAR_ADD_EVENT)
			return;
		
		if (!ApplicationConfig.CALENDAR_ADD_EVENT_CONFIRMED && (EVENT_STATUS.CONFIRMED == status))
			return;
		
		if (!ApplicationConfig.CALENDAR_ADD_EVENT_CANCELED && (EVENT_STATUS.CANCELED == status))
			return;

		try {
	        ContentResolver cr = context.getContentResolver();
	        ContentValues values = new ContentValues();
	
	        values.put(Attendees.ATTENDEE_NAME, name);
//	        values.put(Attendees.ATTENDEE_EMAIL, "trevor@example.com");
	        values.put(Attendees.ATTENDEE_RELATIONSHIP, Attendees.RELATIONSHIP_ATTENDEE);
	        values.put(Attendees.ATTENDEE_TYPE, Attendees.TYPE_OPTIONAL);
	        
	        values.put(Attendees.ATTENDEE_STATUS, status.attentee);
	        values.put(Attendees.EVENT_ID, eventId);
	
	        Uri uri = cr.insert(Attendees.CONTENT_URI, values);
	        logMe("Uri returned=>"+uri.toString());
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Deprecated
	public int updateEventStatus(long eventId, EVENT_STATUS status) {
		int iNumRowsUpdated = 0;
		try {
	        ContentResolver cr = context.getContentResolver();
	        ContentValues values = new ContentValues();
	
	        // This information is sufficient for most entries 
	        // tentative (0), confirmed (1) or canceled (2):
	        values.put(Attendees.SELF_ATTENDEE_STATUS, status.attentee);
	
	        logMe("Timezone retrieved=>"+TimeZone.getDefault().getID());
	        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
	        logMe("Uri returned=>"+uri.toString());
	        
	        iNumRowsUpdated = cr.update(uri, values, null, null);
	
	        logMe("Updated " + iNumRowsUpdated + " calendar entry.");
		} catch(Exception ex) {
			ex.printStackTrace();
		}

        return iNumRowsUpdated;
	}

	public int recreateEventStatus(long eventId, EVENT_STATUS status) {
		int iNumRowsUpdated = 0;
		try {
	        ContentResolver cr = context.getContentResolver();
	        ContentValues values = new ContentValues();
	
	        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
	        logMe("Uri returned=>"+uri.toString());
	        
	        Cursor cursor = cr.query(uri, null, null, null, null);
	        if (cursor!=null) {
	        	String[] columnNames = cursor.getColumnNames();
	        	for(String name : columnNames) {
	        		int index = cursor.getColumnIndex(name);
	        		switch(cursor.getType(index)) {
		        		case Cursor.FIELD_TYPE_BLOB:
	        				values.put(name, cursor.getBlob(index));
	        				break;
		        		case Cursor.FIELD_TYPE_FLOAT:
	        				values.put(name, cursor.getFloat(index));
	        				break;
		        		case Cursor.FIELD_TYPE_INTEGER:
	        				values.put(name, cursor.getInt(index));
	        				break;
		        		case Cursor.FIELD_TYPE_STRING:
	        				values.put(name, cursor.getString(index));
	        				break;
		        		case Cursor.FIELD_TYPE_NULL:
	        			default:
	        				values.putNull(name);
	        		}
	        	}
	        }
	        
	        for(String name : values.keySet()) {
	        	logMe("recreateEventStatus '" + name + "':" + values.get(name));
	        }

	        values.put(Attendees.SELF_ATTENDEE_STATUS, status.attentee);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

        return iNumRowsUpdated;
	}

	public EVENT_STATUS toEventStatus(STATUS status) {
		switch(status) {
			case ACCEPT:
				return EVENT_STATUS.CONFIRMED;
			case REFUSE:
				return EVENT_STATUS.CANCELED;
			default:
				return EVENT_STATUS.UNKNOW;
		}
	}

	public int deleteCalendarEntry(long eventId) {
		int iNumRowsDeleted = 0;

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
		iNumRowsDeleted = context.getContentResolver().delete(uri, null, null);

		logMe("Deleted " + iNumRowsDeleted + " calendar entry.");

		return iNumRowsDeleted;
	}

//	public int updateEventStatus(int eventId, EVENT_STATUS status) {
//		int iNumRowsUpdated = 0;
//		try {
//        ContentResolver cr = context.getContentResolver();
//        ContentValues values = new ContentValues();
//
//        // This information is sufficient for most entries 
//        // tentative (0), confirmed (1) or canceled (2):
//        values.put(Attendees.SELF_ATTENDEE_STATUS, status.attentee);
//
//        logMe("Timezone retrieved=>"+TimeZone.getDefault().getID());
//        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
//        logMe("Uri returned=>"+uri.toString());
//        
//        iNumRowsUpdated = cr.update(uri, values, null, null);
//
//        logMe("Updated " + iNumRowsUpdated + " calendar entry.");
//		} catch(Exception ex) {
//			ex.printStackTrace();
//		}
//
//        return iNumRowsUpdated;
//	}

//    private int UpdateCalendarEntry(int entryID) {
//        int iNumRowsUpdated = 0;
//
//        ContentValues event = new ContentValues();
//
//        event.put("title", "Changed Event Title");
//        event.put("hasAlarm", 1); // 0 for false, 1 for true
//
//        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
//        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
//
//        iNumRowsUpdated = context.getContentResolver().update(eventUri, event, null,
//                null);
//
//        logMe("Updated " + iNumRowsUpdated + " calendar entry.");
//
//        return iNumRowsUpdated;
//    }
//
//    private int DeleteCalendarEntry(int entryID) {
//        int iNumRowsDeleted = 0;
//
//        Uri eventsUri = Uri.parse(getCalendarUriBase()+"events");
//        Uri eventUri = ContentUris.withAppendedId(eventsUri, entryID);
//        iNumRowsDeleted = context.getContentResolver().delete(eventUri, null, null);
//
//        logMe("Deleted " + iNumRowsDeleted + " calendar entry.");
//
//        return iNumRowsDeleted;
//    }

//	 private long addEvent(String title, String description, String location, int status, long startDate, boolean needReminder, boolean needMailService) {
//		    /***************** Event: note(without alert) *******************/
//
//		    String eventUriString = "content://com.android.calendar/events";
//		    ContentValues eventValues = new ContentValues();
//
//		    eventValues.put("calendar_id", 1); // id, We need to choose from
//		                                        // our mobile for primary
//		                                        // its 1
//		    eventValues.put("title", title);
//		    eventValues.put("description", description);
//		    eventValues.put("eventLocation", location);
//
//		    long endDate = startDate + 1000 * 60 * 60; // For next 1hr
//
//		    eventValues.put("dtstart", startDate);
//		    eventValues.put("dtend", endDate);
//
//		    // values.put("allDay", 1); //If it is bithday alarm or such
//		    // kind (which should remind me for whole day) 0 for false, 1
//		    // for true
//		    eventValues.put("eventStatus", status); // This information is
//		    // sufficient for most
//		    // entries tentative (0),
//		    // confirmed (1) or canceled
//		    // (2):
//		    eventValues.put("visibility", 3); // visibility to default (0),
//		                                        // confidential (1), private
//		                                        // (2), or public (3):
//		    eventValues.put("transparency", 0); // You can control whether
//		                                        // an event consumes time
//		                                        // opaque (0) or transparent
//		                                        // (1).
//		    eventValues.put("hasAlarm", 1); // 0 for false, 1 for true
//
//		    Uri eventUri = context.getApplicationContext().getContentResolver().insert(Uri.parse(eventUriString), eventValues);
//		    long eventID = Long.parseLong(eventUri.getLastPathSegment());
//
//		    if (needReminder) {
//		        /***************** Event: Reminder(with alert) Adding reminder to event *******************/
//
//		        String reminderUriString = "content://com.android.calendar/reminders";
//
//		        ContentValues reminderValues = new ContentValues();
//
//		        reminderValues.put("event_id", eventID);
//		        reminderValues.put("minutes", 5); // Default value of the
//		                                            // system. Minutes is a
//		                                            // integer
//		        reminderValues.put("method", 1); // Alert Methods: Default(0),
//		                                            // Alert(1), Email(2),
//		                                            // SMS(3)
//
//		        Uri reminderUri = context.getApplicationContext().getContentResolver().insert(Uri.parse(reminderUriString), reminderValues);
//		    }
//
//		    /***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/
//
//		    if (needMailService) {
//		        String attendeuesesUriString = "content://com.android.calendar/attendees";
//
//		        /********
//		         * To add multiple attendees need to insert ContentValues multiple
//		         * times
//		         ***********/
//		        ContentValues attendeesValues = new ContentValues();
//
//		        attendeesValues.put("event_id", eventID);
//		        attendeesValues.put("attendeeName", "xxxxx"); // Attendees name
//		        attendeesValues.put("attendeeEmail", "yyyy@gmail.com");// Attendee
//		                                                                            // E
//		                                                                            // mail
//		                                                                            // id
//		        attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
//		                                                        // Relationship_None(0),
//		                                                        // Organizer(2),
//		                                                        // Performer(3),
//		                                                        // Speaker(4)
//		        attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
//		                                                // Required(2), Resource(3)
//		        attendeesValues.put("attendeeStatus", 0); // NOne(0), Accepted(1),
//		                                                    // Decline(2),
//		                                                    // Invited(3),
//		                                                    // Tentative(4)
//
//		        Uri attendeuesesUri = context.getApplicationContext().getContentResolver().insert(Uri.parse(attendeuesesUriString), attendeesValues);
//		    }
//
//		    return eventID;
//
//		}

	private void logMe(String message) {
		com.crashlytics.android.Crashlytics.log(message);
		Logger.logMe(TAG, message);
	}
}