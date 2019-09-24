import java.util.ArrayList;

//taken from https://www.cs.utexas.edu/~scottm/cs307/javacode/utilities/Stopwatch.java
class Stopwatch {
    Stopwatch(){}

    private long startTime;
    private long stopTime;

    long average = 0;
    ArrayList<Long> runs = new ArrayList<Long>();

    public static final long NANOS_PER_SEC = (long)1000000000.0;

    /**
     start the stop watch.
     */
    void start(){
        startTime = System.nanoTime();
    }

    /**
     stop the stop watch.
     */
    void stop()
    {	stopTime = System.nanoTime();	}

    public String toString(){
        return "elapsed time: " + timeInNanoseconds() + " NanaoSeconds.";
    }

    /**
     elapsed time in nanoseconds.
     @return the time recorded on the stopwatch in nanoseconds
     */
    long timeInNanoseconds()
    {	return (stopTime - startTime);	}
}