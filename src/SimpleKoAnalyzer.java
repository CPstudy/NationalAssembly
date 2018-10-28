import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;


/*
 * 한국어 처리를 위한 Analyzer 구현 샘플 코드
 */

public class SimpleKoAnalyzer extends Analyzer {

	protected TokenStreamComponents createComponents(String fieldName) {
		Tokenizer tok = new WhitespaceTokenizer();
		TokenFilter filter = new SimpleKoStemFilter(tok);
		return new TokenStreamComponents(tok, filter);
	}
}
