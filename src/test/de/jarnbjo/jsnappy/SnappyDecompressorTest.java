package de.jarnbjo.jsnappy;

import org.junit.Assert;
import org.junit.Test;

public class SnappyDecompressorTest {

	@Test
	public void testSuperfluousData() {
		
		byte[] data = new byte[2];
		
		try {
			SnappyDecompressor.decompress(data);
			Assert.fail("Exception was expected");
		}
		catch(FormatViolationException e) {
			// the input data is invalid on offset 1
			Assert.assertEquals(1, e.getOffset());
		}
		
	}
	
}
