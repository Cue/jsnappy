package de.jarnbjo.jsnappy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class SnzOutputStreamTest {

	@Test
	public void testIllegalBufferSize1() throws IOException {
		try {
			new SnzOutputStream(new ByteArrayOutputStream(), 42);
			Assert.fail("Buffer size 42 should not be accepted");
		}
		catch(IllegalArgumentException e) {
			// expected
		}
	}
	
	@Test
	public void testIllegalBufferSize2() throws IOException {
		try {
			new SnzOutputStream(new ByteArrayOutputStream(), 4711);
			Assert.fail("Buffer size 4711 should not be accepted");
		}
		catch(IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testIllegalBufferSize3() throws IOException {
		try {
			new SnzOutputStream(new ByteArrayOutputStream(), 1<<30);
			Assert.fail("Buffer size 1<<30 should not be accepted");
		}
		catch(IllegalArgumentException e) {
			// expected
		}
	}
	
	@Test
	public void testIllegalBufferSize4() throws IOException {
		try {
			new SnzOutputStream(new ByteArrayOutputStream(), -1);
			Assert.fail("Buffer size -1 should not be accepted");
		}
		catch(IllegalArgumentException e) {
			// expected
		}
	}

}
