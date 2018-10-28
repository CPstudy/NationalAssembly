import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.parser.html.*;
import org.xml.sax.ContentHandler;


/*
 * Tika를 쓰지 않아 필요한 부분만 남겨두고 모두 지움
 */

public class Extractor {	
	public class Text {
		String title;
		String meta;
		String body;
		
		void setTitle(String s) { title = s; }
		void setMeta(String s) { meta = s; }
		void setBody(String s) { body = s; }
	}
}
