// Java program for implementation of QuickSort
//from https://www.geeksforgeeks.org/quick-sort/
class QuickSort {

	//by Sasha
	private void swap(int[] arr, int a, int b){
		int temp = arr[a];
		arr[a] = arr[b];
		arr[b] = temp;
	}

	private int partMed(int[] arr, int low, int high) {
		int mid = low + (high-low)/2;

		if(arr[low] > arr[mid])
			swap(arr,low,mid);
		if(arr[low] > arr[high])
			swap(arr,low,high);
		if(arr[mid] < arr[high])
			swap(arr,mid,high);

		return partHigh(arr,low,high);
	}

    void insertSort(int[] arr, int high)
    {
        for (int i = 1; i <= high; ++i) {
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
	private int partHoare(int[] arr, int low, int high){
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

	private int partHigh(int[] arr, int low, int high){

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

			sort(type,arr, pi+1, high,bitterEnd);
			if(type == SortType.HOARE && pi+1 < arr.length) pi++;
			sort(type,arr, low, pi-1,bitterEnd);
		}
	}
}
