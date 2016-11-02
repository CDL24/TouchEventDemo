package com.reseeit;

import android.app.Activity;
import android.content.Intent;
import com.reseeit.util.MyPrefs;

public class NotificationActivity extends Activity {
	@Override
	protected void onResume() {
		super.onResume();
		if (new MyPrefs(getApplicationContext()).isLogin()) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtra("extra", getIntent().getExtras());
			startActivity(intent);
		}
		finish();
	}
}
