import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class PathFinder {
	
	private static int inputHeight;
	private static int inputWidth;
	private static int[][] terrain;
	private static double[][] costMap;
	private static BufferedImage bestRoute;
	
	/* uses Dijkstra's pathfinding algorithm to find most efficient route across terrain
	 * @param terrainImg topographic map of terrain
	 * @param startX x-coordinate of starting location
	 * @param startY y-coordinate of starting location
	 * @param endX x-coordinate of ending location
	 * @param endY y-coordinate of ending location
	 * @return image of terrain with the most efficient path between specified points marked in green
	 */
	public static BufferedImage findBestRoute(BufferedImage terrainImg, int startX, int startY, int endX, int endY) {

		inputHeight = terrainImg.getHeight();
		inputWidth = terrainImg.getWidth();
		terrain = new int[inputHeight][inputWidth]; //stores image values so easier to access
		costMap = new double[inputHeight][inputWidth]; // stores cost
		
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
		queue.add({startY, startX});
		

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
		bestRoute = copyBufferedImage(terrainImg);
		
		// mark best route in green
		bestRoute = markBestRoute(bestRoute);
		
		return bestRoute;
	}

	private static BufferedImage copyBufferedImage(BufferedImage image, int height, int width){
		BufferedImage output = new BufferedImage(height, width, BufferedImage.TYPE_4BYTE_ABGR);

		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				setBWPixel(output, i, j, image.getRGB(j,i));
			}
		}
	}

	private static BufferedImage markBestRoute(BufferedImage image, double[][] costMap, int endX, int endY){
		/*
		given an image, ending coordinates, and a costMap computed from starting coordinates, 
		mark the most cost efficient route from start to end in green
		*/
		
		// traceback path
		int x = endX;
		int y = endY;
		double minCost = costMap[y][x];
		
		// mark ending square
		setGreenPixel(image, y, x, 255);

		while(minCost != 0) {

			// find lowest surrounding cost
			int[] nextCoord = minSurroundingCostCoordinates(costMap, y, x);
			
			// move to new square
			y = nextCoord[0];	
			x = nextCoord[1];
			
			minCost = costMap[y][x];
	
			// mark new square
			setGreenPixel(image, y, x, 255);
		}

		return image;
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
				int testX = x + j;
				int testY = y + i;
				if(outOfBounds(testY, testX)) {
					continue;
				}else if(costMap[testY][testX] < minCost) {
					minY = testY;
					minX = testX;
					minCost = costMap[testY][testX];
				}
			}
		}
		
		return { minY, minX };
		
	}
	
}
