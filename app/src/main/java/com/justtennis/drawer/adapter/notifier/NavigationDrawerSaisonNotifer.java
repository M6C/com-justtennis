package com.justtennis.drawer.adapter.notifier;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.cameleon.common.android.factory.FactoryDialog;
import com.cameleon.common.android.factory.listener.OnClickViewListener;
import com.justtennis.R;
import com.justtennis.adapter.CustomArrayAdapter;
import com.justtennis.domain.Saison;
import com.justtennis.drawer.manager.DrawerManager;

import java.util.List;

public final class NavigationDrawerSaisonNotifer implements INavigationDrawerNotifer {

    private static final String TAG = NavigationDrawerSaisonNotifer.class.getName();

    private DrawerManager drawerManager;
    private CustomArrayAdapter<String> adpSaison;
    private Spinner spSaison;
    private Context context;
    private View btnAdd;
    private View btnDel;

    public NavigationDrawerSaisonNotifer(DrawerManager drawerManager) {
        this.drawerManager = drawerManager;
    }

    @Override
    public void onCreateView(View view) {
        context = view.getContext().getApplicationContext();
        btnAdd = view.findViewById(R.id.btn_add_saison);
        btnDel = view.findViewById(R.id.btn_del_saison);

        spSaison = (Spinner)view.findViewById(R.id.sp_saison);
        initializeSaisonList();
        initializeSaison();
        initializeSaisonButton();
    }

    @Override
    public void onUpdateView(View view) {
    }

    private void initializeSaisonButton() {
        Log.d(TAG, "initializeSaisonButton");

        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                OnClickViewListener onClickOkListener = new OnClickViewListener() {

                    @Override
                    public void onClick(DialogInterface dialog, View view, int which) {
                        CheckBox cbActivate = (CheckBox) view.findViewById(R.id.cb_activate);
                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.dp_saison_year);
                        int year = datePicker.getYear();
                        if (!drawerManager.getBusiness().isExistSaison(year)) {
                            boolean active = cbActivate.isChecked();
                            Saison saison = drawerManager.getBusiness().createSaison(year, active);
                            drawerManager.getTypeManager().setSaison(saison);

                            drawerManager.getBusiness().initializeDataSaison();
                            drawerManager.getNotiferSaison().initializeSaison();
                        } else {
                            Toast.makeText(drawerManager.getActivity(), R.string.error_message_saison_already_exist, Toast.LENGTH_LONG).show();
                        }
                    }
                };

                FactoryDialog.getInstance()
                    .buildLayoutDialog(drawerManager.getActivity(), onClickOkListener, null, R.string.dialog_saison_add_title, R.layout.dialog_saison_year_picker, R.id.ll_main)
                    .show();
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Saison saison = drawerManager.getTypeManager().getSaison();
                            if (!drawerManager.getBusiness().isEmptySaison(saison)) {
                                if (!drawerManager.getBusiness().isExistInviteSaison(saison)) {
                                    drawerManager.getBusiness().deleteSaison(saison);
                                    drawerManager.getTypeManager().reinitialize(drawerManager.getActivity(), drawerManager.getNotificationMessage());

                                    drawerManager.getBusiness().initializeDataSaison();
                                    drawerManager.getNotiferSaison().initializeSaison();
                                    drawerManager.updValue();
                                } else {
                                    Toast.makeText(drawerManager.getActivity(), R.string.error_message_invite_exist_saison, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                };

                FactoryDialog.getInstance()
                    .buildYesNoDialog(context, onClickListener, R.string.dialog_saison_del_title, R.string.dialog_saison_del_message)
                    .show();
            }
        });

    }

    private void initializeSaisonList() {
        Log.d(TAG, "initializeSaisonList");
        adpSaison = new CustomArrayAdapter<String>(context, drawerManager.getBusiness().getListTxtSaisons());
        spSaison.setAdapter(adpSaison);

        spSaison.setOnItemSelectedListener(adpSaison.new OnItemSelectedListener<Saison>() {
            @Override
            public Saison getItem(int position) {
                return drawerManager.getBusiness().getListSaison().get(position);
            }

            @Override
            public boolean isHintItemSelected(Saison item) {
                return drawerManager.getBusiness().isEmptySaison(item);
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id, Saison item) {
                drawerManager.getTypeManager().setSaison(drawerManager.getBusiness().getListSaison().get(position));
                if (drawerManager.getDrawerLayoutSaisonNotifier() != null) {
                    drawerManager.getDrawerLayoutSaisonNotifier().onDrawerLayoutSaisonChange(parent, view, position, id, item);
                }
            }
        });
    }

    public void initializeSaison() {
        Log.d(TAG, "initializeSaison");
        Saison saison = drawerManager.getTypeManager().getSaison();
        int position = 0;
        List<Saison> listSaison = drawerManager.getBusiness().getListSaison();
        for(Saison item : listSaison) {
            if (item.equals(saison)) {
                spSaison.setSelection(position, true);
                break;
            } else {
                position++;
            }
        }
        adpSaison.notifyDataSetChanged();
    }
}
