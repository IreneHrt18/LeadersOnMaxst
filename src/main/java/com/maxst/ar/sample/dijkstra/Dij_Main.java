package com.maxst.ar.sample.dijkstra;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Dij_Main {

	Context mContext;

	private int folder=2;

	private int startLayer;

	private int endLayer;

	public Dij_Main(Context context){
		mContext=context;
	}

	public int getStartLayer()
	{
		return startLayer;
	}

	public int getEndLayer()
	{
		return endLayer;
	}

	public  ArrayList<Point> getMyList(String ID,String tname) throws IOException{
		
		ArrayList<Point> myList=new ArrayList<Point>();
		Dijkstra my=new Dijkstra(this.mContext);
		//start 是字符串得到的，但是end是读txt得到的
		int layer1=Integer.parseInt(ID)/1000,start=Integer.parseInt(ID)%1000;
		int layer2=0,end=0;
		int fg=0;/////
		for(int layer=9;layer<=11;layer++) {//
			ArrayList<String> list=new ArrayList<String>();
			InputStream file=mContext.getResources().getAssets().open(Integer.toString(folder)+"/f"+layer+"id-name.txt");
//			FileInputStream file=new FileInputStream("my\\f"+layer+"id-name.txt");
			InputStreamReader input=new InputStreamReader(file);
			BufferedReader buf=new BufferedReader(input);
		    
			String values=buf.readLine();
			    while(values!=null)
			    {
			         list.add(values);
			    	 values=buf.readLine();   
			    }
			   buf.close();
		        
			int size=list.size();
			for(int i=0;i<size;i++)
			{
				values=list.get(i);
			    String []value=values.split(" ");
			    int id=Integer.parseInt(value[0]);
			    String name=value[1];
			    if(name.equals(tname)) {
//			System.out.println(lc+" "+id);c
			    	layer2=layer;end=id;
			    	fg=1;break;
			    }
			}
			if(fg==1)break;
		}/////

		startLayer=layer1;
		endLayer=layer2;

		my.setST(layer1,start,layer2,end);
		System.out.println("起点楼层"+layer1+"  起点"+start+" 终点楼层"+layer2+" 终点"+end);
		
	    myList=my.getMyPoints();
	    for(int i=0;i<myList.size();i++) {
	    	if(myList.get(i).layer!=0)
	    	System.out.println(myList.get(i).layer+" "+myList.get(i).id+" "+myList.get(i).ID+" "+myList.get(i).x+" " +
					""+myList.get(i).y+" name:"+myList.get(i).name+" angle:"+myList.get(i).angle+" distance:"+myList.get(i).dis+" layer:"+myList.get(i).layer);
	    }
		return myList;

	}
	
}
