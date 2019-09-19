
import java.util.ArrayList;
import java.util.Collections;
import org.knowm.xchart.*;

public class Quicky {

	public static void main(String[] args) {

		Stopwatch stp = new Stopwatch();
		int averageOf = 64;
		int arraySize = 512;
		int num_of_tests = 5;

		long[][] runTimes = new long[num_of_tests][arraySize];
		double[] bitterN = new double[arraySize];
		
		QuickSort qs = new QuickSort();

		for(int tempArraySize = 2;tempArraySize <= arraySize;tempArraySize++){
			long total[] = new long[num_of_tests];
			double bitterNumbers = 0;
			for(int run = 0;run < averageOf;run++){
				
				//set up testing arrays
				int[][] testArr= new int[num_of_tests][tempArraySize];
				testArr[0] = createList(tempArraySize);
				for(int j = 1;j < num_of_tests;j++)
					for(int k = 0;k < tempArraySize;k++)
						testArr[j][k] = testArr[0][k];

				stp.start();
				qs.sort(SortType.QUICK,testArr[0], 0, tempArraySize-1,0);
				stp.stop();
				total[0]+=stp.timeInNanoseconds();
				
				stp.start();
				qs.sort(SortType.MEDIAN,testArr[1], 0, tempArraySize-1,0);
				stp.stop();
				total[1]+=stp.timeInNanoseconds();
				
				long[] bitter = bitterHelper(stp,testArr[2]);
				int bitterNum = 0;
				long bitterTime = Long.MAX_VALUE;
				for(int j = 0;j< bitter.length;j++)
					if(bitter[j] < bitterTime) {
						bitterTime = bitter[j];
						bitterNum = j;
					}
				
				total[2]+= bitterTime;
				bitterNumbers+= bitterNum;
				
				stp.start();
				qs.sort(SortType.HOARE,testArr[3], 0, tempArraySize-1,0);
				stp.stop();
				total[3]+=stp.timeInNanoseconds();
				
				stp.start();
				qs.sort(SortType.LOMUTO,testArr[4], 0, tempArraySize-1,0);
				stp.stop();
				total[4]+=stp.timeInNanoseconds();
			}
			System.out.println("N:"+tempArraySize);
			for(int j=0; j < num_of_tests;j++) {
				runTimes[j][tempArraySize-1] = total[j]/averageOf;
				System.out.println("runTimes: "+j+" average: "+runTimes[j][tempArraySize-1]);
			}
			bitterN[tempArraySize-1] = bitterNumbers/averageOf;
			System.out.println("BitterN: "+bitterN[tempArraySize-1]);
		}
		showChart(runTimes,bitterN);
	}

	//create and shuffle a new list of length "length"
	static int[] createList(int length){

		ArrayList<Integer> list = new ArrayList<Integer>(length);
		for (int i = 0 ; i < length;i+=1)
			list.add(i,i);

		Collections.shuffle(list);

		int[] arr = new int[length];
		for (int i = 0 ; i < length;i+=1)
			arr[i] = (int)list.get(i);

		return arr;
	}
	
	static long[] bitterHelper(Stopwatch stp,int[] arr){
		
		QuickSort qs = new QuickSort();
		long[] times = new long[arr.length];
		int[][] bigArray = new int[arr.length][arr.length];
		
		//copy array into bigArray
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr.length; j++)
				bigArray[i][j] = arr[j];
		
		for (int i = 0; i < arr.length; i++) {
			stp.start();
			qs.sort(SortType.BITTEREND,bigArray[i], 0, arr.length-1,i);
			qs.insertSort(bigArray[i], 0, bigArray[i].length-1);
			stp.stop();
			times[i]=stp.timeInNanoseconds();
		}
		
		return times;
	}

	static void showChart(long arr[][], double[] bitterNum) 
	{
		double[][] arrD = new double[arr.length][arr[0].length];
		
		double[] xAxis = new double[arr[0].length];
		for (int i=0; i<arr[0].length; i++) {
			xAxis[i] = i;
			for (int j=0; j<arr.length; j++)
					arrD[j][i] = (double)arr[j][i];
		}
	 
	    // Create Chart		
	    XYChart chartA = QuickChart.getChart("QuickSort vs MeanOf3 vs BitterEnd", "Array Size", "nanoSec", "QuickSort", xAxis, arrD[0]);
	    chartA.addSeries("MeanOf3", arrD[1]);
	    chartA.addSeries("BitterEnd", arrD[2]);    
	    
	    XYChart chartB = QuickChart.getChart("Bitter End Sort", "Array Size", "End Number", "BitterSort", xAxis, bitterNum);
	    
		XYChart chartC = QuickChart.getChart("Hoare vs Lomuto", "Array Size", "nanoSec", "HoareSrt", xAxis, arrD[3]);
		chartC.addSeries("LomutoSort", arrD[4]);	    
	    
	    // Show it
	    new SwingWrapper<XYChart>(chartA).displayChart();
	    new SwingWrapper<XYChart>(chartB).displayChart();
	    new SwingWrapper<XYChart>(chartC).displayChart();
	}
}

// Java program for implementation of QuickSort
//from https://www.geeksforgeeks.org/quick-sort/
class QuickSort { 
	
	//by Sasha
	void swap(int arr[],int a, int b){
		int temp = arr[a];
		arr[a] = arr[b]; 
		arr[b] = temp;
	}

	int partMed(int arr[], int low, int high) {
		int mid = low + (high-low)/2;

		if(arr[low] > arr[mid])
			swap(arr,low,mid);
		if(arr[low] > arr[high])
			swap(arr,low,high);
		if(arr[mid] < arr[high])
			swap(arr,mid,high);
		
		return partHigh(arr,low,high);
	}
	
    void insertSort(int arr[],int low,int high) 
    { 
        for (int i = low+1; i <= high; ++i) { 
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j = j - 1;
            }
            arr[j + 1] = key;
        }
    }
	
	//from https://www.geeksforgeeks.org/hoares-vs-lomuto-partition-scheme-quicksort/
	int partHoare(int[] arr,int low, int high){
		int pivot = arr[low];
		int i = low - 1, j = high + 1;

		while (true){ 
			do i++; 
			while (arr[i] < pivot);
			do j--; 
			while (arr[j] > pivot); 
			
			if (i >= j)
				return j;
			swap(arr,i, j);
		}
	}
	
	//from https://www.geeksforgeeks.org/hoares-vs-lomuto-partition-scheme-quicksort/
	int partLomuto(int []arr, int low, int high){ 
		int pivot = arr[high]; 
		int i = (low - 1);
		
		for (int j = low; j <= high- 1; j++)
		// If current element is smaller 
		// than or equal to pivot 
		if (arr[j] <= pivot){ 
			i++; // increment index of 
			// smaller element 
			swap(arr, i, j); 
		}
		swap(arr, i + 1, high);
		return (i + 1);
	}

	int partHigh(int arr[], int low, int high){
		
		int pivot = arr[high];
		int i = (low-1);
		for (int j=low; j<high; j++){
			if (arr[j] < pivot){
				i++;
				swap(arr,i,j);
			}
		}
		
		swap(arr,i+1,high);

		return i+1;
	} 


	/* The main function that implements QuickSort() 
	arr[] --> Array to be sorted, 
	low --> Starting index, 
	high --> Ending index */
	void sort(SortType type,int[] arr, int low, int high, int bitterEnd) 
	{
		if (low < high){
			
			int pi = 0;
			if(type == SortType.BITTEREND) {
				if(high - low > bitterEnd)
						pi = partHigh(arr, low, high);
				else return;
			}
			else if(type == SortType.QUICK)
				pi = partHigh(arr, low, high);
			else if(type == SortType.MEDIAN)
				pi = partMed(arr, low, high);
			else if(type == SortType.HOARE)
				pi = partHoare(arr, low, high);
			else if(type == SortType.LOMUTO)
				pi = partLomuto(arr, low, high);

			sort(type,arr, pi+1, high,bitterEnd);
			if(type == SortType.HOARE && pi+1 < arr.length) pi++;
			sort(type,arr, low, pi-1,bitterEnd);
		} 
	} 
} 
/*This code is contributed by Rajat Mishra */

//stole from https://www.cs.utexas.edu/~scottm/cs307/javacode/utilities/Stopwatch.java
class Stopwatch {
	public Stopwatch(){}

    private long startTime;
	private long stopTime;
	
	long average = 0;
	ArrayList<Long> runs = new ArrayList<Long>();

    public static final long NANOS_PER_SEC = (long)1000000000.0;

	/**
	 start the stop watch.
	*/
	public void start(){
		startTime = System.nanoTime();
	}

	/**
	 stop the stop watch.
	*/
	public void stop()
	{	stopTime = System.nanoTime();	}

	public String toString(){
	    return "elapsed time: " + timeInNanoseconds() + " NanaoSeconds.";
	}

	/**
	elapsed time in nanoseconds.
	@return the time recorded on the stopwatch in nanoseconds
	*/
	public long timeInNanoseconds()
	{	return (stopTime - startTime);	}
}
