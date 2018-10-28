/**
 * https://github.com/yasserg/crawler4j �� ���� �ڵ带 �ణ ������ ����
 */

import java.util.ArrayList;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController {

    public static ArrayList<MemberSet> member = new ArrayList<MemberSet>();

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
        	System.err.println("Usage: java " + BasicCrawlController.class.getName() + " <dir> <crawler-num>");
        	System.err.println("\tdir: it will contain intermediate crawl data");
        	System.err.println("\tcrawler-num: number of concurrent threads");
            return;
        }
        Member mb = new Member();
        member = mb.getMembers();

    /*
     * crawlStorageFolder is a folder where intermediate crawl data is
     * stored.
     */
        String crawlStorageFolder = args[0];

    /*
     * numberOfCrawlers shows the number of concurrent threads that should
     * be initiated for crawling.
     */
        int numberOfCrawlers = Integer.parseInt(args[1]);

        CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(crawlStorageFolder);

    /*
     * Be polite: Make sure that we don't send more than 1 request per
     * second (1000 milliseconds between requests).
     */
        config.setPolitenessDelay(1000);

    /*
     * You can set the maximum crawl depth here. The default value is -1 for
     * unlimited depth
     */
        config.setMaxDepthOfCrawling(1);

    /*
     * You can set the maximum number of pages to crawl. The default value
     * is -1 for unlimited number of pages
     */
        config.setMaxPagesToFetch(300);

        /**
         * Do you want crawler4j to crawl also binary data ?
         * example: the contents of pdf, or the metadata of images etc
         */
        config.setIncludeBinaryContentInCrawling(false);

    /*
     * Do you need to set a proxy? If so, you can use:
     * config.setProxyHost("proxyserver.example.com");
     * config.setProxyPort(8080);
     *
     * If your proxy also needs authentication:
     * config.setProxyUsername(username); config.getProxyPassword(password);
     */

    /*
     * This config parameter can be used to set your crawl to be resumable
     * (meaning that you can resume the crawl from a previously
     * interrupted/crashed crawl). Note: if you enable resuming feature and
     * want to start a fresh crawl, you need to delete the contents of
     * rootFolder manually.
     */
        config.setResumableCrawling(false);
        
        //config.setUserAgentString("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");

    /*
     * Instantiate the controller for this crawl.
     */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        
        //robots.txt �ؼ� ���� ����
        robotstxtConfig.setEnabled(false);
        
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

    /*
     * For each crawl, you need to add some seed urls. These are the first
     * URLs that are fetched and then the crawler starts following links
     * which are found in these pages
     */
        //controller.addSeed("http://www.sungkyul.ac.kr/");
        //controller.addSeed("http://computer.sungkyul.ac.kr/");
        //controller.addSeed("http://news.naver.com/main/main.nhn?mode=LSD&mid=shm&sid1=105");

        //controller.addSeed("https://ko.wikipedia.org/wiki/%EC%9C%84%ED%82%A4%EB%B0%B1%EA%B3%BC:%EB%8C%80%EB%AC%B8");
        //controller.addSeed("http://www.assembly.go.kr/assm/memact/congressman/memCond/memCondListAjax.do?rowPerPage=299");
        for(int i = 0; i < member.size(); i++) {
        	controller.addSeed("http://www.assembly.go.kr/assm/memPop/memPopup.do?dept_cd=" + member.get(i).num);
        	System.out.println("http://www.assembly.go.kr/assm/memPop/memPopup.do?dept_cd=" + member.get(i).num);
        }
        

    /*
     * Start the crawl. This is a blocking operation, meaning that your code
     * will reach the line after this only when crawling is finished.
     */
        BasicCrawler.configure(crawlStorageFolder);
        controller.start(BasicCrawler.class, numberOfCrawlers);
    }
}