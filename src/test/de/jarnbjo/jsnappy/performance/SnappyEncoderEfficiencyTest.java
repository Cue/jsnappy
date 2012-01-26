/*
 *  Copyright 2011 Tor-Einar Jarnbjo
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package de.jarnbjo.jsnappy.performance;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Assert;

import de.jarnbjo.jsnappy.SnzOutputStream;
import de.jarnbjo.jsnappy.TestUtil;

/**
 * Place test files in the resources/testdata directory  
 */
public class SnappyEncoderEfficiencyTest {

	private static String snzipExecutable;
	private static File testdataDirectory;
	
	public static void main(String[] args) throws Exception {
		snzipExecutable = TestUtil.findSnzipExecutable();
		if(snzipExecutable == null) {
			throw new IllegalStateException("No executable snzip file found in bin directory");
		}
	
		testdataDirectory = TestUtil.directoriesToFile("resources", "testdata");
		
		if(!testdataDirectory.exists() || !testdataDirectory.isDirectory() || testdataDirectory.listFiles().length == 0) {
			throw new IllegalStateException("Test data directory \"" + testdataDirectory.getAbsolutePath() + " does not exist, is not a directory or is empty");
		}

		TestUtil.deleteRecursive(new File("tmp"));

		File tmpDirectory = TestUtil.directoriesToFile("tmp", "SnappyEncoderEfficiencyTest");
		
		TestUtil.copyRecursive(testdataDirectory, tmpDirectory);
		
		ProcessBuilder pb = new ProcessBuilder(snzipExecutable, "*");
		pb.directory(tmpDirectory);
		pb.redirectErrorStream(true);
		Process p = pb.start();

		TestUtil.pipe(p.getInputStream(),  System.out);
		int snzReturnCode = p.waitFor();
		if(snzReturnCode != 0) {
			Assert.fail("snzip exited with return code " + snzReturnCode);
		}
		
		System.out.println();
		System.out.println();
		
		for(File org : testdataDirectory.listFiles()) {
			if(org.isFile()) {
				// System.out.println("comparing " + org.getName());
				byte[] originalData = TestUtil.readFully(org);

				int nativeLength = (int)new File(tmpDirectory, org.getName() + ".snz").length();
				double nativeSize = 100. * nativeLength / originalData.length;

				ByteArrayOutputStream jsnappyData = new ByteArrayOutputStream();
				SnzOutputStream sos = new SnzOutputStream(jsnappyData);
				sos.setCompressionEffort(1);
				sos.write(originalData);
				sos.close();
				double javaSize1 = 100. * jsnappyData.toByteArray().length / originalData.length;
				
				jsnappyData = new ByteArrayOutputStream();
				sos = new SnzOutputStream(jsnappyData);
				sos.setCompressionEffort(50);
				sos.write(originalData);
				sos.close();
				double javaSize50 = 100. * jsnappyData.toByteArray().length / originalData.length;
				
				jsnappyData = new ByteArrayOutputStream();
				sos = new SnzOutputStream(jsnappyData);
				sos.setCompressionEffort(100);
				sos.write(originalData);
				sos.close();
				double javaSize100 = 100. * jsnappyData.toByteArray().length / originalData.length;

				System.out.printf("%s: native: %.1f%%, Java: %.1f%% - %.1f%% - %.1f%% %n", org.getName(), nativeSize, javaSize1, javaSize50, javaSize100);
			}
		}
	}
	

	
}
