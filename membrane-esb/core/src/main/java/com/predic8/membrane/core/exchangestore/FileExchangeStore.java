/* Copyright 2009 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */

package com.predic8.membrane.core.exchangestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.predic8.membrane.core.Constants;
import com.predic8.membrane.core.exchange.AbstractExchange;
import com.predic8.membrane.core.http.Message;
import com.predic8.membrane.core.rules.Rule;
import com.predic8.membrane.core.rules.RuleKey;
import com.predic8.membrane.core.rules.StatisticCollector;
import com.predic8.membrane.core.util.TextUtil;

/**
 * The output file is UTF-8 encoded.
 */
public class FileExchangeStore extends AbstractExchangeStore {

	private static Log log = LogFactory.getLog(FileExchangeStore.class
			.getName());

	private static AtomicInteger counter = new AtomicInteger();

	private static final String DATE_FORMAT = "'h'hh'm'mm's'ss'ms'ms"; 
	
	private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>();

	private static final String separator = System
			.getProperty("file.separator");

	public static final String MESSAGE_FILE_PATH = "message.file.path";

	private String dir;

	private boolean raw;

	private File directory;

	private boolean saveBodyOnly = false;

	public void add(AbstractExchange exc) {
		int fileNumber;
		if (exc.getResponse() == null)
			fileNumber = counter.incrementAndGet();
		else
			fileNumber = counter.get();

		Message msg = exc.getResponse() == null ? exc.getRequest() : exc
				.getResponse();

		StringBuffer buf = getDirectoryNameBuffer(exc);

		directory = new File(buf.toString());
		directory.mkdirs();
		if (directory.exists() && directory.isDirectory()) {
			buf.append(separator);
			buf.append(getDateFormat().format(exc.getTime().getTime()));
			buf.append("-");
			buf.append(fileNumber);
			exc.setProperty(MESSAGE_FILE_PATH, buf.toString());
			buf.append("-");
			buf.append(exc.getResponse() == null ? "Request" : "Response");
			buf.append(".msg");
			try {
				writeFile(msg, buf.toString());
			} catch (Exception e) {
				log.error(e, e);
			}
		} else {
			log.error("Directory does not exists or file is not a directory: "+ buf.toString());
		}

	}

	private StringBuffer getDirectoryNameBuffer(AbstractExchange exc) {
		StringBuffer buf = new StringBuffer();
		buf.append(dir);
		buf.append(separator);
		buf.append(exc.getTime().get(Calendar.YEAR));
		buf.append(separator);
		buf.append((exc.getTime().get(Calendar.MONTH) + 1));
		buf.append(separator);
		buf.append(exc.getTime().get(Calendar.DAY_OF_MONTH));
		return buf;
	}
	
	private static DateFormat getDateFormat() {
		DateFormat df = dateFormat.get();
		if (df == null) {
			df = new SimpleDateFormat(DATE_FORMAT);
			dateFormat.set(df);
		}
		return df;
	}

	private void writeFile(Message msg, String path) throws Exception {
		File file = new File(path);
		file.createNewFile();

		FileOutputStream os = new FileOutputStream(file);
		try {
			if (raw || !saveBodyOnly) {
				msg.writeStartLine(os);
				msg.getHeader().write(os);
				os.write(Constants.CRLF_BYTES);
			}

			if (msg.isBodyEmpty())
				return;

			if (raw)
				os.write(msg.getBody().getRaw());
			else {
				if (msg.isXML())
					os.write(TextUtil.formatXML(
							new InputStreamReader(msg.getBodyAsStream(), msg
									.getHeader().getCharset()))
							.getBytes(Constants.UTF_8));
				else
					os.write(msg.getBody().getContent());
			}
		} finally {
			os.close();
		}
	}

	public AbstractExchange[] getExchanges(RuleKey ruleKey) {
		throw new RuntimeException(
				"Method getExchanges() is not supported by FileExchangeStore");
	}

	public int getNumberOfExchanges(RuleKey ruleKey) {
		throw new RuntimeException(
				"Method getNumberOfExchanges() is not supported by FileExchangeStore");
	}

	public void remove(AbstractExchange exchange) {
		throw new RuntimeException(
				"Method remove() is not supported by FileExchangeStore");
	}

	public void removeAllExchanges(Rule rule) {
		throw new RuntimeException(
				"Method removeAllExchanges() is not supported by FileExchangeStore");
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isRaw() {
		return raw;
	}

	public void setRaw(boolean raw) {
		this.raw = raw;
	}

	public StatisticCollector getStatistics(RuleKey ruleKey) {
		return null;
	}

	public Object[] getAllExchanges() {
		return null;
	}

	public Object[] getLatExchanges(int count) {
		return null;
	}

	public List<AbstractExchange> getAllExchangesAsList() {
		return null;
	}

	public void removeAllExchanges(AbstractExchange[] exchanges) {
		// ignore
	}

	public boolean isSaveBodyOnly() {
		return saveBodyOnly;
	}

	public void setSaveBodyOnly(boolean saveBodyOnly) {
		this.saveBodyOnly = saveBodyOnly;
	}

}
