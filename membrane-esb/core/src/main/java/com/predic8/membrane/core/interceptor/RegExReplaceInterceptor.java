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
package com.predic8.membrane.core.interceptor;

import java.io.IOException;

import javax.xml.stream.*;

import org.apache.commons.logging.*;

import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.http.Message;
import com.predic8.membrane.core.util.MessageUtil;

public class RegExReplaceInterceptor extends AbstractInterceptor {

	private static Log log = LogFactory.getLog(RegExReplaceInterceptor.class.getName());

	private String pattern;
	
	private String replacement;
	
	public RegExReplaceInterceptor() {
		name="Regex Replacer";
	}
	
	@Override
	public Outcome handleResponse(Exchange exc) throws Exception {
		replaceBody(exc.getResponse());
		return Outcome.CONTINUE;
	}
	
	@Override
	public Outcome handleRequest(Exchange exc) throws Exception {
		replaceBody(exc.getRequest());

		return Outcome.CONTINUE;
	}

	private void replaceBody(Message res) throws IOException, Exception {
		if (hasNoTextContent(res) ) return; 
		
		log.debug("pattern: " +pattern);
		log.debug("replacement: " +replacement);
		
		res.readBody();
		byte[] content = MessageUtil.getContent(res);
		res.setBodyContent(new String(content, res.getCharset()).replaceAll(pattern, replacement).getBytes(res.getCharset()));
		res.getHeader().removeFields("Content-Encoding");
	}

	private boolean hasNoTextContent(Message res) throws Exception {		
		return res.isBodyEmpty() || !res.isXML() && !res.isHTML();
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	
	@Override
	protected void writeInterceptor(XMLStreamWriter out)
			throws XMLStreamException {

		out.writeStartElement("regExReplacer");

		out.writeAttribute("regex", pattern);
		out.writeAttribute("replace", replacement);

		out.writeEndElement();
	}
	
	@Override
	protected void parseAttributes(XMLStreamReader token) {
		
		pattern = token.getAttributeValue("", "regex");
		replacement = token.getAttributeValue("", "replace");
	}
}
