package de.jarnbjo.jsnappy.performance;

import java.io.File;
import java.io.RandomAccessFile;

import de.jarnbjo.jsnappy.Buffer;
import de.jarnbjo.jsnappy.SnappyCompressor;

public class CompressPerformance {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		long lall = 0, tall = 0;

		int[] efforts = {1, 50, 100};
		
		for(int effort : efforts) {
			for(File f : new File("resources/testdata/").listFiles()) {
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				byte[] data = new byte[(int) raf.length()];
				raf.readFully(data);
				Buffer b = new Buffer();
	
				long l = 0, m = 0;
	
				int iterations = 5;
	
				long t0 = System.nanoTime();
				for(int i=0; i<iterations; i++) {
					m += SnappyCompressor.compress(data, 0, data.length, b, effort).getLength();
					l += data.length;
				}
				long t1 = System.nanoTime();
				lall += l;
				tall += (t1-t0);
	
				// System.out.println(f.getName() + ": " + String.format("%.2f", (l * 1000000000. / (t1-t0))/(1024*1024)));
			}
	
			System.out.println("all (" + effort + "): " + String.format("%.2f", (lall * 1000000000. / (tall))/(1024*1024)));
		}
	}

}
