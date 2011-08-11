/* Copyright 2011 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.membrane.core.interceptor.groovy;

import junit.framework.TestCase;

import org.junit.Test;

import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.http.Request;
import com.predic8.membrane.core.interceptor.Outcome;

public class GroovyInterceptorTest extends TestCase {
		
	@Test
	public void testRequest() throws Exception {
		Exchange exc = new Exchange();
		exc.setRequest(new Request());
		
		GroovyInterceptor i = new GroovyInterceptor();
		
		i.setSrc("exc.setProperty('foo', 'bar')\n"+
				 "CONTINUE");
		
		assertEquals(Outcome.CONTINUE, i.handleRequest(exc));
		
		assertEquals("bar", exc.getProperty("foo"));
		
	}
	
}
