
package RoAnalyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.io.FileReader;
import java.io.IOException;

public final class RoAnalyzer extends StopwordAnalyzerBase {
    private CharArraySet stemExclusionSet;

    //for default stopwords.txt file
    public RoAnalyzer() {
        this.stemExclusionSet = RomanianAnalyzer.getDefaultStopSet();
    }

    //for custom stopwords.txt file
    public RoAnalyzer(String swPath) {
        try {
            FileReader reader = new FileReader(swPath);
            this.stemExclusionSet = StopwordAnalyzerBase.loadStopwordSet(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        TokenStream result = new StandardFilter(source);

        result = new LowerCaseFilter(result);
        result = new ASCIIFoldingFilter(result);
        result = new StopFilter(result, this.stemExclusionSet);
        result = new SnowballFilter(result, new RomanianStemmer());

        return new TokenStreamComponents(source, result);
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }
}
