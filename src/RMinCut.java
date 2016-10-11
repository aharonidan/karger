public class RMinCut {
	
	static int originalNumOfVertices, currentNumOfVertices;
	static Integer[] originalVertices;
	static Integer[][] currentVertices;
	static int[][] originalAdjacencyMatrix, currentAdjacencyMatrix;
	
	static int numberOfTimesToRun;
	static Integer[][] currentCutFound;
	static Integer sizeOfCurrentCutFound = Integer.MAX_VALUE;
	
	public static void main(String[] args) {

		initialize(args);
		
		for(int i=0; i < numberOfTimesToRun; i++){
			int cutSize = karger();
			if (cutSize < sizeOfCurrentCutFound){
				sizeOfCurrentCutFound = cutSize;
				currentCutFound = currentVertices;
			}
		}	
		printResults();
	}
	
	private static int karger() {
		initAdjacencyMatrixAndVertices();
		int vIndex = 0;
		
		while(currentNumOfVertices > 2){
			vIndex = chooseRandomVertex();
			int uIndex = chooseRandomNeighbor(vIndex);
			contract(vIndex,uIndex);
		}
		return countNumberOfEdges(vIndex);
	}

	private static void initialize(String[] args){
		originalVertices = new Integer[args.length];
		originalNumOfVertices = 0;
		
		// counting the number of vertices and initializing originalVertices[] 
		// using (args.length/2)*2 to ignore last element if there are odd number of elements
		for(int i=0; i < (args.length/2)*2; i++){
			for(int j=0; j < originalVertices.length; j++){
				if (originalVertices[j] == null){
					originalVertices[j] = Integer.parseInt(args[i]);
					originalNumOfVertices++;
					break;
				}
				else if (Integer.parseInt(args[i]) == originalVertices[j]){
					break;
				}
			}
		}
		
		originalAdjacencyMatrix = new int[originalNumOfVertices][originalNumOfVertices];
		currentAdjacencyMatrix = new int[originalNumOfVertices][originalNumOfVertices];
		// initializing originalAdjacencyMatrix[][]
		// using (args.length/2)*2 to ignore last element if there are odd number of elements
		for(int i=0; i < (args.length/2)*2; i++){
			int vIndex = vertexIndex(Integer.parseInt(args[i]));
			int uIndex = vertexIndex(Integer.parseInt(args[++i]));
			originalAdjacencyMatrix[vIndex][uIndex] = 1;
			originalAdjacencyMatrix[uIndex][vIndex] = 1;
		}
		// calculating numberOfTimesToRun to achieve an error probability at most 0.01
		// the formula is (log(1/delta)*n^2)/2, (adding 1 to round up).
		numberOfTimesToRun = (int) ((Math.log(1/0.01) * originalNumOfVertices * originalNumOfVertices)/2 + 1);
	}
	
	private static void initAdjacencyMatrixAndVertices() {
		currentVertices = new Integer[originalNumOfVertices][originalNumOfVertices];
		for(int i=0; i < originalNumOfVertices; i++)
			currentVertices[i][0]= originalVertices[i];
		
		for(int i=0; i < originalNumOfVertices; i++)
			currentAdjacencyMatrix[i] = originalAdjacencyMatrix[i].clone();
		
		currentNumOfVertices = originalNumOfVertices;
	}

	private static void contract(int vIndex, int uIndex) {
		// find the next available index in vertices[vIndex]
		int i = 0; 
		while (currentVertices[vIndex][i] != null)
			i++;
		
		// move all labels from u to v and mark u with nulls; 
		int j=0;
		while (currentVertices[uIndex][j] != null){
			currentVertices[vIndex][i] = currentVertices[uIndex][j];
			currentVertices[uIndex][j] = null;
			i++;
			j++;
		}
		// move all edges from u to v
		for(int k=0; k < originalNumOfVertices; k++){
			currentAdjacencyMatrix[vIndex][k] += currentAdjacencyMatrix[uIndex][k]; //move edges to v
			currentAdjacencyMatrix[k][vIndex] += currentAdjacencyMatrix[uIndex][k]; 
			currentAdjacencyMatrix[uIndex][k] = 0; //remove edges from u
			currentAdjacencyMatrix[k][uIndex] = 0;
		}
		
		// remove self loops
		for(int k=0; currentVertices[vIndex][k]!= null; k++){	
			int index = vertexIndex(currentVertices[vIndex][k]);
			currentAdjacencyMatrix[vIndex][index] = 0;
			currentAdjacencyMatrix[index][vIndex] = 0;
		}
		
		currentNumOfVertices--;
	}
	
	private static int chooseRandomVertex() {
		int random = (int)(Math.random() * currentNumOfVertices);
		int counter = -1;
		int i;
		// looping until we get to the random chosen vertex
		for(i=0; counter < random; i++){
				if (currentVertices[i][0] != null) //counting only vertices that are not null 
					counter++;
		}
		return i-1;
	}
	
	private static int chooseRandomNeighbor(int vIndex) {
				
		int random = (int)(Math.random() * countNumberOfEdges(vIndex));
		int counter = -1;
		int i;
		
		//Looping until we get to the selected edge 
		for(i=0; counter < random; i++)
			counter+= currentAdjacencyMatrix[vIndex][i];

		return i-1;
	}

	private static int countNumberOfEdges(int index) {
		int numOfEdges = 0;
		for(int i=0; i<originalNumOfVertices; i++)
			numOfEdges += currentAdjacencyMatrix[index][i];
			
		return numOfEdges;
	}

	private static int vertexIndex(int label) {
		int i=0;
		while(originalVertices[i] != label)
			i++;
		
		return i;	
	}
	
	private static void printResults() {
		System.out.println("The size of the minimum cut found is: " + sizeOfCurrentCutFound);
		System.out.println("The set of vertices on each side of the cut are:");
		int i=0;
		while(currentCutFound[i][0] == null)
			i++;
		
		printArray(currentCutFound[i++]);
		
		while(currentCutFound[i][0] == null)
			i++;
		
		printArray(currentCutFound[i]);
	}
	
	private static void printArray(Integer[] toPrint) {
		for(int i=0; toPrint[i]!= null; i++)
			System.out.print(toPrint[i] + " ");
		
		System.out.println();
	}
}
