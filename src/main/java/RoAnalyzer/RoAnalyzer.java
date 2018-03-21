
package RoAnalyzer;

import java.io.IOException;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

public final class RoAnalyzer extends StopwordAnalyzerBase {
    private final CharArraySet stemExclusionSet;
    public static final String DEFAULT_STOPWORD_FILE = "stopwords.txt";
    private static final String STOPWORDS_COMMENT = "#";

    public RoAnalyzer() {
        //this(RoAnalyzer.DefaultSetHolder.DEFAULT_STOP_SET);
        //this.stemExclusionSet = RomanianAnalyzer.getDefaultStopSet();
        try {
            this.stemExclusionSet = RoAnalyzer.loadStopwordSet(false, RomanianAnalyzer.class, DEFAULT_STOPWORD_FILE, STOPWORDS_COMMENT);
        } catch (IOException var1) {
            throw new RuntimeException("Unable to load default stopword set");
        }
    }

    public RoAnalyzer(CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }

    public RoAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream result = new StandardFilter(source);
        result = new LowerCaseFilter(result);
        result = new SnowballFilter(result, new RomanianStemmer());
        result = new ASCIIFoldingFilter(result);
        result = new StopFilter(result, this.stopwords);
        if (!this.stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter(result, this.stemExclusionSet);
        }


        return new TokenStreamComponents(source, result);
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }
}
