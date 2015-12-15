package com.justtennis.listener.itemclick;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.cameleon.common.android.factory.FactoryDialog;
import com.justtennis.R;
import com.justtennis.activity.ListPersonActivity;
import com.justtennis.domain.Person;
import com.justtennis.domain.Phone;
import com.justtennis.domain.Player;
import com.justtennis.manager.ContactStructuredManager;
import com.justtennis.manager.PhoneManager;
import com.justtennis.parser.PlayerParser;

public class OnItemClickListPerson implements OnItemClickListener {
	private Activity context;
	
	public OnItemClickListPerson(Activity context) {
		this.context = context;
	}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	final Person person = (Person)view.getTag();

    	List<Phone> listPhone = PhoneManager.getInstance(context).getListPhone(context, person.getId());
		if (listPhone!=null && listPhone.size()>0) {
			if (listPhone.size()>1) {
				int i=0;
				String[] listPhonenumber = new String[listPhone.size()];
				for(Phone phone : listPhone) {
					listPhonenumber[i++] = phone.getNumber();
				}
				
				Dialog dialog = FactoryDialog.getInstance().buildListView(context, R.string.txt_phonenumber, listPhonenumber, new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				    	returnPlayer(person, ((TextView)view).getText().toString());
					}
				});
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.show();
			}
			else {
//				String phonenumber = null;
//				Phone phone = listPhone.get(0);
//			    switch (phone.getType()) {
//			        case CommonDataKinds.Phone.TYPE_HOME:
//			            // do something with the Home number here...
//			        	phonenumber = phone.getNumber();
//			            break;
//			        case CommonDataKinds.Phone.TYPE_MOBILE:
//			            // do something with the Mobile number here...
//			        	phonenumber = phone.getNumber();
//			            break;
//			        case CommonDataKinds.Phone.TYPE_WORK:
//			            // do something with the Work number here...
//			        	phonenumber = phone.getNumber();
//			            break;
//		        }
		    	returnPlayer(person, listPhone.get(0).getNumber());
			}
		}
		else {
	    	returnPlayer(person, "");
		}
    }

	private void returnPlayer(Person person, String phonenumber) {
		person = ContactStructuredManager.getInstance(context).getContact(context, person.getId());
		person.setPhonenumber(phonenumber);

		Player player = PlayerParser.getInstance().fromPersonForCreate(person);
		Intent intent = new Intent();
		intent.putExtra(ListPersonActivity.EXTRA_PLAYER, player);
		context.setResult(Activity.RESULT_OK, intent);
		context.finish();
	}
}