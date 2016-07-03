package wit.cgd.bunnyhop.game;

import com.badlogic.gdx.utils.TimeUtils;

public class Timer {
	
	 public long start;
	 public long secsToWait;

	    public Timer(long secsToWait)
	    {
	        this.secsToWait = secsToWait;
	    }

	    public void start()
	    {
	        start = TimeUtils.millis() / 1000;
	    }

	    public boolean hasCompleted()
	    {
	        return TimeUtils.millis() / 1000 - start >= secsToWait;
	    }

}
