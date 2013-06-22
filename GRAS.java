import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class GRAS {


	private static int[] landaValues = {3};
	private static int landa; //the minimum length of "word - suffix"; 
	//in the paper it says that the average word length of the language is a good guess;
	// the average length of Sorani words  is 5.6; for Kurmanji it is 4.8.

	private static int[] alphaValues = {2};
	private static int alpha; // the minimum occurrence of a pair of suffixes to consider them  'frequent'; 
	// in the paper it proposes to use  4.

	private static float[] gammaValues = {0.9F};
	private static float gamma;  // this is the minimum value of a cluster's cohesion (which measure how well-connected the cluster is);
	// in the paper it is said that it should be between 0.5 and 1.0;
	// the proposed value is 0.8.

	private static String lexiconFilePath = "lexicons/sorani-atenth-shahin.txt"; //this is the address to the lexicon (a.k.a 'terms vector');
																		//IMPORTANT: the lexicon must be alphabetically sorted first!
	
	private static String outputFilePath;// the output file which will contain the stems and their corresponding cluster
	
	
	private static PrintWriter output;
	
	private static HashMap<String,Integer> suffixPairMap = new HashMap<String,Integer>();
	private static HashMap<String,Integer> nodePairMap;

	public static void main(String[] args) throws FileNotFoundException {

		for(int i = 0; i < landaValues.length; i++){
			landa = landaValues[i];
			for(int j = 0; j < alphaValues.length; j++){
				alpha = alphaValues[j];
				for(int k = 0; k < gammaValues.length; k++){
					gamma = gammaValues[k];
					
					outputFilePath = "stems/stems_landa="+landa+"_alpha="+alpha+"_gamma="+gamma+".txt";

					computeFrequentSuffixPairs();
					
					//System.out.println(suffixPairMap.size());

					formTheClasses();
					
				}
			}
		}

	}

	// it makes a pass over the lexicon and computes the frequency of the different suffix pairs.
	// the results are saved in a file.
	private static void computeFrequentSuffixPairs() throws FileNotFoundException{

		Scanner scan = new Scanner (new File(lexiconFilePath));

		ArrayList<String> array = new ArrayList<String>();
		String prefix = "";
		while (scan.hasNextLine()){
			String term = scan.nextLine().trim();
			if(term.length() >= landa){
				if (array.size() == 0){
					prefix = term.substring(0,landa);
					array.add(term);
				}else{
					if (array.indexOf(term) != -1){
						//System.out.println("repeated===========================================: "+term);
						continue;
					}
					String currentPrefix = term.substring(0,landa);
					if (prefix.equals(currentPrefix)){
						array.add(term);
					}else{
						//System.out.println("======================== next group ==========================");
						for(int i = 0; i < array.size(); i++){
							for(int j = i+1 ; j < array.size(); j++){
								String lcs = longestCommonSubstring(array.get(i),array.get(j));
								//System.out.println(lcs);
								String leftSuffix = array.get(i).substring(lcs.length());
								String rightSuffix = array.get(j).substring(lcs.length());								
								int res = leftSuffix.compareTo(rightSuffix);
								if(res!=0){
									//System.out.println(leftSuffix+":"+rightSuffix);
									String suffixPairKey=createKey(res, leftSuffix, rightSuffix);
									if (suffixPairMap.containsKey(suffixPairKey)){
										int currentCount = suffixPairMap.get(suffixPairKey);
										currentCount++;
										suffixPairMap.put(suffixPairKey, currentCount);
									}else{
										suffixPairMap.put(suffixPairKey, 1);
									}
								}
							}
						}
						array = new ArrayList<String>();						
					}
				}
			}
		}

		scan.close();

	}

	private static String longestCommonSubstring(String left, String right){
		String common = left.substring(0,landa);
		for(int i = landa; i < left.length() && i < right.length(); i++){
			if (left.charAt(i) == right.charAt(i)){
				common = common + left.charAt(i);
			}else{
				return common;
			}
		}
		return common;
	}

	// the actual key ordering method
	private static String createKey(int res,String left, String right){
		String key ="";
		if (res > 0){
			key = left+":"+right;
		}else{
			key = right+":"+left;
		}

		return key;

	}

	//this method makes another pass over the lexicon and 
	// 1) extracts groups of words with common beginning (using the list of frequent suffix pairs, from the previous pass), 
	// 2) put each group into a pre-defined graph format
	// 3) clusters the nodes in each graph; the core of each cluster its the 'root' of all other words in that cluster.
	private static void formTheClasses() throws FileNotFoundException{

		output = new PrintWriter(new File(outputFilePath));
		
		Scanner scan = new Scanner (new File(lexiconFilePath));

		ArrayList<String> array = new ArrayList<String>();
		String prefix = "";
		while (scan.hasNextLine()){
			String term = scan.nextLine().trim();
			if(term.length() >= landa){
				if (array.size() == 0){
					prefix = term.substring(0,landa);
					array.add(term);
				}else{
					if (array.indexOf(term) != -1){
						//a repeated term
						continue;
					}
					String currentPrefix = term.substring(0,landa);
					if (prefix.equals(currentPrefix)){
						array.add(term);
					}else{
						//create a new group;
						nodePairMap = new HashMap<String,Integer>();
						for(int i = 0; i < array.size(); i++){
							for(int j = i+1 ; j < array.size(); j++){
								String lcs = longestCommonSubstring(array.get(i),array.get(j));
								//System.out.println(lcs);
								String leftSuffix = array.get(i).substring(lcs.length());
								String rightSuffix = array.get(j).substring(lcs.length());								
								int res = leftSuffix.compareTo(rightSuffix);
								if(res!=0){
									//System.out.println(leftSuffix+":"+rightSuffix);
									String suffixPairKey=createKey(res, leftSuffix, rightSuffix);
									int edgeWeight = suffixPairMap.get(suffixPairKey);
									if (edgeWeight >= alpha){
										String leftTerm = array.get(i);
										String rightTerm = array.get(j);								
										int res2 = leftTerm.compareTo(rightTerm);
										if (res2!=0){
											String nodePairKey = createKey(res, leftTerm, rightTerm);
											nodePairMap.put(nodePairKey, edgeWeight);
										}											
									}											
								}
							}
						}

						convertToGraphAndCluster(nodePairMap);

						array = new ArrayList<String>();
					}
				}
			}
		}

		scan.close();
		output.flush();
		output.close();

	}


	private static void convertToGraphAndCluster(HashMap<String,Integer> npMap){
		HashMap<String,ArrayList<String>> adjacencyLists = new HashMap<String,ArrayList<String>>();

		// extract the the adjacency lists 
		for(String key: npMap.keySet()){
			String[] parts = key.split(":");
			for(int i = 0; i < 2; i++){
				if (adjacencyLists.containsKey(parts[i])){
					ArrayList<String> tempArray =  adjacencyLists.get(parts[i]);
					tempArray.add(parts[(i+1)%2]);
					adjacencyLists.put(parts[i], tempArray);
				}else{
					ArrayList<String> tempArray = new ArrayList<String>();
					tempArray.add(parts[(i+1)%2]);
					adjacencyLists.put(parts[i], tempArray);					
				}				
			}
		}


		// sort the adjacency lists based on their edge weights 
		for(String key: adjacencyLists.keySet()){

			// copy the adjacency lists into arrays + build parallel arrays for the edge weights
			ArrayList<String> currentList = adjacencyLists.get(key);
			String[] destNodes = new String[currentList.size()];
			int[] edgeWeights = new int[currentList.size()];

			for(int i = 0; i < currentList.size(); i++){
				destNodes[i] = currentList.get(i);
				edgeWeights[i] = npMap.get(createKey(key, destNodes[i]));
			}


			// sort the adjacency lists (i.e., 'arrays') using bubble sort
			ArrayList<String> sortedDestNodes = new ArrayList<String>();
			for (int i = 0; i < edgeWeights.length; i++){
				int maxIndex = i;
				for (int j = i+ 1; j < edgeWeights.length; j++){
					if (edgeWeights[j] > edgeWeights[maxIndex]){
						maxIndex = j;
					}
				}
				if (maxIndex!=i){
					//simultaneous swaping!
					int tempDouble = edgeWeights[i];
					String tempString = destNodes[i];
					edgeWeights[i] = edgeWeights[maxIndex];
					destNodes[i] = destNodes[maxIndex];
					edgeWeights[maxIndex] = tempDouble;
					destNodes[maxIndex] = tempString;
				}
				sortedDestNodes.add(destNodes[i]);
			}

			adjacencyLists.put(key,sortedDestNodes);

		}			


		//do the clustering
		if(adjacencyLists.size()!=0){
			System.out.println("======================== graph clustering ==========================");
		}
		while(adjacencyLists.size()!=0){
			//get the node with the longest list
			int longestListsLength = 0;
			String longestListsSrcNode = "";
			for(String key: adjacencyLists.keySet()){
				int currentListsLength = adjacencyLists.get(key).size();
				if(currentListsLength >= longestListsLength){
					longestListsLength = currentListsLength;
					longestListsSrcNode = key;
				}
			}

			ArrayList<String> theNewCluster = new ArrayList<String>();
			theNewCluster.add(longestListsSrcNode);
			while(true){
				ArrayList<String> longestList = adjacencyLists.get(longestListsSrcNode);
				boolean checkedAll = true;
				String nextDest = "";
				for(String key:longestList){
					if(!theNewCluster.contains(key)){
						checkedAll = false;
						nextDest = key;
						break;
					}
				}

				if(checkedAll){
					// the full list has been checked.
					break;
				}

				ArrayList<String> nextList = adjacencyLists.get(nextDest);
				if (computeCohesion(longestList, nextList) >= gamma){//is part of the cluster (longestListsSrcNode is its root)
					theNewCluster.add(nextDest);
				}else{//is NOT part of the cluster 

					longestList.remove(nextDest);//remove the irrelevant node from the list
					adjacencyLists.put(longestListsSrcNode, longestList);
					nextList.remove(longestListsSrcNode); //reflect the change in the nextDest's list too
					adjacencyLists.put(nextDest, nextList);
				}
			}

			//print out the new cluster
			System.out.println("===== new cluster =====");
			System.out.println("root: "+theNewCluster.get(0));
			for(int i = 0; i < theNewCluster.size(); i++ ){
				System.out.print(theNewCluster.get(i)+", ");
			}
			System.out.println();
			

			if(theNewCluster.size() > 1){//only write the non-trivial clusters to the file
				output.println(theNewCluster.get(0));
				for(String key: theNewCluster){
					output.print(key+",");
				}
				output.println();
				output.println("----------------------------------------------------------");
			}
			

			// update the big graph: first part
			for(String key: theNewCluster){
				adjacencyLists.remove(key);
			}
			// update the big graph: second part
			for(String key: adjacencyLists.keySet()){
				ArrayList<String> tempArray = adjacencyLists.get(key);
				for(String innerKey: theNewCluster){
					tempArray.remove(innerKey);
				}
				adjacencyLists.put(key, tempArray);
			}
		}
	}

	//this is to make sure that the combination of any two parts will be correctly mapped to one key 
	//(since order must not matter)
	private static String createKey(String left, String right){
		int res = left.compareTo(right);
		return createKey(res,left, right);
	}


	// this is the implementation of the cohesion function as defined in the GRAS paper
	private static float computeCohesion(ArrayList<String> leftList, ArrayList<String> rightList){
		int intersectionSize = 0;
		for(String key :leftList){
			if(rightList.contains(key)){
				intersectionSize++;
			}
		}
		return (float)(1+intersectionSize)/rightList.size();
	}
}
