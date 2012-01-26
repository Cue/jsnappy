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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jarnbjo.jsnappy.SnzInputStream;
import de.jarnbjo.jsnappy.SnzOutputStream;
import de.jarnbjo.jsnappy.TestUtil;

/**
 * Place test files in the resources/testdata directory  
 */
public class SnzInAndOutputStreamTest {

	private static File testdataDirectory;
	
	@BeforeClass
	public static void init() {
		testdataDirectory = TestUtil.directoriesToFile("resources", "testdata");		
		if(!testdataDirectory.exists() || !testdataDirectory.isDirectory() || testdataDirectory.listFiles().length == 0) {
			throw new IllegalStateException("Test data directory \"" + testdataDirectory.getAbsolutePath() + " does not exist, is not a directory or is empty");
		}
	}
	
	@Test
	public void testBothWays() throws IOException, InterruptedException {

		for(File org : testdataDirectory.listFiles()) {
			if(org.isFile()) {
				System.out.println("checking " + org.getName());
				byte[] originalData = TestUtil.readFully(org);
				
				ByteArrayOutputStream compressedData = new ByteArrayOutputStream();				
				SnzOutputStream sos = new SnzOutputStream(compressedData);
				sos.write(originalData);
				sos.close();
				
				ByteArrayOutputStream decompressedData = new ByteArrayOutputStream();
				SnzInputStream sis = new SnzInputStream(new ByteArrayInputStream(compressedData.toByteArray()));
				TestUtil.pipe(sis, decompressedData);
				Assert.assertArrayEquals(originalData, decompressedData.toByteArray());
			}
		}
		
	}
	
	
}
