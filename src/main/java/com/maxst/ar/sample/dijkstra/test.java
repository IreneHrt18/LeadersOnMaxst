package com.maxst.ar.sample.dijkstra;

import java.io.IOException;
import java.util.ArrayList;

public class test {

	
public static void main(String[] args) throws IOException {
		
		ArrayList<Point> myList=new ArrayList<Point>();
		String ID="11001";//输入ID编号（范围：9001-9078,10001-10078,11001-11078)
		String tname="1116";//输入终点名称（范围：0901-0978,1001-1019,1101-1119,电梯1,电梯2, 楼梯1,楼梯2, 卫生间1,卫生间2)

	Dij_Main test=new Dij_Main(null);
		myList=test.getMyList(ID,tname);
	    
	}
}
