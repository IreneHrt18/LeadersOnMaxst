package com.maxst.ar.sample.dijkstra;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;



public class Readtxt {
	//爱琴海数据
//	int vexnum[]=new int[]{19,545,484,566,642,502,296,};
//	int edge[]=new int[]{38,648,624,726,797,619,354,};
//	String layerName[]=new String[]{
//			"2\\0.txt",
//			"2\\F1id.txt",
//			"2\\F2id.txt",
//			"2\\F3id.txt",
//			"2\\F4id.txt",
//			"2\\F5id.txt",
//			"2\\F6id.txt"
//	};
//	String Fid_name[]=new String[]{
//			"",
//			"2\\F1id-name.txt",
//			"2\\F2id-name.txt",
//			"2\\F3id-name.txt",
//			"2\\F4id-name.txt",
//			"2\\F5id-name.txt",
//			"2\\F6id-name.txt"
//	};
//	//处理数据用
////	String Fname[]=new String[]{
////			"",
////			"2\\F1name.txt",
////			"2\\F2name.txt",
////			"2\\F3name.txt",
////			"2\\F4name.txt",
////			"2\\F5name.txt",
////			"2\\F6name.txt"
////	};
//海洋楼数据
	int vexnum[]=new int[]{4,0,0,0,0,0,0,0,0,78,78,78};
	int edge[]=new int[]{8,0,0,0,0,0,0,0,0,78,78,78};
	String layerName[]=new String[]{
			"2/00.txt",
			"","","","","","","","",
			"2/fid9-11.txt",
			"2/fid9-11.txt",
			"2/fid9-11.txt"
	};
	String Fid_name[]=new String[]{
			"","","","","","","","","",
			"2/f9id-name.txt",
			"2/f10id-name.txt",
			"2/f11id-name.txt",
	};
	//处理数据用
	String Fname[]=new String[]{
			"","","","","","","","","",
			"2/f9name.txt",
			"2/f10name.txt",
			"2/f11name.txt"
	};

	private Context mContext;

	Readtxt(Context context){
		mContext=context;
	}

	public void main(int layer1,int layer2,Graph_DG g) throws IOException {
		readLayer(layer1,0,g);
		readStair(layer1,g);
		readName(layer1,0,g);
//		getId_name(l, g);//
		if(layer1!=layer2) {//读取第二层(layer2
			readLayer(layer2, vexnum[layer1],g);
			readName(layer2,vexnum[layer1], g);
			for(int id1=1;id1<=vexnum[0];id1++)
			{
				double min=0xffffff;
				int minid = 0;
				for(int i=vexnum[0]+vexnum[layer1]+1;i<=vexnum[0]+vexnum[layer1]+vexnum[layer2];i++){
					double tem=(g.points[id1].x-g.points[i].x)*(g.points[id1].x-g.points[i].x)+(g.points[id1].y-g.points[i].y)*(g.points[id1].y-g.points[i].y);
					if(tem<min){
						min=tem;
						minid=i;
					}
				}
				//设置楼梯距离
				g.arc[id1-1][minid-1]=0;
				g.arc[minid-1][id1-1]=0;
//				g.arc[id1-1][minid-1]=1000;//Math.sqrt(min);
//				g.arc[minid-1][id1-1]=1000;//Math.sqrt(min);
			}
		}
	}
	//读取文件用
	public ArrayList<String> getList(String txtName) throws IOException{
		ArrayList<String> list=new ArrayList<String>();

		InputStream file=mContext.getResources().getAssets().open(txtName);

//		FileInputStream file=new FileInputStream(txtName);
		InputStreamReader input=new InputStreamReader(file);
		BufferedReader buf=new BufferedReader(input);

		String values=buf.readLine();
		while(values!=null)
		{
			list.add(values);
			values=buf.readLine();
		}
		buf.close();
		return list;
	}
	//读取一层数据
	public void readLayer(int layer,int vel,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		list=getList(layerName[layer]);
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			String values=list.get(i);
			String []value=values.split(" ");
			int id1=Integer.parseInt(value[0])+vexnum[0]+vel;
			double x1=Double.parseDouble(value[1]);
			double y1=Double.parseDouble(value[2]);
			int id2=Integer.parseInt(value[3])+vexnum[0]+vel;
			double x2=Double.parseDouble(value[4]);
			double y2=Double.parseDouble(value[5]);
			double dis=Double.parseDouble(value[6]);
			g.points[id1]=new Point(x1, y1);
			g.points[id2]=new Point(x2, y2);
			g.points[id1].layer=layer;
			g.points[id2].layer=layer;
			g.arc[id1-1][id2-1]=dis;
			g.arc[id2-1][id1-1]=dis;

//		    System.out.println(id1+" "+x1+" "+y1+" "+dis);//
		}
	}
	//读取楼梯并连接
	public void readStair(int layer,Graph_DG g) throws IOException {//stair floor
		ArrayList<String> list=new ArrayList<String>();
		list=getList("2/00.txt");
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			String values=list.get(i);
			String []value=values.split(" ");
			double x1=Double.parseDouble(value[0]);
			double y1=Double.parseDouble(value[1]);
			int id1=Integer.parseInt(value[2]);
			g.points[id1]=new Point(x1, y1);
			g.points[id1].name=value[3];
			g.points[id1].layer=0;

			double min=0xffffff;
			int minid = 0;
			for(int i1=vexnum[0]+1;i1<=vexnum[0]+vexnum[layer];i1++){
				double tem=(g.points[id1].x-g.points[i1].x)*(g.points[id1].x-g.points[i1].x)+(g.points[id1].y-g.points[i1].y)*(g.points[id1].y-g.points[i1].y);
				if(tem<min){
					min=tem;
					minid=i1;
				}
			}
			//设置楼梯距离
			g.arc[id1-1][minid-1]=0;
			g.arc[minid-1][id1-1]=0;
//			g.arc[id1-1][minid-1]=1000;//Math.sqrt(min);
//			g.arc[minid-1][id1-1]=1000;//Math.sqrt(min);
//		    System.out.println(id1+" "+x1+" "+y1+" "+value[3]);
		}
	}
	//读取点的名称
	public void readName(int layer,int vel,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		list=getList(Fid_name[layer]);
		int size=list.size();
		for(int i=0;i<size;i++)
		{
			String values=list.get(i);
			String []value=values.split(" ");
			int id=Integer.parseInt(value[0]);
			String name=value[1];
			g.points[id+vexnum[0]+vel].name=name;
		}
	}

	//获得id-name一一对应//处理数据用
	public void getId_name(int layer,Graph_DG g) throws IOException {
		ArrayList<String> list=new ArrayList<String>();
		list=getList(Fname[layer]);

		//output
		File file = new File("2/f"+layer+"id-name.txt");
		if(!file.getParentFile().exists()){ //如果文件的目录不存在
			file.getParentFile().mkdirs(); //创建目录
		}
		OutputStream output = new FileOutputStream(file);

		int size=list.size();
		for(int i=0;i<size;i++)
		{
			String values=list.get(i);
			String []value=values.split(" ");
			String name=value[0];
			double x1=Double.parseDouble(value[1]);
			double y1=Double.parseDouble(value[2]);

			double min=0xffffff;
			int minid = 0;
			for(int i1=vexnum[0]+1;i1<=vexnum[0]+vexnum[layer];i1++){
				double tem=(x1-g.points[i1].x)*(x1-g.points[i1].x)+(y1-g.points[i1].y)*(y1-g.points[i1].y);
				if(tem<min){
					min=tem;
					minid=i1;
				}
			}
//			g.points[minid].name=name;

//			//输出文件id-name.txt
			String str=minid-vexnum[0]+" "+name+"\n";
			byte data[] = str.getBytes();
			output.write(data);

//		    System.out.println(minid+" "+x1+" "+y1+" "+name);
		}
		output.close();
	}

}
