import java.awt.image.BufferedImage;
import java.util.LinkedList;

//input: input.bmp terrain
//output: output.png

/*
 * read in image convert to array
 * 
 * decide where to build
 * 
 * make cost array from city's edge while checking for obstructions create 2d
 * array cost and arraylist stack add all px on city edge to stack and set
 * values in cost array to 0 set all other costs to infinity check surroundings,
 * calculate value, add surrounding px to stack if not obstructions or edges,
 * remove tested px from stack repeat until no more in stack
 * 
 * find lowest cost for each city use known city loc to get city edge compare
 * cost on edge of city
 * 
 * pick three lowest costs
 * 
 * build roads to chosen cities read out image
 */

public class PathFinder {
	
	private static int inputHeight;
	private static int inputWidth;
	private static int[][] terrain;
	private static double[][] costMap;
	private static BufferedImage bestRoute;
	
	
	public static BufferedImage findBestRoute(BufferedImage terrainImg, int startX, int startY, int endX, int endY) {

		inputHeight = terrainImg.getHeight();
		inputWidth = terrainImg.getWidth();
		terrain = new int[inputHeight][inputWidth]; //stores image values so easier to access
		costMap = new double[inputHeight][inputWidth]; // stores cost
		bestRoute = new BufferedImage(inputHeight, inputWidth, BufferedImage.TYPE_4BYTE_ABGR);
		
		// sets costMap entries to Double.MAX_VALUE and copies terrainImg to terrain[][]
		for (int x = 0; x < inputWidth; x++) {
			for (int y = 0; y < inputHeight; y++) {
				terrain[y][x] = Integer.valueOf(Integer.toBinaryString(terrainImg.getRGB(y, x)).substring(24), 2);
				costMap[y][x] = Double.MAX_VALUE;
			}
		}
		
		// initialize queue where queue[0] is y (vertical) coordinate and queue[1] is x (horizontal) coordinate
		LinkedList<Integer[]> queue = new LinkedList<Integer[]>();

		// add starting x and y to stack
		Integer[] startingCoordinates = {startY, startX};
		queue.add(startingCoordinates);
		

		// compute cost array
		while(queue.size() > 0) {
			
			Integer[] coordinate = queue.remove();
			int y = coordinate[0];
			int x = coordinate[1];
			
			for(int i = -1; i < 2; i++) {
				
				for(int j = -1; j < 2; j++) {
					
					if(outOfBounds(y + i, x + j) || (i == 0 && j == 0)) {
						continue;
					}
					
					double calCost = computeCostOfMovement(y, x, i, j);
					double newCost = costMap[y][x] + calCost;
					
					if (costMap[y + i][x + j] > newCost) {
						costMap[y + i][x + j] = newCost;
						Integer[] newCordinate = {y + i, x + j};
						queue.add(newCordinate);
					}
					
				}
				
			}
			
		}
		
		
		
		//copy terrainImg to bestRoute
		for(int i = 0; i < inputHeight; i++) {
			for(int j = 0; j < inputWidth; j++) {
				setBWPixel(bestRoute, i, j, terrainImg.getRGB(j,i));
			}
		}
		
		
		// traceback path and mark on bestRoute image
		int x = endX;
		int y = endY;
		double minCost = costMap[y][x];
		
		// mark ending square
		setGreenPixel(bestRoute, y, x, 255);

		while(minCost != 0) {

			// find lowest surrounding cost
			int[] nextCoord = minSurroundingCostCoordinates(costMap, y, x);
			
			// move to new square
			y = nextCoord[0];	
			x = nextCoord[1];
			
			minCost = costMap[y][x];
	
			// mark new square
			setGreenPixel(bestRoute, y, x, 255);
		}
		
		
		return bestRoute;
	}

	
	// checks if coordinate plus adjustment is out of bounds 
	private static boolean outOfBounds(int y, int x) {
		if (y < 0 || x < 0 || y > inputHeight || x > inputWidth) {
			return true;
		} else {
			return false;
		}
	}

	// compute the cost to move from (y, x) to (y + i, x + j)
	private static double computeCostOfMovement(int y, int x, int i, int j) {

		return Double.valueOf((Math.pow((i * i + j * j), 0.5)) + Math.pow((terrain[y][x] - terrain[y + i][x + j]), 2));
		
	}

	// sets specified pixel in specified image to a grayscale value
	private static void setBWPixel(BufferedImage img, int y, int x, int value) {
		String tempBinary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
		img.setRGB(y, x, (int) Long.parseLong("11111111" + tempBinary + tempBinary + tempBinary, 2));
	}
	
	// sets specified pixel in specified image to green with specified intensity value
	private static void setGreenPixel(BufferedImage img, int y, int x, int value) {
		String tempBinary = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
		img.setRGB(x, y, (int) Long.parseLong("11111111" + "00000000" + tempBinary + "00000000", 2));/// turn
	}
	
	// returns the coordinates of the lowest cost around (x, y) in costMap
	private static int[] minSurroundingCostCoordinates(double[][] costMap, int y, int x) {
		
		int minX = 0;
		int minY = 0;
		double minCost = Double.MAX_VALUE;
		
		// find lowest surrounding cost
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if(outOfBounds(y + i, x + j)) {
					continue;
				}else if(costMap[y + i][x + j] < minCost) {
					minY = y + i;
					minX = x + j;
					minCost = costMap[y + i][x + j];
				}
			}
		}
		
		int[] lowestCostCoordinates = {minY, minX};
		
		return lowestCostCoordinates;
	}
	
}
