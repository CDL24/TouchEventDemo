package com.reseeit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.reseeit.R;

public class Util {

	// public static void startAct(Context context, Class<?> clazz) {
	// startAct(context, clazz, 4);
	// }

	public static void translateLeft(Context context) {
		((Activity) context).overridePendingTransition(R.anim.center_to_left, R.anim.right_to_center);
	}

	public static void translateRight(Context context) {
		((Activity) context).overridePendingTransition(R.anim.center_to_right, R.anim.left_to_center);
	}

	public static void translateTop(Context context) {
		((Activity) context).overridePendingTransition(R.anim.center_to_top, R.anim.bottom_to_center);
	}

	public static void translateBottom(Context context) {
		((Activity) context).overridePendingTransition(R.anim.center_to_bottom, R.anim.top_to_center);
	}

	public static void translateFadeIn(Context context) {
		((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	public static void translateFadeOut(Context context) {
		((Activity) context).overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
	}

	public static void startAct(Context source, Class<?> act, int position) {
		Intent intent = new Intent(source, act);
		source.startActivity(intent);
		((Activity) source).finish();
		switch (position) {
		case 0:
			translateLeft(source);
			break;
		case 1:
			translateRight(source);
			break;
		case 2:
			translateTop(source);
			break;
		case 3:
			translateBottom(source);
			break;
		case 4:
			translateFadeIn(source);
			break;
		case 5:
			translateFadeOut(source);
			break;

		default:
			break;
		}
	}

	public static void toast(Context context, String string) {
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

}
