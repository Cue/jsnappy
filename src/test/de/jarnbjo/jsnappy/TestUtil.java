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

package de.jarnbjo.jsnappy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestUtil {

	public static byte[] readResource(String path) throws IOException {
		return readFully(TestUtil.class.getResourceAsStream(path));
	}
	
	public static byte[] readFully(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		pipe(is, baos);
		return baos.toByteArray();		
	}
	
	public static byte[] readFully(File f) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(f);
		pipe(fis, baos);
		fis.close();
		return baos.toByteArray();		
	}
	
	public static void pipe(InputStream is, OutputStream os) throws IOException {
		byte[] buffer = new byte[65536];
		int r = 0;
		while((r=is.read(buffer))>=0) {
			os.write(buffer, 0, r);
		}
		os.flush();
	}

	public static void writeFile(String filename, byte[] data) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(data);
		fos.close();
	}

	public static void deleteRecursive(File dir) {
		if(dir.exists() && dir.isDirectory()) {
			for(File f : dir.listFiles()) {
				if(f.isDirectory()) {
					deleteRecursive(f);
				}
				else {
					f.delete();
				}
			}
		}
	}
	
	public static void copyRecursive(File sourceDir, File targetDir) throws IOException {
		targetDir.mkdirs();
		for(File f : sourceDir.listFiles()) {
			if(f.isDirectory()) {
				copyRecursive(f, new File(targetDir, f.getName()));
			}
			else {
				FileInputStream fis = new FileInputStream(f);
				FileOutputStream fos = new FileOutputStream(new File(targetDir, f.getName()));			
				pipe(fis, fos);
				fis.close();
				fos.close();
			}
		}
	}
	
	public static String findSnzipExecutable() {
		for(File f : new File("bin").listFiles()) {
			if(f.getName().equals("snzip") || f.getName().startsWith("snzip.")) {
				return new File(new File("bin"), f.getName()).getAbsolutePath();
			}
		}
		return null;
	}
	
	public static File directoriesToFile(String ... directoryNames) {
		if(directoryNames.length == 0) {
			throw new IllegalArgumentException("No directory names");
		}
		File f = null;
		for(String dn : directoryNames) {
			f = f == null ? new File(dn) : new File(f, dn);
		}
		return f;
	}
	
}
