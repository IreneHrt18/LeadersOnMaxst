package com.maxst.ar.sample.dijkstra;

import android.content.Context;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;


import java.math.*;

class Dis {
	String path= "";
	double value= 0;
	boolean visit= false;
}

class Graph_DG {
	private int vexnum;   //图的顶点个数
	private int edge;     //图的边数1
	private Dijkstra dij;
	double arc[][];   //邻接矩阵
	private Dis [] dis;   //记录各个顶点最短路径的信息
	public Point points[]=new Point[2005];
	private double MAX=1e18;
	public Graph_DG(int vexnum, int edge,Dijkstra dij) {
		//初始化顶点数和边数
		this.vexnum = vexnum;
		this.edge = edge;
		this.dij=dij;
		//为邻接矩阵开辟空间和赋初值
		arc = new double[this.vexnum][this.vexnum];
		dis = new Dis[this.vexnum];
		for (int i = 0; i < this.vexnum; i++) {
			dis[i]=new Dis();//amazing???
			arc[i] = new double[this.vexnum];
			for (int k = 0; k < this.vexnum; k++) {
				//邻接矩阵初始化为无穷大
				arc[i][k] = 1e18;
			}
		}
	}
	//创建图
	void createGraph(int layer1,int layer2,Context context) throws IOException{
		Readtxt readtxt=new Readtxt(context);
		readtxt.main(layer1,layer2,this);
	}
	//求最短路径
	void Dijkstra(int begin) {
		//首先初始化我们的dis数组
		int i;
		//设置起点的到起点的路径为0
		dis[begin - 1].value = 0;
		dis[begin - 1].visit = true;
		for (i = 0; i < this.vexnum; i++) {
			//设置当前的路径
			dis[i].path += 'v'+Integer.toString(begin)+'v'+Integer.toString(i+1);

			dis[i].value = arc[begin - 1][i];
		}

		int count = 1;
		//计算剩余的顶点的最短路径（剩余this.vexnum-1个顶点）
		while (count != this.vexnum) {
			//temp用于保存当前dis数组中最小的那个下标
			//min记录的当前的最小值
			int temp=0;
			double min = MAX;
			for (i = 0; i < this.vexnum; i++) {
				if (!dis[i].visit && dis[i].value<min) {
					min = dis[i].value;
					temp = i;
				}
			}
			//把temp对应的顶点加入到已经找到的最短路径的集合中
			dis[temp].visit = true;
			++count;
			for (i = 0; i < this.vexnum; i++) {
				//注意这里的条件arc[temp][i]!=INT_MAX必须加，不然会出现溢出，从而造成程序异常
				if (!dis[i].visit && arc[temp][i]!=MAX && (dis[temp].value + arc[temp][i]) < dis[i].value) {
					//如果新得到的边可以影响其他为访问的顶点，那就就更新它的最短路径和长度
					dis[i].value = dis[temp].value + arc[temp][i];

					dis[i].path = dis[temp].path + 'v'+ Integer.toString(i+1);
				}
			}
		}
	}
	//打印最短路径
	void getPath(int end) {

//    	System.out.println(dis[end-1].path);//
		String pa="";
		int patt[]=new int[2005];
		int p=-1;
		for(int i=0;i<dis[end-1].path.length();i++){
			if(dis[end-1].path.charAt(i)=='v'){
				if(pa!="")
					patt[++p]=Integer.parseInt(pa);
				pa="";
			}
			else{
				pa+=dis[end-1].path.charAt(i);
			}
		}
		patt[++p]=Integer.parseInt(pa);
		pa="";
		for(int i=0;i<=p;i++){
//			if(i<p) {
//				double a=points[patt[i+1]].x -points[patt[i]].x;
//				double b=points[patt[i+1]].y -points[patt[i]].y;
////        		double c=Math.sqrt(a*a+b*b);
//				double c=arc[patt[i]-1][patt[i+1]-1];
//				//double angle=180*Math.acos(b/c)/Math.acos(-1);
//				double r;
//				if(b==c)
//					r=0.5*Math.PI;
//				else
//					r=Math.acos(b/c);
//				double angle=180*r/Math.PI;
//				System.out.println(angle);
//				if(a<0)angle=360-angle;
//				points[patt[i]].angle =angle;
//				points[patt[i]].dis=c;
//			}
//			if(i<p) {
//				double a=points[patt[i+1]].x -points[patt[i]].x;
//				double b=points[patt[i+1]].y -points[patt[i]].y;
////        		double c=Math.sqrt(a*a+b*b);
//				double c=arc[patt[i]-1][patt[i+1]-1];
//				double angle=180*Math.atan(a/b)/Math.acos(-1);
//				if(Math.abs(b-0)<0.01) {
//					if(a>0)angle=90;
//					else angle=270;
//				}
//				else if(b<0)angle+=180;
//				else if(a<0&&b>0)angle+=360;
//				points[patt[i]].angle =angle;
//				points[patt[i]].dis=c;
//			}
			if(i<p) {
				double a=points[patt[i+1]].x -points[patt[i]].x;
				double b=points[patt[i+1]].y -points[patt[i]].y;
//        		double c=Math.sqrt(a*a+b*b);
				double c=arc[patt[i]-1][patt[i+1]-1];
				double angle=180*Math.atan(a/b)/Math.acos(-1);
				if(Math.abs(b-0)<0.01) {
					if(a>0)angle=90;
					else angle=270;
				}
				else if(b>0)angle=180-angle;
				else if(a>=0&&b<0)angle*=(-1);
				else if(a<0&&b<0)angle=360-angle;
				points[patt[i]].angle =angle;
				points[patt[i]].dis=c;
			}


			if(points[patt[i]].layer==dij.layer2&&dij.layer1!=dij.layer2)
				points[patt[i]].id=patt[i]-(dij.vexnum[0]+dij.vexnum[dij.layer1]);
			else if(points[patt[i]].layer==dij.layer1)
				points[patt[i]].id=patt[i]-(dij.vexnum[0]);
			else
				points[patt[i]].id=patt[i];
			dij.myPoints.add(points[patt[i]]);
//		    System.out.println(points[patt[i]].id+" "+points[patt[i]].x+" "+points[patt[i]].y+" 角度"+points[patt[i]].angle+" 距离"+c);
		}

		dij.distance=dis[end-1].value;
//    	System.out.println(points[patt[p]].id+" "+points[patt[p]].x+" "+points[patt[p]].y);
	}

};
public class Dijkstra {

	public Context mContext;

	public Dijkstra(Context context)
	{
		mContext=context;
	}

	//爱琴海数据
//	int vexnum[]=new int[]{19,545,484,566,642,502,296,};
//	int edge[]=new int[]{38,648,624,726,797,619,354,};
//海洋楼数据
	int vexnum[]=new int[]{4,0,0,0,0,0,0,0,0,78,78,78};
	int edge[]=new int[]{8,0,0,0,0,0,0,0,0,78,78,78};
	public int layer1,layer2,start,end;
	double distance;
	public ArrayList<Point> myPoints=new ArrayList<Point>();
	public void setST(int l,int s,int ll,int t) {
		this.layer1=l;
		this.layer2=ll;
		this.start=s;
		this.end=t;
	}
	//从外部调用的主函数
	public ArrayList<Point> getMyPoints() throws IOException {
		int v,e;
		start+=vexnum[0];end+=vexnum[0];
		if(layer1!=layer2){
			end+=vexnum[layer1];
			v=vexnum[0]+vexnum[layer1]+vexnum[layer2];e=edge[0]+edge[layer1]+edge[layer2];
		}
		else{
			v=vexnum[0]+vexnum[layer1];e=edge[0]+edge[layer1];
		}
		Graph_DG graph=new Graph_DG(v,e,this);
		graph.createGraph(layer1,layer2,this.mContext);
		graph.Dijkstra(start);
		graph.getPath(end);

		for(int i=0;i<myPoints.size();i++) {
			myPoints.get(i).ID=Integer.toString(myPoints.get(i).id+myPoints.get(i).layer*1000);
		}
		return myPoints;
	}


}
