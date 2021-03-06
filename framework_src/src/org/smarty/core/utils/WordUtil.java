package org.smarty.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;

/**
 * Microsoft Word工具(未完待续)
 * Created Date 2015/04/09
 *
 * @author quliang
 * @version 1.0
 */
public class WordUtil {
	private static Log logger = LogFactory.getLog(WordUtil.class);

	private WordUtil() {

	}


	/**
	 * 以File对象初始化Word读取器
	 *
	 * @return Word读取器
	 */
	public static ReadWord instanceRead(String fileName) throws IOException {
		try {
			return instanceRead(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			logger.warn(e);
			throw e;
		}
	}

	/**
	 * 以File对象初始化Word读取器
	 *
	 * @return Word读取器
	 */
	public static ReadWord instanceRead(File file) throws IOException {
		try {
			return instanceRead(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.warn(e);
			throw e;
		}
	}

	/**
	 * 以File对象初始化Word读取器
	 *
	 * @return Word读取器
	 */
	public static ReadWord instanceRead(InputStream is) throws IOException {
		return new ReadWord(is);
	}

	/**
	 * Word读取器
	 */
	public static class ReadWord {
		private InputStream is;
		private HWPFDocument we;

		private ReadWord(InputStream is) throws IOException {
			if (is == null)
				return;
			try {
				this.is = is;
				we = new HWPFDocument(is);
			} catch (IOException e) {
				logger.warn(e);
				throw e;
			}
		}

		/**
		 * 获得所有内容
		 *
		 * @return word
		 */
		public String getTextAll() {
			Range r = we.getRange();
			StringBuilder sb = new StringBuilder();
			for (int i = 0, len = r.numParagraphs(); i < len; i++) {
				Paragraph p = r.getParagraph(i);
				sb.append(p.text());

			}
			return sb.toString();
		}

		/**
		 * 获得内容
		 *
		 * @param paragraph 段落
		 * @return 内容
		 */
		public String getText(int paragraph) {
			Range r = we.getRange();
			Paragraph p = r.getParagraph(paragraph);
			return p.text();
		}

		/**
		 * 关闭操作流
		 */
		public void close() {
			if (is == null)
				return;
			try {
				is.close();
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

}
