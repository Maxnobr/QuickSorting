import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Quicky {

	public static void main(String[] args) throws IOException {

		Stopwatch stp = new Stopwatch();
		int averageOf = 128;
		int arraySize = 512;
		int num_of_tests = SortType.values().length;

		long[][] runTimes = new long[num_of_tests][arraySize];
		double[] bitterN = new double[arraySize];
		
		QuickSort qs = new QuickSort();

		for(int tempArraySize = 2;tempArraySize <= arraySize;tempArraySize++){
			long[] total = new long[num_of_tests];
			double bitterNumbers = 0;
			for(int run = 0;run < averageOf;run++){
				
				//set up testing arrays
				int[][] testArr= new int[num_of_tests][tempArraySize];
				testArr[0] = createList(tempArraySize);
				for(int j = 1;j < num_of_tests;j++)
					if (tempArraySize >= 0) System.arraycopy(testArr[0], 0, testArr[j], 0, tempArraySize);

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
	private static int[] createList(int length){

		ArrayList<Integer> list = new ArrayList<>(length);
		for (int i = 0 ; i < length;i+=1)
			list.add(i,i);

		Collections.shuffle(list);

		int[] arr = new int[length];
		for (int i = 0 ; i < length;i+=1)
			arr[i] = list.get(i);

		return arr;
	}
	
	private static long[] bitterHelper(Stopwatch stp, int[] arr){
		
		QuickSort qs = new QuickSort();
		long[] times = new long[arr.length];
		int[][] bigArray = new int[arr.length][arr.length];
		
		//copy array into bigArray
		for (int i = 0; i < arr.length; i++)
			System.arraycopy(arr, 0, bigArray[i], 0, arr.length);
		
		for (int i = 0; i < arr.length; i++) {
			stp.start();
			qs.sort(SortType.BITTEREND,bigArray[i], 0, arr.length-1,i);
			qs.insertSort(bigArray[i], bigArray[i].length-1);
			stp.stop();
			times[i]=stp.timeInNanoseconds();
		}
		
		return times;
	}

	private static void showChart(long[][] arr, double[] bitterNum) throws IOException {

		//set up arrays for charts
		double[][] arrD = new double[arr.length][arr[0].length];
		double[] xAxis = new double[arr[0].length];
		for (int i=0; i<arr[0].length; i++) {
			xAxis[i] = i;
			for (int j=0; j<arr.length; j++)
					arrD[j][i] = (double)arr[j][i];
		}
	 
	    // Create Charts
	    XYChart chartA = QuickChart.getChart("QuickSort vs MeanOf3 vs BitterEnd", "Array Size", "nanoSec", "QuickSort", xAxis, arrD[0]);
	    chartA.addSeries("MeanOf3", arrD[1]).setMarker(SeriesMarkers.NONE);
	    chartA.addSeries("BitterEnd", arrD[2]).setMarker(SeriesMarkers.NONE);

	    XYChart chartB = QuickChart.getChart("Bitter End Sort", "Array Size", "End Number", "BitterSort", xAxis, bitterNum);

		XYChart chartC = QuickChart.getChart("Hoare vs Lomuto", "Array Size", "nanoSec", "HoareSrt", xAxis, arrD[3]);
		chartC.addSeries("LomutoSort", arrD[0]).setMarker(SeriesMarkers.NONE);

	    // Show it
		new SwingWrapper<>(chartA).displayChart();
		new SwingWrapper<>(chartB).displayChart();
		new SwingWrapper<>(chartC).displayChart();

		// Save it
		BitmapEncoder.saveBitmap(chartA, "./Sorting", BitmapEncoder.BitmapFormat.JPG);
		BitmapEncoder.saveBitmap(chartB, "./BitterEnd", BitmapEncoder.BitmapFormat.JPG);
		BitmapEncoder.saveBitmap(chartC, "./Hoaruto", BitmapEncoder.BitmapFormat.JPG);

		// Save to csv
		FileWriter csvWriter = new FileWriter("quickSortTimes.csv");

		//header
		csvWriter.append("Array length,QuickSort,MeanOf3,BitterEnd,Hoare,BestBitterEndNumber\n");

		for (int i=0; i<arr[0].length; i++) {
			csvWriter.append(String.valueOf(i));
			for (long[] longs : arr)
				csvWriter.append(",").append(Long.toString(longs[i]));

			csvWriter.append(",").append(String.valueOf(bitterNum[i]));
			csvWriter.append("\n");
		}

		csvWriter.flush();
		csvWriter.close();
	}
}