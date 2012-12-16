package com.example.comet_test;

import java.math.BigDecimal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView piResult;
	
	static final BigDecimal ONE = new BigDecimal(1);
    static final BigDecimal TWO = new BigDecimal(2);
    static final BigDecimal FOUR = new BigDecimal(4);
    static final int SCALE = 10000;
	
	// Newton's method for sqrt
    public static BigDecimal sqrt(BigDecimal A, final int SCALE) {
        BigDecimal x0 = new BigDecimal("0");
        BigDecimal x1 = new BigDecimal(Math.sqrt(A.doubleValue()));
        
        while (!x0.equals(x1)) {
            x0 = x1;
            x1 = A.divide(x0, SCALE, BigDecimal.ROUND_HALF_UP);
            x1 = x1.add(x0);
            x1 = x1.divide(TWO, SCALE, BigDecimal.ROUND_HALF_UP);
        }
        
        return x1;
    }
	
    // Gauss-Legendre Algorithm
	public static BigDecimal calculatePi() {
        BigDecimal a = ONE;
        BigDecimal b = ONE.divide(sqrt(TWO, SCALE), SCALE, BigDecimal.ROUND_HALF_UP);
        BigDecimal t = new BigDecimal(0.25);
        BigDecimal x = ONE;
        BigDecimal y;
        
        while (!a.equals(b)) {
            y = a;
            
            // a = (a + b)/2
            a = a.add(b).divide(TWO, SCALE, BigDecimal.ROUND_HALF_UP);
            
            // b = sqrt(b*y)
            b = sqrt(b.multiply(y), SCALE);
            
            // t -= x*((y - a)^2)
            t = t.subtract(x.multiply(y.subtract(a).multiply(y.subtract(a))));
            
            // x *= 2
            x = x.multiply(TWO);
        }
        // a + (a+b)/(4)
        return a.add(b).multiply(a.add(b)).divide(t.multiply(FOUR), SCALE, BigDecimal.ROUND_HALF_UP);
	}
	
	final long numBenchesToPerf = 10;
	long accumulatedRunTime = 0;
	long numBenchesLeft = 0;
	
	private class PIBenchmarkTask extends AsyncTask<Object, Long, Long> {
	     protected Long doInBackground(Object... objects) {
	    	 Long startTime = SystemClock.elapsedRealtime();
	    	 calculatePi();
	    	 Long endTime = SystemClock.elapsedRealtime();
	    	 accumulatedRunTime += (endTime - startTime);
	    	 return (long) 0;
	     }
	     
	     protected void onProgressUpdate(Long... progress) {}

	     protected void onPostExecute(Long result) {
	    	 numBenchesLeft -= 1;
	    	 if (numBenchesLeft <= 0) {
	    		 setPIResultText(accumulatedRunTime / numBenchesToPerf); 
	    	 } else {
	    		 (new PIBenchmarkTask()).execute();
	    	 }
	     }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		piResult = new TextView(this); 
		piResult = (TextView)findViewById(R.id.textView2);
		
		final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                accumulatedRunTime = 0;
                numBenchesLeft = numBenchesToPerf;
            	(new PIBenchmarkTask()).execute();
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void setPIResultText(Long t) {
		String s = String.valueOf(t);
		piResult.setText(s + "ms");
	}
	
}
