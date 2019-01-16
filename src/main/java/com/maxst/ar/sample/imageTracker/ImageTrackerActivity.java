/*
 * Copyright 2017 Maxst, Inc. All Rights Reserved.
 */

package com.maxst.ar.sample.imageTracker;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.maxst.ar.CameraDevice;
import com.maxst.ar.MaxstAR;
import com.maxst.ar.ResultCode;
import com.maxst.ar.TrackerManager;
import com.maxst.ar.sample.ARActivity;
import com.maxst.ar.sample.M_main_activity;
import com.maxst.ar.sample.R;
import com.maxst.ar.sample.dijkstra.Dij_Main;
import com.maxst.ar.sample.dijkstra.Point;
import com.maxst.ar.sample.util.SampleUtil;

import java.io.IOException;
import java.util.ArrayList;

public class ImageTrackerActivity extends ARActivity implements View.OnClickListener {

	private ImageTrackerRenderer imageTargetRenderer;
	private GLSurfaceView glSurfaceView;
	private TextView mesView;
	private int preferCameraResolution = 0;
	/**
	 * 响应信息模块
	 */
	public static MessageHandler messageHandler;

	class MessageHandler extends Handler {


		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle=msg.getData();
			String message=bundle.getString("name");
			ImageTrackerActivity.this.mesView.setText(message);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		messageHandler =new MessageHandler();
//
		String startID=getIntent().getStringExtra("startID");
		String destination=getIntent().getStringExtra("destination");
//		String startID="9001";
//		String destination="1104";
		getDijkstraResults(startID,destination);

		setContentView(R.layout.activity_image_tracker);
		mesView=findViewById(R.id.mesView);
		TrackerManager.getInstance().setTrackingOption(TrackerManager.TrackingOption.NORMAL_TRACKING);
//		findViewById(R.id.normal_tracking).setOnClickListener(this);
//		findViewById(R.id.extended_tracking).setOnClickListener(this);
//		findViewById(R.id.multi_tracking).setOnClickListener(this);

		ArrayList<Point> points=getDijkstraResults(startID,destination);
		imageTargetRenderer = new ImageTrackerRenderer(this,points);


		glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface_view);
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setRenderer(imageTargetRenderer);


		addLeadersTrackerData (points);

		TrackerManager.getInstance().addTrackerData("ImageTarget/Blocks.2dmap", true);
		TrackerManager.getInstance().addTrackerData("ImageTarget/Glacier.2dmap", true);
		TrackerManager.getInstance().addTrackerData("ImageTarget/Lego.2dmap", true);
		//TrackerManager.getInstance().addTrackerData("{\"image\":\"add_image\",\"image_path\":\"ImageTarget/Blocks.png\",\"image_width\":0.26}", true);
		//TrackerManager.getInstance().addTrackerData("{\"image\":\"add_image\",\"image_path\":\"ImageTarget/Glacier.png\",\"image_width\":0.26}", true);
		//TrackerManager.getInstance().addTrackerData("{\"image\":\"add_image\",\"image_path\":\"/sdcard/Download/sample/Blocks.png\",\"image_width\":0.26}", false);
		//TrackerManager.getInstance().addTrackerData("{\"image\":\"add_image\",\"image_path\":\"/sdcard/Download/sample/Glacier.png\",\"image_width\":0.26}", false);
		TrackerManager.getInstance().loadTrackerData();

		preferCameraResolution = getSharedPreferences(SampleUtil.PREF_NAME, Activity.MODE_PRIVATE).getInt(SampleUtil.PREF_KEY_CAM_RESOLUTION, 0);
	}

	public ArrayList<Point> getDijkstraResults(String startID,String destination)
	{
		Dij_Main dij_main=new Dij_Main(ImageTrackerActivity.this);
		ArrayList<Point> points=null;
		try {
			points=dij_main.getMyList(startID,destination);
		} catch (IOException e) {
			e.printStackTrace();
		}
        for (int i=0;i<points.size();i++) {

			if(points.get(i).layer==0) {
				points.remove(i);
				i--;
			}
			//System.out.println("GetMyPoints:"+point.getID()+" x:"+point.getX()+" y:"+point.getY()+" " + point.getAngle() + " " + point.getDis()+" "+point.layer);
			//System.out.println("dijk:"+point.getX() + " " + point.getY() + " " + point.getAngle() + " " + point.getDis());
        }

		return points;
	}
	private void addLeadersTrackerData(ArrayList<Point> points){
		for (Point point :
				points) {
			//substring 从0开始，不包括endindex
			String pointID=point .getID();
			if(pointID.length()<4)
				continue;
			String folder=pointID.substring(0,pointID.length()-3);
			if(folder.length()<2)
				folder="0"+folder;
			//String index=pointID.substring(pointID.length()-3);

			TrackerManager.getInstance().addTrackerData("LC"+folder+"/"+pointID+".2dmap", true);
			System.out.println("加载："+"LC"+folder+"/"+pointID);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		glSurfaceView.onResume();
		TrackerManager.getInstance().startTracker(TrackerManager.TRACKER_TYPE_IMAGE);

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

		glSurfaceView.queueEvent(new Runnable() {
			@Override
			public void run() {
				imageTargetRenderer.destroyVideoPlayer();
			}
		});

		glSurfaceView.onPause();

		TrackerManager.getInstance().stopTracker();
		CameraDevice.getInstance().stop();
		MaxstAR.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.normal_tracking:
				TrackerManager.getInstance().setTrackingOption(TrackerManager.TrackingOption.NORMAL_TRACKING);
				break;

			case R.id.extended_tracking:
				TrackerManager.getInstance().setTrackingOption(TrackerManager.TrackingOption.EXTENDED_TRACKING);
				break;

			case R.id.multi_tracking:
				TrackerManager.getInstance().setTrackingOption(TrackerManager.TrackingOption.MULTI_TRACKING);
				break;
		}
	}
}
