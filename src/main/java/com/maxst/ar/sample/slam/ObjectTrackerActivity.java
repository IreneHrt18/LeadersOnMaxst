/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package com.maxst.ar.sample.slam;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.ResultCode;
import com.maxst.ar.SensorDevice;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.sample.ARActivity;
import com.maxst.ar.sample.R;
import com.maxst.ar.sample.util.SampleUtil;

public class ObjectTrackerActivity extends ARActivity implements View.OnTouchListener {

	public static final String MAP_FILE_NAME_KEY = "map_file_name";

	private GLSurfaceView glSurfaceView;
	private int preferCameraResolution = 0;
	private ObjectTrackerRenderer renderer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_object_tracker);

		renderer = new ObjectTrackerRenderer(this);
		glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setRenderer(renderer);
		glSurfaceView.setOnTouchListener(this);

		preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);

		String mapFileName = getIntent().getStringExtra(MAP_FILE_NAME_KEY);
		TrackerManager.getInstance().addTrackerData(mapFileName, false);
		TrackerManager.getInstance().loadTrackerData();
	}

	@Override
	protected void onResume() {
		super.onResume();

		glSurfaceView.onResume();
		TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_OBJECT);
		TrackerManager.getInstance().setTrackingOption(TrackerManager.TrackingOption.JITTER_REDUCTION_ACTIVATION);

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

		//CameraDevice.getInstance().setAutoWhiteBalanceLock(true); // For ODG-R7 preventing camera flickering

		MaxstAR.onResume();
		renderer.resetTranslation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();

		TrackerManager.getInstance().stopTracker();
		CameraDevice.getInstance().stop();
		SensorDevice.getInstance().stop();

		MaxstAR.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, final MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				float[] screen = new float[2];
				screen[0] = x;
				screen[1] = y;
				float[] world = new float[3];
				TrackerManager.getInstance().getWorldPositionFromScreenCoordinate(screen, world);
				renderer.setTranslation(world[0], world[1], world[2]);
				break;
		}

		return true;
	}
}
