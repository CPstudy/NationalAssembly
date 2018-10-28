import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Member {
	private final String DIR_MEMBER_FILE = StringStorage.MEMBER_LIST;
	ArrayList<MemberSet> arrMember = new ArrayList<MemberSet>();
	MemberSet member;
	
	Member() {
		File file = new File(DIR_MEMBER_FILE);
		Elements elements;
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			elements = doc.select("div.memberna_list dl");
			//System.out.println(elements.toString());
			
			for(Element element : elements) {
			    member = new MemberSet(
			    		element.select("dt").text().split(" ")[0],
			    		element.select("dt").text().split(" ")[1].replace("(", "").replace(")", ""),
			    		element.select("dt").text().replaceAll("[^a-zA-Z]", ""),
			    		element.select("strong").select("a").toString().replaceAll("[^0-9]", ""),
			    		element.select("dd.mt").text(),
			    		element.select("dd.ht").text()
			    		);
			    arrMember.add(member);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList getMembers() {
		return arrMember;
	}
}
