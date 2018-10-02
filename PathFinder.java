	import java.awt.image.BufferedImage;
	import java.io.*;
	import java.util.ArrayList;
	import java.util.Arrays;
	import javax.imageio.ImageIO;
	import java.util.Scanner;

	//input: input.bmp terrain
	//input: cities.bmp
	//input: cities.txt
	//output: output.png

public class PathFinder {
		public static double[][] cost= new double[4096][4096];;
		public static BufferedImage output = new BufferedImage(4096, 4096, BufferedImage.TYPE_4BYTE_ABGR);
		public static int roadCount=0;
		
		
		
		public static void main(String args[]) throws IOException{
		
			
			
			
			
			int searchRange=Integer.parseInt(args[0]);
			long start = System.nanoTime();
			
			//top left corner (check)
			int myCityX=700;
			int myCityY=2600;
			
			
			
			BufferedImage image = ImageIO.read(new File("terrain.bmp"));
			
			//import terrain array
			//initialize cost arrays
			//set unusable to neg max value
			//set unsearched to max
			int[][] terrain = new int[4096][4096];
			for(int x=0;x<4096;x++){
				for(int y=0;y<4096;y++){
					terrain[x][y]=Integer.valueOf(Integer.toBinaryString(image.getRGB(x,y)).substring(24),2);
					if(Integer.valueOf(Integer.toBinaryString(cityImage.getRGB(x,y)).substring(24),2).intValue() == 255){
						//for unusable
						cost[x][y]=-Double.MAX_VALUE;
					}else{
						//for unsearched
						cost[x][y]=Double.MAX_VALUE;
					}
				}
			}
			
			System.out.println("cost array initialized");
			
			//cost[] good here
			
			ArrayList<Integer> stackX = new ArrayList<Integer>();
			ArrayList<Integer> stackY = new ArrayList<Integer>();
			
			
			//put edge of city in stack, update cost array, and set inside of city to unusable
			for(int x=myCityX;x<myCityX+250;x++){
				for(int y=myCityY;y<myCityY+250;y++){
					if(y==myCityY||y==myCityY+249||x==myCityX||x==myCityX+249){
						cost[x][y]=Double.valueOf(0);
						stackX.add(x);
						stackY.add(y);
					}
				}
			}
			
			System.out.println("computing cost array");
			
			//compute cost array
			while(stackX.size()>0){
				int x = stackX.get(0);
				int y = stackY.get(0);
				for(int i=-1;i<2;i++){
					for(int j=-1;j<2;j++){
						if(outOfBounds(x, y, i, j)){continue;}
						double calCost=Double.valueOf((Math.pow((i*i+j*j), 0.5))+Math.pow((terrain[x][y]-terrain[x+i][y+j]), 2));
						double newCost=cost[x][y]+calCost;
						if(cost[x+i][y+j]>newCost&&(Math.abs(x+i-700)<searchRange)&&(Math.abs(y+j-2600)<searchRange)){
							cost[x+i][y+j]=newCost;
							stackX.add(x+i);
							stackY.add(y+j);
						}
					}
				}
				stackX.remove(0);
				stackY.remove(0);
				
				/*int stackSize=stackX.size();
				if(stackSize%1000==0){
					System.out.println(stackSize);
				}
				*/
			}
			
			System.out.println("cost array done!");

			
			
			
			//good
			
			//initialize output image
			for(int i=0;i<4096;i++){
				for(int j=0;j<4096;j++){
					String tempBinary = String.format("%8s", Integer.toBinaryString(0)).replace(' ', '0');
					output.setRGB(i, j, (int)Long.parseLong("11111111"+tempBinary+tempBinary+tempBinary,2));///turn greyscale values to ARGB
				}
			}
			
			System.out.println("about to build...");
			
			//check that it picks three lowest costs///////////////////////////////////////////////////////////
			//try to build roads to city
			Object[] costCopy = toCityCost.toArray();
			Arrays.sort(costCopy);
			
			
			for(int i=0;i<toCityX.size();i++){
				int index=toCityCost.indexOf(costCopy[i]);
				
				//passes coordinates of toCity and cost[][] array
				buildRoad(toCityX.get(index),toCityY.get(index), cost, index);
				
			
				if(roadCount==3){break;}
			}
			
			
			//read out image
			File outputfile = new File("output.png");
		    ImageIO.write(output, "png", outputfile);
		    System.out.println("total time: "+(System.nanoTime()-start)/1000000000+" s");
		}
		
		
		public static void buildRoad(int x, int y, double[][] cost, int index){
			
			
			double minCost=toCityCost.get(index);
			
			while(cost[x][y]!=0){
				int minI=0;
				int minJ=0;
				
				//find lowest surrounding cost
				for(int i=-1;i<2;i++){
					for(int j=-1;j<2;j++){
						if(outOfBounds(x, y, i, j)){
							continue;
						}else if(cost[x+i][y+j]>=0 && cost[x+i][y+j]<minCost){
							minCost=cost[x+i][y+j];
							minI=i;
							minJ=j;
						}
					}
				}
				
				//mark current square
				String tempBinary = String.format("%8s", Integer.toBinaryString(255)).replace(' ', '0');
				output.setRGB(x, y, (int)Long.parseLong("11111111"+tempBinary+tempBinary+tempBinary,2));///turn greyscale values to ARGB
				
				//move to new square
				x+=minI;
				y+=minJ;
				
			}
			
			System.out.println((roadCount+1)+" printed! for "+toCityCost.get(index));
			roadCount++;
			
		}
		
		public static boolean outOfBounds(int x, int y, int i, int j){
			if((i==0&&j==0)||x+i>4095||x+i<0||y+j<0||y+j>4095||x>4095||x<0||y<0||y>4095){
				return true;
			}else{ 
				return false;
			}
		}
		
		public double computeCost(int startX, int startY, int endX, int endY) {
			
			//find most efficient path
			double minCost=Double.MAX_VALUE;
			
			for(int i=toX-1;i<toX+251;i++){
				for(int j=toY-1;j<toY+251;j++){
					if(j==toY-1||j==toY+250||i==toX-1||i==toX+250){
						if(cost[i][j]>=0 && cost[i][j]<minCost){
							minCost=cost[i][j];
							toCityX.set(h, i);
							toCityY.set(h, j);
						}
					}
				}
			}		
			
		}
	/*
	read in image
	convert to array

	decide where to build

		make cost array from city's edge while checking for obstructions
	                    create 2d array cost and arraylist stack
	                    add all px on city edge to stack and set values in cost array to 0
	                    set all other costs to infinity
	                    check surroundings, calculate value,
	                    add surrounding px to stack if not obstructions or edges, remove tested px from stack 
	                    repeat until no more in stack

		find lowest cost for each city 
	                    use known city loc to get city edge 
	                    compare cost on edge of city

		pick three lowest costs
			
	build roads to chosen cities
	read out image
	*/
}
