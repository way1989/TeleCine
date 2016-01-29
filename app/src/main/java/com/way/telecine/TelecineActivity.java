package com.way.telecine;

import com.way.screenshot.TakeScreenshotActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

public final class TelecineActivity extends Activity implements OnCheckedChangeListener {
	public static final String VIDEO_SIZE_KEY = "videoSize";
	public static final String SHOW_COUNTDOWN_KEY = "showCountdown";
	public static final String HIDE_FROM_RECENTS_KEY = "hideFromRecents";
	public static final String RECORDING_NOTIFICATION_KEY = "recordingNotification";
	public static final String SHOW_TOUCHES_KEY = "showTouches";
	Spinner videoSizePercentageView;
	Switch showCountdownView;
	Switch hideFromRecentsView;
	Switch recordingNotificationView;
	Switch showTouchesView;
	SharedPreferences sharedPreferences;
	private VideoSizePercentageAdapter videoSizePercentageAdapter;
	private int longClickCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		setContentView(R.layout.activity_main);
		initViews();
		Resources res = getResources();
		Bitmap taskIcon = BitmapFactory.decodeResource(res, R.drawable.ic_videocam_white_48dp);
		setTaskDescription(new ActivityManager.TaskDescription(res.getString(R.string.app_name), taskIcon,
				res.getColor(R.color.primary_normal)));

		videoSizePercentageAdapter = new VideoSizePercentageAdapter(this);
		Log.i("way", "videoSizePercentageAdapter = " + videoSizePercentageAdapter + ", videoSizePercentageView = "
				+ videoSizePercentageView);
		videoSizePercentageView.setAdapter(videoSizePercentageAdapter);
		videoSizePercentageView.setSelection(
				VideoSizePercentageAdapter.getSelectedPosition(sharedPreferences.getInt(VIDEO_SIZE_KEY, 100)));

		showCountdownView.setChecked(sharedPreferences.getBoolean(SHOW_COUNTDOWN_KEY, true));
		hideFromRecentsView.setChecked(sharedPreferences.getBoolean(HIDE_FROM_RECENTS_KEY, true));
		recordingNotificationView.setChecked(sharedPreferences.getBoolean(RECORDING_NOTIFICATION_KEY, true));
		showTouchesView.setChecked(sharedPreferences.getBoolean(SHOW_TOUCHES_KEY, true));
	}

	private void initViews() {
		videoSizePercentageView = (Spinner) findViewById(R.id.spinner_video_size_percentage);
		videoSizePercentageView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				onVideoSizePercentageSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		showCountdownView = (Switch) findViewById(R.id.switch_show_countdown);
		hideFromRecentsView = (Switch) findViewById(R.id.switch_hide_from_recents);
		recordingNotificationView = (Switch) findViewById(R.id.switch_recording_notification);
		showTouchesView = (Switch) findViewById(R.id.switch_show_touches);

		showCountdownView.setOnCheckedChangeListener(this);
		hideFromRecentsView.setOnCheckedChangeListener(this);
		recordingNotificationView.setOnCheckedChangeListener(this);
		showTouchesView.setOnCheckedChangeListener(this);
		findViewById(R.id.screen_shot).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(TelecineActivity.this, TakeScreenshotActivity.class));
				
			}
		});
		findViewById(R.id.launch).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onLaunchClicked();
			}
		});
	}

	protected void onLaunchClicked() {
		if (longClickCount > 0) {
			longClickCount = 0;
			Log.d("way", "Long click count reset.");
		}

		Log.d("way", "Attempting to acquire permission to screen capture.");
		CaptureHelper.fireScreenCaptureIntent(this);
	}

	boolean onLongClick() {
		if (++longClickCount == 5) {
			throw new RuntimeException("Crash! Bang! Pow! This is only a test...");
		}
		Log.d("way", "Long click count updated to " + longClickCount);
		return true;
	}

	void onVideoSizePercentageSelected(int position) {
		int newValue = videoSizePercentageAdapter.getItem(position);
		int oldValue = sharedPreferences.getInt(VIDEO_SIZE_KEY, 100);
		if (newValue != oldValue) {
			Log.d("way", "Video size percentage changing to " + newValue);
			sharedPreferences.edit().putInt(VIDEO_SIZE_KEY, newValue).apply();

		}
	}

	void onShowCountdownChanged() {
		boolean newValue = showCountdownView.isChecked();
		boolean oldValue = sharedPreferences.getBoolean(SHOW_COUNTDOWN_KEY, true);
		if (newValue != oldValue) {
			Log.d("way", "Hide show countdown changing to " + newValue);
			sharedPreferences.edit().putBoolean(SHOW_COUNTDOWN_KEY, newValue).apply();

		}
	}

	void onHideFromRecentsChanged() {
		boolean newValue = hideFromRecentsView.isChecked();
		boolean oldValue = sharedPreferences.getBoolean(HIDE_FROM_RECENTS_KEY, true);
		if (newValue != oldValue) {
			Log.d("way", "Hide from recents preference changing to " + newValue);
			sharedPreferences.edit().putBoolean(HIDE_FROM_RECENTS_KEY, newValue).apply();

		}
	}

	void onRecordingNotificationChanged() {
		boolean newValue = recordingNotificationView.isChecked();
		boolean oldValue = sharedPreferences.getBoolean(RECORDING_NOTIFICATION_KEY, true);
		if (newValue != oldValue) {
			Log.d("way", "Recording notification preference changing to " + newValue);
			sharedPreferences.edit().putBoolean(RECORDING_NOTIFICATION_KEY, newValue).apply();
		}
	}

	void onShowTouchesChanged() {
		boolean newValue = showTouchesView.isChecked();
		boolean oldValue = sharedPreferences.getBoolean(SHOW_TOUCHES_KEY, false);
		if (newValue != oldValue) {
			Log.d("way", "Show touches preference changing to " + newValue);
			sharedPreferences.edit().putBoolean(SHOW_TOUCHES_KEY, newValue).apply();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!CaptureHelper.handleActivityResult(this, requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (sharedPreferences.getBoolean(HIDE_FROM_RECENTS_KEY, true) && !isChangingConfigurations()) {
			Log.d("way", "Removing task because hide from recents preference was enabled.");
			finishAndRemoveTask();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.switch_show_countdown:
			onShowCountdownChanged();
			break;
		case R.id.switch_hide_from_recents:
			onHideFromRecentsChanged();
			break;

		case R.id.switch_recording_notification:
			onRecordingNotificationChanged();
			break;

		case R.id.switch_show_touches:
			onShowTouchesChanged();
			break;

		default:
			break;
		}
	}
}
