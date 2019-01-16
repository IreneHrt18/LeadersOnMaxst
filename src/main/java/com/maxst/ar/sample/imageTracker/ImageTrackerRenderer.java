/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */
package com.maxst.ar.sample.imageTracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.MaxstARUtil;
import com.maxst.ar.Trackable;
import com.maxst.ar.TrackedImage;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.TrackingResult;
import com.maxst.ar.TrackingState;
import com.maxst.ar.sample.arobject.BackgroundRenderHelper;
import com.maxst.ar.sample.arobject.Yuv420spRenderer;
import com.maxst.ar.sample.arobject.ChromaKeyVideoRenderer;
import com.maxst.ar.sample.arobject.ColoredCubeRenderer;
import com.maxst.ar.sample.arobject.TexturedCubeRenderer;
import com.maxst.ar.sample.arobject.VideoRenderer;
import com.maxst.ar.sample.dijkstra.Point;
import com.maxst.videoplayer.VideoPlayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


class ImageTrackerRenderer implements Renderer {

	public static final String TAG = ImageTrackerRenderer.class.getSimpleName();

	private TexturedCubeRenderer texturedCubeRenderer;
	private ColoredCubeRenderer coloredCubeRenderer;
	private VideoRenderer videoRenderer;
	private ChromaKeyVideoRenderer chromaKeyVideoRenderer;

	private int surfaceWidth;
	private int surfaceHeight;
	private BackgroundRenderHelper backgroundRenderHelper;

	private HashMap<String,Point> renderMap;
	private final Activity activity;

	ImageTrackerRenderer(Activity activity, ArrayList<Point> pointArrayList) {
		this.activity = activity;
		this.renderMap=new HashMap<String, Point>();
		int index=0;
		for (Point point :
				pointArrayList) {
			if(index<pointArrayList.size()-1) {
				point.cross = pointArrayList.get(++index).layer - point.layer;
				System.out.println("cross"+pointArrayList.get(index).layer + " "+point.layer);
			}
			renderMap.put(point.getID(), point);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		Bitmap bitmap = MaxstARUtil.getBitmapFromAsset("Leaders_Cube.png", activity.getAssets());

		texturedCubeRenderer = new TexturedCubeRenderer();
		texturedCubeRenderer.setTextureBitmap(bitmap);

		coloredCubeRenderer = new ColoredCubeRenderer();

		videoRenderer = new VideoRenderer();
		VideoPlayer player = new VideoPlayer(activity);
		videoRenderer.setVideoPlayer(player);
		player.openVideo("VideoSample.mp4");

		chromaKeyVideoRenderer = new ChromaKeyVideoRenderer();
		player = new VideoPlayer(activity);
		chromaKeyVideoRenderer.setVideoPlayer(player);
		player.openVideo("ShutterShock.mp4");

		backgroundRenderHelper = new BackgroundRenderHelper();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		surfaceWidth = width;
		surfaceHeight = height;

		MaxstAR.onSurfaceChanged(width, height);
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

		boolean legoDetected = false;
		boolean blocksDetected = false;

		float[] projectionMatrix = CameraDevice.getInstance().getProjectionMatrix();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		for (int i = 0; i < trackingResult.getCount(); i++) {
			Trackable trackable = trackingResult.getTrackable(i);

			Point point =renderMap.get(trackable.getName());
			//Log.i(TAG, "Image width : " + trackable.getWidth() + ", height : " + trackable.getHeight());

			texturedCubeRenderer.setProjectionMatrix(projectionMatrix);
			texturedCubeRenderer.setTransform(trackable.getPoseMatrix());
			texturedCubeRenderer.setTranslate(0, 0, -trackable.getHeight()*0.25f*0.25f);
			String string=trackable.getName();
			//double angle=Double.isNaN( renderMap.get(trackable.getName()).getAngle())?0:renderMap.get(trackable.getName()).getAngle();
			double angle=point.getAngle();
			texturedCubeRenderer.setRotation(( float )angle,0,0,1);
			//texturedCubeRenderer.setScale(trackable.getWidth()*0.25f, trackable.getHeight()*0.25f, trackable.getHeight()*0.25f*0.5f);
			texturedCubeRenderer.setScale(0.55f, 0.55f, trackable.getHeight()*0.25f*0.5f);
			double distance=point.getDis();
			int cross=point.cross;
			String alertText="";
			if(cross>0)
				alertText+="上"+cross+"层";
			else if(cross<0)
				alertText+="下"+Integer.toString(-cross)+"层";
			else
				alertText="向此方向前进"+distance+"米";
			sendMessage(alertText);

			texturedCubeRenderer.draw();
//			switch (trackable.getName()) {
//				case "Lego":
//					legoDetected = true;
//					if (videoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_READY ||
//							videoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_PAUSE) {
//						videoRenderer.getVideoPlayer().start();
//					}
//					videoRenderer.setProjectionMatrix(projectionMatrix);
//					videoRenderer.setTransform(trackable.getPoseMatrix());
//					videoRenderer.setTranslate(0.0f, 0.0f, 0.0f);
//					videoRenderer.setScale(trackable.getWidth(), trackable.getHeight(), 1.0f);
//					videoRenderer.draw();
//					break;
//
//				case "Blocks":
//					blocksDetected = true;
//					if (chromaKeyVideoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_READY ||
//							chromaKeyVideoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_PAUSE) {
//						chromaKeyVideoRenderer.getVideoPlayer().start();
//					}
//					chromaKeyVideoRenderer.setProjectionMatrix(projectionMatrix);
//					chromaKeyVideoRenderer.setTransform(trackable.getPoseMatrix());
//					chromaKeyVideoRenderer.setTranslate(0.0f, 0.0f, 0.0f);
//					chromaKeyVideoRenderer.setScale(trackable.getWidth(), -trackable.getHeight(), 1.0f);
//					chromaKeyVideoRenderer.draw();
//					break;
//
//				case "Glacier":
//					texturedCubeRenderer.setProjectionMatrix(projectionMatrix);
//					texturedCubeRenderer.setTransform(trackable.getPoseMatrix());
//					texturedCubeRenderer.setTranslate(0, 0, -trackable.getHeight()*0.25f*0.25f);
//					texturedCubeRenderer.setScale(trackable.getWidth()*0.25f, trackable.getHeight()*0.25f, trackable.getHeight()*0.25f*0.5f);
//					texturedCubeRenderer.draw();
//					break;

//				default:
//					coloredCubeRenderer.setProjectionMatrix(projectionMatrix);
//					coloredCubeRenderer.setTransform(trackable.getPoseMatrix());
//					coloredCubeRenderer.setTranslate(0, 0, -trackable.getHeight()*0.25f*0.25f);
//					coloredCubeRenderer.setScale(trackable.getWidth()*0.25f, trackable.getHeight()*0.25f, trackable.getHeight()*0.25f*0.5f);
//					coloredCubeRenderer.draw();
//			}
		}

		if (!legoDetected) {
			if (videoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_PLAYING) {
				videoRenderer.getVideoPlayer().pause();
			}
		}

		if (!blocksDetected) {
			if (chromaKeyVideoRenderer.getVideoPlayer().getState() == VideoPlayer.STATE_PLAYING) {
				chromaKeyVideoRenderer.getVideoPlayer().pause();
			}
		}
	}

	void destroyVideoPlayer() {
		videoRenderer.getVideoPlayer().destroy();
		chromaKeyVideoRenderer.getVideoPlayer().destroy();
	}
	/**
	 * 向主线程UI发送信息
	 */
	private void sendMessage(String messageText){

		Message message;

		Bundle bundle = new Bundle();

		message = ImageTrackerActivity.messageHandler.obtainMessage();

		bundle.putString("name", messageText+"");

		message.setData(bundle);

		ImageTrackerActivity.messageHandler.sendMessage(message);

	}
}
