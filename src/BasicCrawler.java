/**
 * https://github.com/yasserg/crawler4j �� ���� �ڵ带 �ణ ������ ����
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BasicCrawler extends WebCrawler {
	
	static int count = 0;
    
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern NUMBER_ONLY = Pattern.compile("(^[0-9]*$)");

    private static String storageDir;
    private static ArrayList<MemberSet> member = new ArrayList<MemberSet>();
    
    public static void configure(String dir) {
    	storageDir = dir;
    	member = BasicCrawlController.member;
    }
    
    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (IMAGE_EXTENSIONS.matcher(href).matches()) {
            return false;
        }
        
        //한겨례 크롤링
       /* String str = href.split(".html")[0];
        if(NUMBER_ONLY.matcher(str.substring(str.length() - 6, str.length())).matches()) {
        	// Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
        	if(href.startsWith("http://www.hani.co.kr/arti") && !href.contains("print")) {
        		return true;
        	} else {
        		return false;
        	}
        } else {
        	return false;
        }*/
        
        //연합뉴스 크롤링
        /*String str;
        try {
        	str = href.split("/")[4];
        } catch(Exception e) {
        	str = href;
        }
        System.err.println(str);
        if(NUMBER_ONLY.matcher(str).matches()) {
        	if(arrURL.contains(href)) {
            	System.err.println("중복 URL: " + href);
            	return false;
            } else if(href.startsWith("http://www.yonhapnews.co.kr")) {
                //리스트에 없으면 URL을 ArrayList에 저장
                arrURL.add(href);
            	return true;
            } else {
            	System.err.println("잘못된 URL " + href);
            	return false;
            }
        } else {
        	return false;
        }*/
        

        
        //리스트에 URL이 있으면 false
        /*if(arrURL.contains(href)) {
        	System.err.println("중복 URL: " + href);
        	return false;
        } else if(href.startsWith("http://news.naver.com") && href.contains("read.nhn")) {
            //리스트에 없으면 URL을 ArrayList에 저장
            arrURL.add(href);
        	return true;
        } else {
        	System.err.println("잘못된 URL " + href);
        	return false;
        }*/
        
        return href.startsWith("http://www.assembly.go.kr/assm/memPop/memPopup");
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();
        String domain = page.getWebURL().getDomain();
        String path = page.getWebURL().getPath();
        String subDomain = page.getWebURL().getSubDomain();
        String parentUrl = page.getWebURL().getParentUrl();
        String anchor = page.getWebURL().getAnchor();

        System.err.println("Docid: " + docid);
        System.err.println("URL: " + url);
        System.err.println("Domain: " + domain);
        System.err.println("Sub-domain: " + subDomain);
        System.err.println("Path: " + path);
        System.err.println("Parent page: " + parentUrl);
        System.err.println("Anchor text: " + anchor);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            //System.err.println("Text length: " + text.length());
            //System.err.println("Html length: " + html.length());
            //System.err.println("Number of outgoing links: " + links.size());
            
            //save the html file
            
            //System.err.println(html.toString());
            saveFile(html, docid, htmlParseData.getTitle().replaceAll("\\p{Punct}", ""), "html");
        }

        Header[] responseHeaders = page.getFetchResponseHeaders();
        if (responseHeaders != null) {
        	//System.err.println("Response headers:");
            for (Header header : responseHeaders) {
            	//System.err.println(String.format("\t%s: %s", header.getName(), header.getValue()));
            }
        }

        System.err.println("=============");
    }
    
    public void saveFile(String content, int docId, String title, String ext) {
    	String fname = storageDir + File.separator + member.get(count).num + "." + ext;
    	BufferedWriter writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fname), "UTF-8"));
	    	writer.write(content);
	    	writer.close();
	    	count++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
