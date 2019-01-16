/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package com.maxst.ar.sample.cloudRecognizer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.ResultCode;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.sample.ARActivity;
import com.maxst.ar.sample.R;
import com.maxst.ar.sample.util.SampleUtil;

public class CloudRecognizerActivity extends ARActivity {

	private CloudRecognizerRenderer cloudTargetRenderer;
	private GLSurfaceView glSurfaceView;
	private int preferCameraResolution = 0;
	private TextView metaValueView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cloud_tracker);
		metaValueView = (TextView)findViewById(R.id.meta_text);

		cloudTargetRenderer = new CloudRecognizerRenderer(this);
		cloudTargetRenderer.listener = resultListener;

		glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setRenderer(cloudTargetRenderer);

		preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();

		glSurfaceView.onResume();

		/* Add SecertId and SecretKey */
		TrackerManager.getInstance().setCloudRecognitionSecretIdAndKey("", "");
		TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_CLOUD_RECOGNIZER);

		ResultCode resultCode = ResultCode.Success;
		switch (preferCameraResolution) {
			case 0:
				resultCode = CameraDevice.getInstance().start(0, 640, 480);
				break;

			case 1:
				resultCode = CameraDevice.getInstance().start(0, 1280, 720);
				break;

			case 2:
				resultCode = CameraDevice.getInstance().start(0, 1920, 1080);
				break;
		}

		if (resultCode != ResultCode.Success) {
			Toast.makeText(this, R.string.camera_open_fail, Toast.LENGTH_SHORT).show();
			finish();
		}

		MaxstAR.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		glSurfaceView.onPause();

		TrackerManager.getInstance().stopTracker();
		CameraDevice.getInstance().stop();
		MaxstAR.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private CloudRecognitionResultListener resultListener = new CloudRecognitionResultListener() {
		@Override
		public void sendData(String cloudName, final String cloudMetaData) {
			(CloudRecognizerActivity.this).runOnUiThread(new Runnable(){
				@Override
				public void run() {
					metaValueView.setText(cloudMetaData);
				}
			});
		}
	};

	public interface CloudRecognitionResultListener {

		void sendData(String cloudName, String cloudMetaData);
	}

}
