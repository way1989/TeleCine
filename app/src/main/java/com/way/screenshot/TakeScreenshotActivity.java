package com.way.screenshot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TakeScreenshotActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScreenshotHelper.fireScreenCaptureIntent(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!ScreenshotHelper.handleActivityResult(this, requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
		finish();
	}

	@Override
	protected void onStop() {
		if (!isFinishing()) {
			finish();
		}
		super.onStop();
	}
}
