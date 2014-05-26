package com.justtennis.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.justtennis.R;
import com.justtennis.manager.TypeManager;
import com.justtennis.tool.QRCodeTool;

public class QRCodeActivity extends Activity {

	private static final String TAG = QRCodeActivity.class.getSimpleName();

	public static final String EXTRA_QRCODE_DATA = "QRCODE_DATA";

	private ImageView image_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode);

		image_view = (ImageView) findViewById(R.id.qr_code);
		TypeManager.getInstance().initializeActivity(findViewById(R.id.layout_main), false);
	}

	@Override
	protected void onResume() {
		String barcode_content = getIntent().getStringExtra(EXTRA_QRCODE_DATA);
		try {
			// generate a 150x150 QR code
			Bitmap bm = QRCodeTool.getInstance().encodeAsBitmap(
				barcode_content, 
				BarcodeFormat.QR_CODE, 
				image_view.getWidth(), 
				image_view.getHeight()
			);

			if (bm != null) {
				image_view.setImageBitmap(bm);
			}
		} catch (WriterException e) {
			Log.e(TAG, "QRCode generation", e);
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
