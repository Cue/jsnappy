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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jarnbjo.jsnappy.IntIterator;
import de.jarnbjo.jsnappy.IntListHashMap;

public class IntListHashMapTest {

	@Test
	public void testSimple1() {
		IntListHashMap m = new IntListHashMap(10);
		m.put(5, 1);
		m.put(5, 2);
		m.put(5, 3);
		IntIterator i = m.getReverse(5);
		assertNotNull(i);

		assertTrue(i.next());
		assertEquals(3, i.get());

		assertTrue(i.next());
		assertEquals(2, i.get());

		assertTrue(i.next());
		assertEquals(1, i.get());
	}

	@Test
	public void testCollision() {
		IntListHashMap m = new IntListHashMap(10);
		m.put(5, 1);
		m.put(5, 2);
		m.put(5, 3);
		m.put(15, 4);
		m.put(15, 5);
		m.put(15, 6);

		IntIterator i = m.getReverse(5);
		assertNotNull(i);

		assertTrue(i.next());
		assertEquals(3, i.get());

		assertTrue(i.next());
		assertEquals(2, i.get());

		assertTrue(i.next());
		assertEquals(1, i.get());

		assertFalse(i.next());

		i = m.getReverse(15);
		assertNotNull(i);

		assertTrue(i.next());
		assertEquals(6, i.get());

		assertTrue(i.next());
		assertEquals(5, i.get());

		assertTrue(i.next());
		assertEquals(4, i.get());

		assertFalse(i.next());
	}

	@Test
	public void testExpand() {

		IntListHashMap m = new IntListHashMap(10);
		for(int i=1; i<=1000; i++) {
			m.put(1, i);
		}

		IntIterator ii = m.getReverse(1);
		assertNotNull(ii);

		for(int i=1000; i>=1; i--) {
			assertTrue(ii.next());
			assertEquals(i, ii.get());
		}

		assertFalse(ii.next());

	}
	
	@Test
	public void testRemoveDuplicates() {

		IntListHashMap m = new IntListHashMap(10);
		m.put(1, 1);
		m.put(1, 1);

		IntIterator ii = m.getReverse(1);
		assertNotNull(ii);

		assertTrue(ii.next());
		assertEquals(1, ii.get());
		assertFalse(ii.next());
	}

	@Test
	public void testFirstHit1() {		
		IntListHashMap m = new IntListHashMap(10);
		m.put(1, 1);
		m.put(1, 2);		
		assertEquals(2, m.getFirstHit(1, Integer.MAX_VALUE));		
	}
	
	@Test
	public void testFirstHit2() {		
		IntListHashMap m = new IntListHashMap(10);
		m.put(1, 1);
		m.put(1, 2);		
		m.put(11, 3);		
		assertEquals(2, m.getFirstHit(1, Integer.MAX_VALUE));		
	}
	
	public void testNoHit() {
		IntListHashMap m = new IntListHashMap(2);
		m.put(0, 1);
		m.put(1, 1);		
		assertEquals(-1, m.getFirstHit(2, Integer.MAX_VALUE));				
	}

}
