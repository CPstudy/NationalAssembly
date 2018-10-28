import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.awt.Color;
import javax.swing.*;

public class IndexerPanel extends JPanel implements Runnable {	
	static IndexWriter writer; //IndexWriter가 색인 생성을 위한 메인 클래스임
	static Extractor tika;
	
	static JScrollPane jsc;
	static JTextArea txtResult;
	static JScrollBar sbar;
	static JButton btnStartIndexing;
	static JButton btnBack;
	static JLabel lblProgress;
	private JTextField txtIndexedFiles;
	private JTextField txtCrawling;
	
	static String[] arguments = new String[2];
	static String[] name;
	static String result = "";
	static boolean boolStart;
	
	static int fileList = 0;
	static int maxFile = 0;
	
	static int success = 0;
	static int failure = 0;
	
	/**
	 * Create the panel.
	 */
	public IndexerPanel(JPanel pan) {
		setBounds(0, 0, 784, 481);
		setLayout(null);
		
		JLabel label = new JLabel("색인");
		label.setForeground(Color.WHITE);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		label.setBounds(353, 30, 78, 32);
		add(label);
		
		JViewport viewport = new JViewport();
		viewport.setView(new JPanel());
		viewport.setOpaque(false);
		
		jsc = new JScrollPane();
		jsc.setViewport(viewport);
		jsc.getViewport().setOpaque(false);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(0, 0, 0, 0));
		panel_1.setBounds(34, 72, 715, 72);
		//panel_1.setLayout(null);
		add(panel_1);
		panel_1.setLayout(null);
		
		JLabel label_1 = new JLabel("색인 결과 위치");
		label_1.setBounds(0, 8, 80, 15);
		label_1.setForeground(Color.WHITE);
		panel_1.add(label_1);
		
		txtIndexedFiles = new JTextField();
		txtIndexedFiles.setBounds(92, 5, 623, 21);
		txtIndexedFiles.setColumns(10);
		txtIndexedFiles.setText(StringStorage.SAVE_INDEXED_FILE);
		txtIndexedFiles.setEnabled(false);
		panel_1.add(txtIndexedFiles);
		
		JLabel label_2 = new JLabel("수집 파일 위치");
		label_2.setHorizontalAlignment(SwingConstants.LEFT);
		label_2.setForeground(Color.WHITE);
		label_2.setBounds(0, 44, 80, 15);
		panel_1.add(label_2);
		
		txtCrawling = new JTextField();
		txtCrawling.setColumns(10);
		txtCrawling.setBounds(92, 41, 623, 21);
		txtCrawling.setText(StringStorage.CRAWLER_RESULT);
		txtCrawling.setEnabled(false);
		panel_1.add(txtCrawling);
		
		jsc.setOpaque(false);
		jsc.setBorder(BorderFactory.createEmptyBorder());
		txtResult = new JTextArea(6, 30);
		txtResult.setForeground(Color.WHITE);
		txtResult.setEditable(false);
		txtResult.setOpaque(false);
		jsc.setBounds(34, 154, 715, 284);
		jsc.setViewportView(txtResult);
		sbar = jsc.getVerticalScrollBar();
		add(jsc);
		
		JPanel panel = new JPanel();
		panel.setBounds(34, 154, 715, 284);
		panel.setBackground(new Color(255, 255, 255, 100));
		add(panel);
		
		btnBack = new JButton("뒤로가기");
		btnBack.setBounds(652, 39, 97, 23);
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				goBack(pan);
			}
			
		});
		add(btnBack);
		
		btnStartIndexing = new JButton("색인 시작");
		btnStartIndexing.setBounds(652, 448, 97, 23);
		btnStartIndexing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				arguments[0] = txtIndexedFiles.getText().toString();
				arguments[1] = txtCrawling.getText().toString();
				try {
					boolStart = true;					
					startThread();
					//startIndexing(arguments);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					System.out.println("쓰레드 오류");
					e1.printStackTrace();
				}
			}
		});
		add(btnStartIndexing);
		
		lblProgress = new JLabel("성공: 0 / 실패: 0 / 완료: 0 / 전체: 0");
		lblProgress.setForeground(Color.WHITE);
		lblProgress.setBounds(34, 452, 606, 15);
		add(lblProgress);
		
		JLabel imgBackground = new JLabel();
		imgBackground.setIcon(new ImageIcon(MainFrame.class.getResource("/img/bg_main.jpg")));
		imgBackground.setBounds(0, 0, 784, 481);
		add(imgBackground);
	
	}
	
	public void goBack(JPanel pan) {
		setVisible(false);
		pan.remove(this);
		pan.setVisible(true);
	}
	
	public void startThread() {
		Thread t = new Thread(this);
		t.start();
	}
	
	//IndexWriter 초기화
	public static void init(Path indexDir) throws Exception {
		//IndexWriter 환경설정
		//기본 Analyzer로 사용할 Analyzer를 설정함
		IndexWriterConfig config = new IndexWriterConfig(new SimpleKoAnalyzer());
		
		//<index-dir>에 색인 새로 생성, 이미 있으면 덮어씀
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		//<index-dir>에 색인이 있으면 그걸 오픈해서 업데이트, 없으면 새로 생성
		//config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

		//IndexWriter 생성
		writer = new IndexWriter(FSDirectory.open(indexDir), config);
		tika = new Extractor();
	}
	
	//파일 하나 색인
	public static void indexAFile(File file) throws Exception {
		boolean b = true;
		
		// 0. 전처리: Tika 사용하여 텍스트 추출
		//Extractor.Text text = tika.new Text();
		//tika.extractTitleBody(file.getCanonicalPath(), text);
		
        BufferedReader reader = new BufferedReader((new InputStreamReader(new FileInputStream(file),"EUC-KR")));
        StringBuffer sb = new StringBuffer();
        String news = "";

        String line = null;
        while ((line = reader.readLine()) != null) {
        	sb.append(line);
        	sb.append("\n");
        }
        reader.close();

		// 1. 비어 있는 새 Document 생성
		Document doc = new Document();
		String article1 = sb.toString();
		String article2 = new String(article1.getBytes("EUC-KR"), "UTF-8");
		ParserResult pr;
		
		//System.out.println(sb2.toString());
		
		if(sb.toString().contains("content=\"http://news.naver.com")) {
			// 네이버
			news = "네이버";
			pr = new ContentsParser(article1, news).getArticle();
		} else if(sb.toString().contains("content=\"http://v.media.daum.net")) {
			// 다음
			news = "다음";
			pr = new ContentsParser(article2.toString(), news).getArticle();
		} else if(sb.toString().contains("content=\"http://news.nate.com")) {
			// 네이트
			news = "네이트";
			pr = new ContentsParser(article1.toString(), news).getArticle();
		} else if(sb.toString().contains("content='http://news.donga.com")) {
			// 동아일보
			news = "동아일보";
			pr = new ContentsParser(article2.toString(), news).getArticle();
		} else if(sb.toString().contains("content=\"http://news.chosun.com")) {
			// 조선일보
			news = "조선일보";
			pr = new ContentsParser(article1.toString(), news).getArticle();
		} else if(sb.toString().contains("content=\"http://news.joins.com")) {
			// 중앙일보
			news = "중앙일보";
			pr = new ContentsParser(article2.toString(), news).getArticle();
		} else if(sb.toString().contains("content=\"http://www.yonhapnews.co.kr")) {
			// 연합뉴스
			news = "연합뉴스";
			pr = new ContentsParser(article2.toString(), news).getArticle();
		} else {
			news = "알 수 없음";
			b = false;
			pr = new ParserResult("undefined", "undefined");
		}
		
		if(b && !pr.article.equals("undefined")) {
			
			// 2. Field 생성하여 Document에 추가
			//Tika에서 추출한 제목과 본문을 이용하여 각각 제목 필드와 본문 필드 생성
					
			
			//본문 필드
			//TextField이므로 토큰 분리하여 색인함
			//Field.Store 값이 지정되지 않았으므로 원본문자열은 저장되지 않음.
			//doc.add(new TextField("title", new FileReader(file)));
			//doc.add(new TextField("contents", new FileReader(file)));
			//doc.add(new TextField("title", text.title, Field.Store.YES));
			//doc.add(new TextField("contents", text.body, Field.Store.YES));
			doc.add(new TextField("title", pr.title, Field.Store.YES));
			doc.add(new TextField("article", pr.article, Field.Store.YES));
			
			//doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))));
	
			//파일이름 필드
			//StringField이므로 토큰 분리하지 않고 색인함
			//Field.Store.YES이면 Analyzer를 거치지 않은 원본문자열을 저장함.
			doc.add(new StringField("filename", file.getCanonicalPath(), Field.Store.YES));
			
			// 3. 이 문서를 분석/처리하여 색인에 추가
			//Lucene 내부에서 토큰분리, 불용어제거, 스테밍 등 텍스트 처리 작업이 수행됨
			//신규 색인을 생성하는 경우
			if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
				txtResult.append("Adding [" + news + "] " + file.getCanonicalPath() + "\n");
				System.out.println("Adding [" + news + "] " + file.getCanonicalPath());
				sbar.setValue(sbar.getMaximum());
				writer.addDocument(doc);
			}
			// 기존 색인을 업데이트하는 경우: 동일한 문서가 색인에 존재할 경우 색인 정보를 이 문서 정보로 대체함
			else {
				txtResult.append("Updating [" + news + "] " + file.getCanonicalPath() + "\n");
				System.out.println("Updating [" + news + "] " + file.getCanonicalPath());
				sbar.setValue(sbar.getMaximum());
				writer.updateDocument(new Term("filename", file.getCanonicalPath()), doc);
			}
			success++;
		} else {
			txtResult.append("Failure [" + news + "] " + file.getCanonicalPath() + "\n");
			System.out.println("Failure [" + news + "] " + file.getCanonicalPath());
			failure++;
		}
		fileList++;
		lblProgress.setText("성공: " + success + " / 실패: " + failure + " / 완료: " + fileList + " / 전체: " + maxFile);
	}

	//색인
	public static void index(File file) throws Exception {
		// 디렉토리이면 파일 리스트를 뽑아내어 파일이름 순으로 각 파일을 모두 색인
		if (file.isDirectory()) { 
			String[] files = file.list();
			Arrays.sort(files);
			
			maxFile = files.length;
			
            for (int i = 0; i < files.length; i++)
				index(new File(file, files[i]));
		}
		// 일반 파일이면
		else
			indexAFile(file);
	}
	
	public static void startIndexing(String[] args) throws Exception {
		failure = 0;
		success = 0;
		fileList = 0;
		btnStartIndexing.setEnabled(false);
		btnBack.setEnabled(false);
		if (args.length != 2) {
			boolStart = false;
			
			txtResult.append("Usage: java " + IndexerPanel.class.getName() + " <index-dir> <doc-dir>" + "\n");
			System.out.println("Usage: java " + IndexerPanel.class.getName() + " <index-dir> <doc-dir>");
			sbar.setValue(sbar.getMaximum());
			throw new Exception ("Usage: java " + IndexerPanel.class.getName() + " <index-dir> <doc-dir>");
		}
		Path indexDir = Paths.get(args[0]);
		File docDir = new File(args[1]);
		
		if (!docDir.exists() || !docDir.isDirectory()) {
			boolStart = false;
			txtResult.append(docDir + "(이)가 존재하지 않거나 디렉토리가 아님.");
			System.out.println(docDir + "(이)가 존재하지 않거나 디렉토리가 아님.");
			sbar.setValue(sbar.getMaximum());
			throw new IOException(docDir + "(이)가 존재하지 않거나 디렉토리가 아님.");
		}
		
		long start = new Date().getTime();

		//IndexWriter 초기화
		init(indexDir);
		
		//색인
		index(docDir);
		int numIndexed = writer.numDocs();
		
		//색인 변경사항을 저장하고 관련 파일들을 클로징
		writer.close();
		
		long end = new Date().getTime();
		
		System.out.println("색인 문서 수: " + numIndexed);
		System.out.println("색인 경과 시간: " + (end-start) + " milliseconds");
		
		txtResult.append("총 문서 수: " + maxFile + "\n");
		txtResult.append("색인 문서 수: " + numIndexed + "\n");
		txtResult.append("색인 실패 수: " + failure + "\n");
		txtResult.append("색인 경과 시간: " + (end-start) + " milliseconds" + "\n");
		sbar.setValue(sbar.getMaximum());
		btnBack.setEnabled(true);
		boolStart = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(boolStart) {
			try {
				startIndexing(arguments);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				boolStart = false;
				btnStartIndexing.setEnabled(true);
			}
		}
	}
}
