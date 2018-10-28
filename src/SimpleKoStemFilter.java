import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class SimpleKoStemFilter extends TokenFilter {
	private static final String[] endings = {
		"입니다.", "이어요.", "습니다.", "가요?"
	};

	//토큰 문자열
	private final CharTermAttribute termAtt;

	public SimpleKoStemFilter(TokenStream input) {
		super(input);
		termAtt = addAttribute(CharTermAttribute.class);
	}

	//IndexWriter가 이 함수를 이용하여 다음 토큰에 억세스함
	public boolean incrementToken() throws IOException {
		//더 이상 토큰이 없으면: return false;
		if (!input.incrementToken())
			return false;

		//여기에서 토큰의 다양한 속성을 세팅함

		//토큰 문자열 속성 변경: 문자열 내용을 stem으로 대체
		String stem = stem(termAtt.toString());
		termAtt.copyBuffer(stem.toCharArray(), 0, stem.length());

		//이외에도 토큰 타입 속성(TypeAttribute), 시작/종료위치(OffsetAttribute) 등
		//필요하다면 다양한 토큰 속성을 수정함
		
		return true;
	}

	//스테밍
	private String stem(String term) {
		int longestLeng = 0;
		int longestId = -1;
		
		//endings로 끝나는 단어인 경우,
		//매칭된 endings 중 가장 긴 것을 삭제하여 스테밍.
		for (int i=0; i<endings.length; i++) {
			if (term.endsWith(endings[i])) {
				longestLeng = endings[i].length();
				longestId = i;
			}
		}
		
		if (longestLeng == 0 || longestId == -1)
			return term;
		else
			return term.substring(0, term.length() - longestLeng);
	}
}
