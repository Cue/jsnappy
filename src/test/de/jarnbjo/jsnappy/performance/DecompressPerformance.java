package de.jarnbjo.jsnappy.performance;

import java.io.File;
import java.io.RandomAccessFile;

import de.jarnbjo.jsnappy.Buffer;
import de.jarnbjo.jsnappy.SnappyCompressor;
import de.jarnbjo.jsnappy.SnappyDecompressor;

public class DecompressPerformance {

	public static void main(String[] args) throws Exception {

		for(int c=0; c<2; c++) {
			long lall = 0, tall = 0;

			for(File f : new File("resources/testdata/").listFiles()) {
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				byte[] data = new byte[(int) raf.length()];
				raf.readFully(data);
				Buffer compressed = SnappyCompressor.compress(data);

				long l = 0;

				Buffer b = new Buffer();

				int iterations = 4000;

				long t0 = System.nanoTime();
				for(int i=0; i<iterations; i++) {
					l += SnappyDecompressor.decompress(compressed, b).getLength();
				}
				long t1 = System.nanoTime();
				lall += l;
				tall += (t1-t0);

				System.out.println(f.getName() + ": " + String.format("%.2f", (l * 1000000000. / (t1-t0))/(1024*1024)));
			}

			System.out.println("all: " + String.format("%.2f", (lall * 1000000000. / (tall))/(1024*1024)));
			System.out.println();
		}
	}

}
