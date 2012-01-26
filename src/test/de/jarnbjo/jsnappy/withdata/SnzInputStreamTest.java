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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jarnbjo.jsnappy.SnzInputStream;
import de.jarnbjo.jsnappy.TestUtil;

/**
 * Place test files in the resources/testdata directory  
 */
public class SnzInputStreamTest {

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
	public void testSnzInputStream() throws IOException, InterruptedException {

		File tmpDirectory = TestUtil.directoriesToFile("tmp", "SnzInputStreamTest");
		
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
		
		for(File org : testdataDirectory.listFiles()) {
			if(org.isFile()) {
				System.out.println("comparing " + org.getName());
				byte[] originalData = TestUtil.readFully(org);
				ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
				SnzInputStream sis = new SnzInputStream(new FileInputStream(new File(tmpDirectory, org.getName() + ".snz")));
				TestUtil.pipe(sis, decompressedData);
				sis.close();
				Assert.assertArrayEquals(originalData, decompressedData.toByteArray());
			}
		}
	}
	

	
}
