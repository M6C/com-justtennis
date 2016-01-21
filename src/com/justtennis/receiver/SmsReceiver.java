package com.justtennis.receiver;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.justtennis.ApplicationConfig;
import com.justtennis.R;
import com.justtennis.activity.InviteActivity;
import com.justtennis.activity.InviteDemandeActivity;
import com.justtennis.activity.InviteDemandeActivity.MODE;
import com.justtennis.activity.MainActivity;
import com.justtennis.activity.PlayerActivity;
import com.justtennis.db.service.InviteService;
import com.justtennis.db.service.PlayerService;
import com.justtennis.db.service.UserService;
import com.justtennis.domain.Invite;
import com.justtennis.domain.Player;
import com.justtennis.domain.User;
import com.justtennis.helper.GCalendarHelper;
import com.justtennis.helper.GCalendarHelper.EVENT_STATUS;
import com.justtennis.notifier.NotifierMessageLogger;
import com.justtennis.parser.SmsParser;
import com.justtennis.parser.SmsParser.MSG_TYPE;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = SmsReceiver.class.getSimpleName();

	private NotificationManager mNM;
	private Context context;
	private String phoneNumber;

	@Override
	public void onReceive(Context context, Intent intent) {
		logMe("onReceive");
		this.context = context;

		// Retrieves a map of extended data from the intent.
		final Bundle bundle = intent.getExtras();

		try {

			if (bundle != null) {

				Object[] pdusObj = (Object[]) bundle.get("pdus");
				if (pdusObj!=null) {
					int size = pdusObj.length;
	        		logMe("nb pdusObj:" + size);
	
	        		String message = "";
					for (int i = 0; i < size; i++) {
	
						SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);

						phoneNumber = currentMessage.getDisplayOriginatingAddress();
						message += currentMessage.getDisplayMessageBody();
						logSms("onReceive message["+i+"]:", message);
					} // end for loop
					logSms("onReceive message:", message);
	
					if (!"".equals(message)) {
						processMessage(message);
					}
				}
				else {
	        		logMe("pdusObj is empty");
				}
			} // bundle is null
			else {
        		logMe("bundle is null");
			}

		} catch (Exception e) {
			Logger.logMe(TAG, e);

		}
	}
	
	private void processMessage(String message) {
		MSG_TYPE msgType = SmsParser.getInstance(context).getMessageType(message);
		if (!MSG_TYPE.UNKNOW.equals(msgType)) {
//			logMe("Knowed Message:" + message);
			abortBroadcast();
			showNotification(msgType, message);
		} else {
			int len = (message.length() > 20 ? 20 : message.length());
			logMe("Unknowed from:" + phoneNumber + " message:" + message.substring(0, len/2) + "..." + message.substring(message.length()-(len-(len/2))));
		}
	}

	@SuppressWarnings("deprecation")
	private void showNotification(MSG_TYPE msgType, String message) {
		Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; type:" + msgType + "; message: " + message);

		if (mNM == null)
			mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int id = (int) System.currentTimeMillis();
		int NOTIFICATION = R.string.txt_notification_invite;

		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		CharSequence text = context.getText(NOTIFICATION);

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.icon_01, text, System.currentTimeMillis());

		Invite invite = buildInvite(msgType, message);
		if (invite!=null) {
			Intent intent = buildIntent(msgType, invite);
			if (intent != null) {
				// The PendingIntent to launch our activity if the user selects this
				// notification
				PendingIntent contentIntent = PendingIntent.getActivity(context, id, intent, 0);
	
				// Set the info for the views that show in the notification panel.
				message = buildText(msgType, invite);
				notification.setLatestEventInfo(context, text, message, contentIntent);
	
				// Cancel the notification after its selected
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
	
				// Send the notification.
				mNM.notify(NOTIFICATION, notification);
			}
		}
	}

	private Intent buildIntent(MSG_TYPE msgType, Invite invite) {
		Intent intent = null;

		InviteService inviteService = new InviteService(context, NotifierMessageLogger.getInstance());
		UserService userService = new UserService(context, NotifierMessageLogger.getInstance());
		PlayerService playerService = new PlayerService(context, NotifierMessageLogger.getInstance());

		switch (msgType) {
			case PLAY_INVITE: {
				Invite invite2 = new Invite(userService.find(), playerService.find(invite.getPlayer().getIdExternal()), invite.getDate(), invite.getStatus());
				invite2.setIdExternal(invite.getId());
				
				inviteService.createOrUpdate(invite2);
	
				intent = new Intent(context, InviteActivity.class);
				intent.putExtra(InviteActivity.EXTRA_MODE, MODE.INVITE_CONFIRM);
				intent.putExtra(InviteActivity.EXTRA_INVITE, invite2);
				break;
			}
			case PLAY_INVITE_YES:
			case PLAY_INVITE_NO: {
				Invite invite1 = inviteService.find(invite.getIdExternal());

				Invite invite2 = new Invite(userService.find(), playerService.find(invite.getPlayer().getIdExternal()), invite.getDate(), invite.getStatus());
				invite2.setId(invite.getIdExternal());
				invite2.setIdExternal(invite.getId());
				invite2.setIdCalendar(invite1.getIdCalendar());
				inviteService.createOrUpdate(invite2);

				Toast.makeText(context, buildText(msgType, invite2), Toast.LENGTH_LONG).show();

				calendarAddAttentee(invite2, (msgType == MSG_TYPE.PLAY_INVITE_YES ? EVENT_STATUS.CONFIRMED : EVENT_STATUS.CANCELED));
				break;
			}
			case PLAYER_ADD: {
				intent = new Intent(context, PlayerActivity.class);
				intent.putExtra(PlayerActivity.EXTRA_MODE, PlayerActivity.MODE.DEMANDE_ADD);
				intent.putExtra(PlayerActivity.EXTRA_INVITE, invite);
				break;
			}
			case PLAYER_ADD_YES: {
				Toast.makeText(context, buildText(msgType, invite), Toast.LENGTH_LONG).show();
	
				Player player = playerService.find(invite.getPlayer().getIdExternal());
				player.setIdExternal(invite.getPlayer().getId());
				playerService.createOrUpdate(player);
				break;
			}
			case PLAYER_ADD_NO: {
				Toast.makeText(context, buildText(msgType, invite), Toast.LENGTH_LONG).show();
				break;
			}
	
			default:
				intent = new Intent(context, MainActivity.class);
				break;
		}
		return intent;
	}

	private Invite buildInvite(MSG_TYPE msgType, String message) {
		Invite invite = null;

		switch (msgType) {
			case PLAY_INVITE: {
				invite = SmsParser.getInstance(context).fromMessageInvite(message);
				break;
			}
			case PLAY_INVITE_YES: {
				invite = SmsParser.getInstance(context).fromMessageInviteConfirmYes(message);
				break;
			}
			case PLAY_INVITE_NO: {
				invite = SmsParser.getInstance(context).fromMessageInviteConfirmNo(message);
				break;
			}
			case PLAYER_ADD: {
				invite = SmsParser.getInstance(context).fromMessageAdd(message);
				break;
			}
			case PLAYER_ADD_YES: {
				invite = SmsParser.getInstance(context).fromMessageDemandeAddYes(message);
				break;
			}
			case PLAYER_ADD_NO: {
				invite = SmsParser.getInstance(context).fromMessageDemandeAddNo(message);
				break;
			}
	
			default:
				break;
		}
		return invite;
	}

	private String buildText(MSG_TYPE msgType, Invite invite) {
		String text = null;

		User user = invite.getUser();
		Player player = invite.getPlayer();
		switch (msgType) {
			case PLAY_INVITE: {
				text = context.getString(R.string.msg_invite, player.getFirstName(), player.getLastName());
				break;
			}
			case PLAY_INVITE_YES: {
				text = context.getString(R.string.msg_invite_confirm_yes, player.getFirstName(), player.getLastName());
				break;
			}
			case PLAY_INVITE_NO: {
				text = context.getString(R.string.msg_invite_confirm_no, player.getFirstName(), player.getLastName());
				break;
			}
			case PLAYER_ADD: {
				text = context.getString(R.string.msg_demande_add, user.getFirstName(), user.getLastName());
				break;
			}
			case PLAYER_ADD_YES: {
				text = context.getString(R.string.msg_demande_add_yes, user.getFirstName(), user.getLastName());
				break;
			}
			case PLAYER_ADD_NO: {
				text = context.getString(R.string.msg_demande_add_no, user.getFirstName(), user.getLastName());
				break;
			}
	
			default:
				text = "";
				break;
		}
		if (ApplicationConfig.SHOW_ID) {
			text += " [invite:" + invite.getId() + "|user:" + invite.getUser().getId() + "|player:" + invite.getPlayer().getId() + "|calendar:" + invite.getIdCalendar() + "]";
		}
		return text;
	}

	private void calendarAddAttentee(Invite invite, EVENT_STATUS status) {
		Player player = invite.getPlayer();
		String name = player.getFirstName() + " " + player.getLastName();
		if (ApplicationConfig.SHOW_ID) {
			name += " [id:" + player.getId() + "|idExt:" + player.getIdExternal() + "]";
		}
		GCalendarHelper.getInstance(context).addAttendee(invite.getIdCalendar().intValue(), status, name);
	}

	private void logMe(String message) {
		Logger.logMe(TAG, message);
	}

	private void logSms(String title, String data) {
		if (ApplicationConfig.SHOW_LOG_SMS_PROCESS) {
			logMe("SHOW_LOG_SMS_PROCESS "+title+data);
		}
	}
}