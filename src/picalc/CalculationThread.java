package picalc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class CalculationThread implements Runnable {
	
	long denominator;
	long signedMultiplicator;
	Vector<BigDecimal> resultsCollector;
	Semaphore semaphore;
	
	public CalculationThread(long denominator, long signedMultiplicator, Vector<BigDecimal> resultsCollector, Semaphore semaphore) {
		this.denominator = denominator;
		this.signedMultiplicator = signedMultiplicator;
		this.resultsCollector = resultsCollector;
		this.semaphore = semaphore;
	}
	
	/**
	 * In this method we use the Leibniz formula with infinite series to approximate Pi. 
	 * This algorithm is very inefficient as it converges very slowly. Around 500k iterations 
	 * are mandatory to get a 5 digit precision.
	 * 
	 * More information can be found at https://en.wikipedia.org/wiki/Leibniz_formula_for_%CF%80
	 * 
	 */
	@Override
	public void run() {
		try {
			this.semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BigDecimal nominator = new BigDecimal(4);
		BigDecimal denominator = new BigDecimal(this.denominator);
		BigDecimal sign = new BigDecimal(this.signedMultiplicator);
		
		
		nominator = nominator.divide(denominator, MathContext.DECIMAL128);
		nominator = nominator.multiply(sign);
		
		this.resultsCollector.addElement(nominator);
		this.semaphore.release();
	}

}
