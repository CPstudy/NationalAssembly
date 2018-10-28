import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class ContentsParser {
	static String contents;
	static String site;
	static Document doc;
	
	ContentsParser(String contents, String site) {
		this.contents = contents;
		this.site = site;
		doc = Jsoup.parseBodyFragment(contents);
	}
	
	public ParserResult getArticle() {
		
		if(site.equals("네이버")) {
			return parseNaver();
		} else if(site.equals("다음")) {
			return parseDaum();
		} else if(site.equals("네이트")) {
			return parseNate();
		} else if(site.equals("동아일보")) {
			return parseDonga();
		} else if(site.equals("조선일보")) {
			return parseChosun();
		} else if(site.equals("중앙일보")) {
			return parseJoongang();
		} else if(site.equals("연합뉴스")) {
			return parseYonhap();
		} else {
			return null;
		}
	}
	
	private ParserResult parseNaver() {
		if(doc.select("div#articleBodyContents").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div#articleBodyContents").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseDaum() {
		if(doc.select("div#harmonyContainer.article_view").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div#harmonyContainer.article_view").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseNate() {
		if(doc.select("div#realArtcContents").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div#realArtcContents").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseDonga() {
		if(doc.select("div.article_txt").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div.article_txt").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseChosun() {
		if(doc.select("div.par").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div.par").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseJoongang() {
		if(doc.select("div#content").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div#content").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
	
	private ParserResult parseYonhap() {
		if(doc.select("div.article").size() > 0) {
			String title = doc.title().toString();
			Element article = doc.select("div.article").first();
			//System.err.println("[네이버]" + element.text());
			return new ParserResult(title, article.text());
		} else {
			return new ParserResult("undefined", "undefined");
		}
	}
}
