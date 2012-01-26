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

package de.jarnbjo.jsnappy.withdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jarnbjo.jsnappy.SnzOutputStream;
import de.jarnbjo.jsnappy.TestUtil;

/**
 * Place test files in the resources/testdata directory  
 */
public class SnzOutputStreamTest {

	private static String snzipExecutable;
	private static File testdataDirectory;
	
	@BeforeClass
	public static void init() {
		snzipExecutable = TestUtil.findSnzipExecutable();
		if(snzipExecutable == null) {
			throw new IllegalStateException("No executable snzip file found in bin directory");
		}
	
		testdataDirectory = TestUtil.directoriesToFile("resources", "testdata");
		
		if(!testdataDirectory.exists() || !testdataDirectory.isDirectory() || testdataDirectory.listFiles().length == 0) {
			throw new IllegalStateException("Test data directory \"" + testdataDirectory.getAbsolutePath() + " does not exist, is not a directory or is empty");
		}

		TestUtil.deleteRecursive(new File("tmp"));
	}
	
	@AfterClass
	public static void cleanup() {
		TestUtil.deleteRecursive(new File("tmp"));
	}
	
	@Test
	public void testSnzOutputStream_001() throws IOException, InterruptedException {
		testInternal(1);
	}
	
	@Test
	public void testSnzOutputStream_050() throws IOException, InterruptedException {
		testInternal(50);
	}

	@Test
	public void testSnzOutputStream_100() throws IOException, InterruptedException {
		testInternal(100);
	}

	public void testInternal(int effort) throws IOException, InterruptedException {

		File tmpDirectory = TestUtil.directoriesToFile("tmp", "SnzOutputStreamTest");
		tmpDirectory.mkdirs();
		
		for(File org : testdataDirectory.listFiles()) {
			if(org.isFile()) {
				System.out.println("compressing " + org.getName());				
				SnzOutputStream sos = new SnzOutputStream(new FileOutputStream(new File(tmpDirectory, org.getName() + ".snz")));
				sos.setCompressionEffort(effort);
				FileInputStream fis = new FileInputStream(org);
				TestUtil.pipe(fis, sos);
				fis.close();
				sos.close();
			}
		}
		
		ProcessBuilder pb = new ProcessBuilder(snzipExecutable, "-d", "*");
		pb.directory(tmpDirectory);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		TestUtil.pipe(p.getInputStream(), System.out);
		int snzReturnCode = p.waitFor();
		if(snzReturnCode != 0) {
			Assert.fail("snzip exited with return code " + snzReturnCode);
		}
		
		for(File org : testdataDirectory.listFiles()) {
			if(org.isFile()) {
				System.out.println("comparing " + org.getName());
				byte[] originalData = TestUtil.readFully(org);
				byte[] processedData = TestUtil.readFully(new File(tmpDirectory, org.getName()));
				Assert.assertArrayEquals(originalData, processedData);
			}
		}

	}
	
}
