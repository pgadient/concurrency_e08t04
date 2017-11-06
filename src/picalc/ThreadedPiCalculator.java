package picalc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;

public class ThreadedPiCalculator {

	int concurrentThreads = 2;		// the amount of threads that are used to approximate Pi
	int rounds = 10000;				// total amount of threads to spawn

	Semaphore semaphore;			// ensures that only #concurrentThreads are allowed to run concurrently
	Vector<BigDecimal> results;		// BigDecimal as we need more precision (128+ Bits)
	ArrayList<Thread> threads;		// here we keep track of the created threads
	
	long startTime;					// calculation start time
	long endTime;					// calculation end time

	
	public static void main(String[] args) {
		ThreadedPiCalculator instance = new ThreadedPiCalculator();
		
		instance.startNumberCrunching();
		instance.collectResults();
	}
	
	public ThreadedPiCalculator() {
		this.semaphore = new Semaphore(this.concurrentThreads);
		this.results = new Vector<BigDecimal>();
		this.threads = new ArrayList<Thread>();
	}

	public void startNumberCrunching() {
		CalculationThread ct;
		long currentDenominator = 1;
		long sign = 1;
		
		this.startTime = System.currentTimeMillis();
		
		for (int i = 0; i < this.rounds; i++) {
			ct = new CalculationThread(currentDenominator, sign, this.results, this.semaphore);
			Thread t = new Thread(ct);
			this.threads.add(t);
			t.start();
			currentDenominator = currentDenominator + 2;
			sign = -sign;
		}
	}

	public void collectResults() {
		while (true) {
			boolean allThreadsDied = true;
			for (Thread t : this.threads) {
				if (t.isAlive()) {
					allThreadsDied = false;
					continue;
				}
			}
			
			if (allThreadsDied) {
				this.endTime = System.currentTimeMillis();
				break;
			}
		}
		
		BigDecimal sum = new BigDecimal(0);
		for (BigDecimal bd : this.results) {
			sum = sum.add(bd);
		}
		
		System.out.println("Ground Truth:\t" + "3.1415926535897932384626433832795028841" + "\nOur Result:\t" + sum.toPlainString() + "\nTime used:\t" + (this.endTime - this.startTime) + " ms\nRounds used:\t" + this.rounds);
	}
	
}
