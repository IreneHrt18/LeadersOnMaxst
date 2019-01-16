/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package com.maxst.ar.sample.slam;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.MaxstARUtil;
import com.maxst.ar.Trackable;
import com.maxst.ar.TrackedImage;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.TrackingResult;
import com.maxst.ar.TrackingState;
import com.maxst.ar.sample.arobject.AxisRenderer;
import com.maxst.ar.sample.arobject.BackgroundRenderHelper;
import com.maxst.ar.sample.arobject.FeaturePointRenderer;
import com.maxst.ar.sample.arobject.TexturedCubeRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class SlamRenderer implements Renderer {

	private int surfaceWidth;
	private int surfaceHeight;

	private FeaturePointRenderer featurePointRenderer;
	private AxisRenderer axisRenderer;
	private final Activity activity;
	private boolean showTrackingLostPopupOnce = false;
	private TexturedCubeRenderer texturedCubeRenderer;
	private BackgroundRenderHelper backgroundRenderHelper;
	private float posX, posY, posZ;

	SlamRenderer(Activity activity) {
		this.activity = activity;
		backgroundRenderHelper = new BackgroundRenderHelper();
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);

		TrackingState state = TrackerManager.getInstance().updateTrackingState();
		TrackingResult trackingResult = state.getTrackingResult();

		TrackedImage image = state.getImage();
		float[] backgroundPlaneProjectionMatrix = CameraDevice.getInstance().getBackgroundPlaneProjectionMatrix();
		backgroundRenderHelper.drawBackground(image, backgroundPlaneProjectionMatrix);

		float[] projectionMatrix = CameraDevice.getInstance().getProjectionMatrix();

		featurePointRenderer.setProjectionMatrix(projectionMatrix);
		featurePointRenderer.draw(TrackerManager.getInstance().getGuideInformation(), trackingResult);

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		if (trackingResult.getCount() > 0) {
			Trackable trackable = trackingResult.getTrackable(0);

			axisRenderer.setProjectionMatrix(projectionMatrix);
			axisRenderer.setTransform(trackable.getPoseMatrix());
			axisRenderer.setTranslate(0, 0, 0);
			axisRenderer.setScale(0.3f, 0.3f, 0.3f);
			axisRenderer.draw();

			texturedCubeRenderer.setTransform(trackable.getPoseMatrix());
			texturedCubeRenderer.setTranslate(posX, posY, posZ);
			texturedCubeRenderer.setProjectionMatrix(projectionMatrix);
			texturedCubeRenderer.draw();

			showTrackingLostPopupOnce = true;
		} else {
			if (showTrackingLostPopupOnce) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, "Lost Tracking. Please restart", Toast.LENGTH_SHORT).show();
					}
				});

				showTrackingLostPopupOnce = false;
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		surfaceWidth = width;
		surfaceHeight = height;
		texturedCubeRenderer.setScale(0.1f, 0.1f, 0.1f);
		MaxstAR.onSurfaceChanged(width, height);
		Log.i("MaxstAR", "width : " + width + " height : " + height);
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		featurePointRenderer = new FeaturePointRenderer();
		axisRenderer = new AxisRenderer();

		Bitmap blueBitmap = MaxstARUtil.getBitmapFromAsset("bluedot.png", activity.getAssets());
		Bitmap redBitmap = MaxstARUtil.getBitmapFromAsset("reddot.png", activity.getAssets());
		featurePointRenderer.setFeatureImage(blueBitmap, redBitmap);

		texturedCubeRenderer = new TexturedCubeRenderer();
		Bitmap bitmap = MaxstARUtil.getBitmapFromAsset("MaxstAR_Cube.png", activity.getAssets());
		texturedCubeRenderer.setTextureBitmap(bitmap);

		backgroundRenderHelper = new BackgroundRenderHelper();
	}

	public void setTranslation(float x, float y, float z)
	{
		posX = x;
		posY = y;
		posZ = z;
	}

	public void resetTranslation()
	{
		posX = posY = posZ = 0.0f;
	}
}
