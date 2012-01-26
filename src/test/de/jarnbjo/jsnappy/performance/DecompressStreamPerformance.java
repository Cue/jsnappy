package de.jarnbjo.jsnappy.performance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;

import de.jarnbjo.jsnappy.SnzInputStream;
import de.jarnbjo.jsnappy.SnzMTInputStream;
import de.jarnbjo.jsnappy.SnzOutputStream;

public class DecompressStreamPerformance {

	public static void main(String[] args) throws Exception {

		boolean mt = false;
		
		for(int c=0; c<2; c++) {
			long lall = 0, tall = 0;

			for(File f : new File("resources/testdata/").listFiles()) {
				RandomAccessFile raf = new RandomAccessFile(f, "r");
				byte[] data = new byte[(int) raf.length()];
				raf.readFully(data);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				SnzOutputStream sos = new SnzOutputStream(baos, 4096);
				sos.write(data);
				sos.close();

				byte[] compressed = baos.toByteArray();

				long l = 0;
				int r = 0;

				byte[] dummy = new byte[65536];

				int iterations = 2500;

				long t0 = System.nanoTime();
				for(int i=0; i<iterations; i++) {
					SnzInputStream sis = mt ? 
							new SnzMTInputStream(new ByteArrayInputStream(compressed)) :
							new SnzInputStream(new ByteArrayInputStream(compressed));
					while((r = sis.read(dummy)) >= 0) {
						l += r;
					}
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
