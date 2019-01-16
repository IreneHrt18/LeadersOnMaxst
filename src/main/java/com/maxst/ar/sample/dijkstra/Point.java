package com.maxst.ar.sample.dijkstra;

import java.io.IOException;

public class Point{

	public String ID;//编号ID=1000*layer+id
	public int id;
	public String name="";
	public double x;
	public double y;
	public double dis;
	public int layer;//楼层
	public double angle;//正北为0，顺时针偏移(0-360)
	public int cross;
	public Point (double x,double y){
		this.x=x;
		this.y=y;
	}
	public String getID(){
		return ID;
	}
	public int getid(){
		return id;
	}
	public String getName(){
		return name;
	}
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getDis(){
		return dis;
	}
	public double getAngle() {
		return angle;
	}

}