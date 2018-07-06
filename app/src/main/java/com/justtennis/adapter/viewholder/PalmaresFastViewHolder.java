package com.justtennis.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.justtennis.R;
import com.justtennis.domain.PalmaresFastValue;

import org.gdocument.gtracergps.launcher.log.Logger;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

public class PalmaresFastViewHolder extends CommonListViewHolder<PalmaresFastValue> {

    private static final String TAG = PalmaresFastViewHolder.class.getSimpleName();

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private TextView tvRanking;
    private EditText etNbVictory;
    private EditText etNbDefeat;

    private PalmaresFastViewHolder(View itemView) {
        super(itemView);

        tvRanking = itemView.findViewById(R.id.tv_ranking);
        etNbVictory = itemView.findViewById(R.id.et_nb_victory);
        etNbDefeat = itemView.findViewById(R.id.et_nb_defeat);
    }

    public static PalmaresFastViewHolder build(View view) {
        return new PalmaresFastViewHolder(view);
    }

    @Override
    public void showData(PalmaresFastValue v) {
//        etNbVictory.setTag(position);
//        etNbDefeat.setTag(position);

        tvRanking.setText(v.getRanking().getRanking());
        etNbVictory.setText(Integer.toString(v.getNbVictory()));
        etNbDefeat.setText(Integer.toString(v.getNbDefeat()));

        Logger.logMe(TAG, MessageFormat.format("PALMARES FAST - PalmaresFastAdapter - onFocusChange setNbVictory data:{0} ranking:{1} nbVictory:{2} nbDefeat:{3}", v, v.getRanking().getRanking(), v.getNbVictory(), v.getNbDefeat()));

        etNbVictory.addTextChangedListener(new EditTextWatcher(etNbVictory, v, true));
        etNbDefeat.addTextChangedListener(new EditTextWatcher(etNbDefeat, v, false));
//		etNbVictory.setOnFocusChangeListener((v1, hasFocus) -> {if (hasFocus) {((EditText) v1).selectAll();}});
//		etNbDefeat.setOnFocusChangeListener((v1, hasFocus) -> {if (hasFocus) {((EditText) v1).selectAll();}});
    }


    public class EditTextWatcher implements TextWatcher {

        private View view;
        private PalmaresFastValue value;
        private boolean victory;

        public EditTextWatcher(View view, PalmaresFastValue value, boolean victory) {
            this.view = view;
            this.value = value;
            this.victory = victory;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (value != null && view.hasFocus()) {
                String txt = ((EditText)view).getText().toString();
                int val = 0;
                try {
                    val = Integer.parseInt(txt);
                } catch (Exception ex) {
                    Logger.logMe(TAG, ex);
                }
                if (victory) {
                    if (value.getNbVictory() != val) {
                        Logger.logMe(TAG, "PALMARES FAST - PalmaresFastAdapter - onTextChange setNbVictory s.toString:" + s.toString() + " data:" + value + " ranking:" + value.getRanking().getRanking() + " val:" + val);
                        value.setNbVictory(val);
                    }
                } else {
                    if (value.getNbDefeat() != val) {
                        Logger.logMe(TAG, "PALMARES FAST - PalmaresFastAdapter - onTextChange setNbDefeat s.toString:" + s.toString() + " data:" + value + " ranking:" + value.getRanking().getRanking() + " val:" + val);
                        value.setNbDefeat(val);
                    }
                }
            }
        }
    }
}
