import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

public class SearchPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	
	static ResultSet rs;
	static ArrayList<ResultSet> arrList = new ArrayList<ResultSet>();
	
	static IndexSearcher searcher;	//주어진 IndexReader를 이용하여 검색 수행
	static IndexReader reader;	//IndexReader의 서브 클래스로서, Directory에 저장된 색인을 읽어들임
	static Highlighter highlighter;
	static Fragmenter fragmenter;
	static QueryParser parser;		//사용자 입력을 처리하여 내부 질의를 구성
	static QueryScorer scorer;
	static Query query;
	
	
	static JLabel lblSearchText;
	static JLabel txtCount;
	static JScrollPane jsc;
	static JScrollBar sbar;
	static JTextArea txtResult;
	static JPanel resultbox;
	static ResultBoxPanel box[];
	static MemberBoxPanel mbox[];
	
	static String[] args = new String[1];
	static String[] name;
	static String strSearchText;
	static String realSearchText;	// 원본 검색어
	static ArrayList<Integer> arrName = new ArrayList<Integer>();		// 검색어에 입력된 국회의원 이름 목록
	
	static boolean boolExist = false;
	
	public SearchPanel(String text, JPanel pan) {
		setBounds(0, 0, 784, 481);
		setLayout(null);
		
		arrList.clear();
		arrName.clear();
		name = MainFrame.name;
		
		lblSearchText = new JLabel("검색 결과: ");
		lblSearchText.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		lblSearchText.setForeground(Color.WHITE);
		lblSearchText.setBounds(24, 20, 641, 20);
		add(lblSearchText);
		
		JViewport viewport = new JViewport();
		viewport.setView(new JPanel());
		viewport.setOpaque(false);
		
		jsc = new JScrollPane();
		jsc.setViewport(viewport);
		jsc.getViewport().setOpaque(false);
		
		JButton btnBack = new JButton("뒤로가기");
		btnBack.setBounds(675, 31, 97, 23);
		btnBack.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				goBack(pan);
			}
			
		});
		add(btnBack);
		
		txtCount = new JLabel("0개");
		txtCount.setForeground(Color.WHITE);
		txtCount.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
		txtCount.setBounds(24, 40, 641, 20);
		add(txtCount);
		
		jsc.setOpaque(false);
		jsc.setBorder(BorderFactory.createEmptyBorder());
		sbar = jsc.getVerticalScrollBar();
		
		txtResult = new JTextArea(6, 30);
		txtResult.setForeground(Color.WHITE);
		txtResult.setEditable(false);
		txtResult.setOpaque(false);
		
		args[0] = StringStorage.SAVE_INDEXED_FILE;
		strSearchText = text;
		realSearchText = text;
		
		try {
			startSearch(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		box = new ResultBoxPanel[arrList.size()];
		resultbox = new JPanel();
		resultbox.setOpaque(false);
		resultbox.setLayout(new GridLayout(0, 1, 0, 0));
		if(!boolExist) {
			jsc.setBounds(10, 71, 760, 400);
		} else {
			jsc.setBounds(392, 71, 380, 400);
		}
		
		int panelWidth = jsc.getWidth() - (int)jsc.getVerticalScrollBar().getPreferredSize().getWidth();
		
		for(int i = 0; i < box.length; i++) {
			//System.out.println(arrList.get(i).contents);
			box[i] = new ResultBoxPanel(arrList.get(i).title, arrList.get(i).article, arrList.get(i).filename, panelWidth);
			resultbox.add(box[i]);
		}
		
		Dimension size = new Dimension();// 사이즈를 지정하기 위한 객체 생성
		size.setSize(panelWidth, box.length * 125 + 5);// 객체의 사이즈를 지정
		resultbox.setPreferredSize(size);// 사이즈 정보를 가지고 있는 객체를 이용해 패널의 사이즈 지정
		jsc.setViewportView(resultbox);
		jsc.getVerticalScrollBar().setUnitIncrement(10);
		jsc.getViewport().setOpaque(false);
		jsc.setViewportBorder(null);
		add(jsc);
		
		sbar.setValue(0);
		

		// 국회의원 목록 부분

		mbox = new MemberBoxPanel[arrName.size()];
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 71, 368, 400);
		add(scrollPane);
		
		int panelWidth2 = scrollPane.getWidth() - (int)scrollPane.getVerticalScrollBar().getPreferredSize().getWidth();
		Dimension size2 = new Dimension();// 사이즈를 지정하기 위한 객체 생성
		size2.setSize(panelWidth2, mbox.length * 145 + 5);// 객체의 사이즈를 지정
		
		JPanel memberpanel = new JPanel();
		memberpanel.setLayout(new GridLayout(0, 1, 0, 0));
		memberpanel.setOpaque(false);
		scrollPane.setViewportView(memberpanel);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setViewportBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		
		for(int i = 0; i < mbox.length; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append("<html>");
			sb.append(MainFrame.arrMember.get(arrName.get(i)).chinese);
			sb.append("<br>");
			sb.append(MainFrame.arrMember.get(arrName.get(i)).english);
			sb.append("<br>");
			sb.append(MainFrame.arrMember.get(arrName.get(i)).election);
			sb.append("<br>");
			sb.append(MainFrame.arrMember.get(arrName.get(i)).locale);
			sb.append("<br>");
			sb.append("</html>");
			mbox[i] = new MemberBoxPanel(MainFrame.arrMember.get(arrName.get(i)).korean, sb.toString(), MainFrame.arrMember.get(arrName.get(i)).num, panelWidth2 - 10);
			memberpanel.add(mbox[i]);
		}
		scrollPane.setViewportView(memberpanel);
		memberpanel.setPreferredSize(size2);
		
		JLabel imgBackground = new JLabel();
		imgBackground.setText("(沈相奵) \r\nSIM SANGJEUNG\r\n3선\r\n경기 고양시갑");
		imgBackground.setIcon(new ImageIcon(SearchPanel.class.getResource("/img/bg_main.jpg")));
		imgBackground.setBounds(0, 0, 784, 481);
		add(imgBackground);
		
	}
	
	public void goBack(JPanel pan) {
		setVisible(false);
		pan.remove(this);
		pan.setVisible(true);
	}
	
	public void startSearch(String[] args) throws Exception {
		
		if (args.length != 1) {
			throw new Exception("Usage: java " + SearchPanel.class.getName() + " <index-dir>");
		}
		
		Path indexDir = Paths.get(args[0]);
		if (!Files.exists(indexDir) || !Files.isDirectory(indexDir)) {
			throw new Exception(indexDir + " does not exist or is not a directory.");
		}
		
		for(int i = 0; i < name.length; i++) {
			if(strSearchText.contains(name[i])) {
				// 국회의원 목록에 한글 이름이 있으면 이름 목록에 추가
				arrName.add(i);
			} else {
				// 없으면 false
			}
		}
		
		if(arrName.size() <= 0) {
			boolExist = false;
		} else {
			boolExist = true;
		}

		if(!boolExist) {
			StringBuffer tsb = new StringBuffer();
			tsb.append(strSearchText);
			tsb.append(" AND (");
			for(int i = 0; i < name.length; i++) {
				tsb.append(name[i]);
				if(i != name.length - 1) {
					tsb.append(" OR ");
				}
			}
			tsb.append(")");
			strSearchText = tsb.toString();
		}
		System.err.println(strSearchText);
		
		init(indexDir);
		
		// 반복적으로 검색어 입력 받아 검색 수행
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));	
		long start = new Date().getTime();
		TopDocs tdocs = search(strSearchText, 100);
		
		scorer = new QueryScorer(query);
		highlighter = new Highlighter(scorer);
		if(!boolExist) {
			fragmenter = new SimpleSpanFragmenter(scorer, 55);
		} else {
			fragmenter = new SimpleSpanFragmenter(scorer, 20);
		}
		highlighter.setTextFragmenter(fragmenter);
		
		long end = new Date().getTime();
		
		txtResult.setText("총 검색 문서 " + tdocs.totalHits + "개 중 상위 " + tdocs.scoreDocs.length + "개 결과(질의: '" + strSearchText + "', 검색 경과 시간: " + (end-start) + " milliseconds): " + "\n");

		printSearchResult(tdocs);
		
		closeIndexReader();
	}
	
	//초기화
		public static void init(Path indexDir) throws Exception {
			Directory directory = FSDirectory.open(indexDir);
			reader = DirectoryReader.open(directory);
			searcher = new IndexSearcher(reader);
			parser = new QueryParser("article", new SimpleKoAnalyzer());
		}

		//종료 시, Closing
		public static void closeIndexReader() throws Exception {
			reader.close();
		}
		
		//검색
		public static TopDocs search(String q, int n) throws Exception {
			//색인이 변경됐는지 확인
			DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader);
			if (newReader != null) { //색인이 변경된 경우
				closeIndexReader();
				reader = newReader;
			}
			
			//질의 생성
			//Query: 사용자 입력 처리 결과 생성된 내부 질의
			query = parser.parse(q);
			
			
			//검색
			return searcher.search(query, n);
		}
		
		//검색결과 출력: 문서 제목이 출력되도록 변경
		public static void printSearchResult(TopDocs tdocs) throws Exception {
			for (int i = 0; i < tdocs.scoreDocs.length; i++) {
				ScoreDoc match = tdocs.scoreDocs[i];
				Explanation exp = searcher.explain(query, match.doc);
				Document doc = searcher.doc(tdocs.scoreDocs[i].doc);
				TokenStream stream = TokenSources.getAnyTokenStream(reader, tdocs.scoreDocs[i].doc, "article", new SimpleKoAnalyzer());
				String[] frags = highlighter.getBestFragments(stream, doc.get("article"), 7);
				
				rs = new ResultSet(doc.get("title"), frags, doc.get("filename"), tdocs.scoreDocs[i].score, "");
				arrList.add(rs);
				//txtResult.append("[" + (i+1) + "] " + doc.get("title") + " (" + tdocs.scoreDocs[i].score + ")" + "\n");
			}
			
			if(!boolExist) {
				lblSearchText.setText(realSearchText + "에 관련된 모든 국회의원 검색 결과");
				txtCount.setText(tdocs.scoreDocs.length + "개");
			} else {
				lblSearchText.setText(realSearchText + "에 관련된 검색 결과");
				txtCount.setText(tdocs.scoreDocs.length + "개");
			}
		}
}
