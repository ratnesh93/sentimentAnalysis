

package sentiment;
import rita.RiWordNet;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.Locale;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class temp {
	public static void main(String[] args) throws ClassNotFoundException, IOException {

		String str = "Hi !!! all ? my name. is r.c. Hope u I.E. are well The number is 2.2 and I am doing good.";
		String[] sentenceHolder = str.split("[/?|/.|/!][^a-zA-Z0-9]");
		for (int i = 0; i < sentenceHolder.length; i++) {
			System.out.println(sentenceHolder[i]);
		}	

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = "This is a test t.v. This is a T.L.A. test. Now with a Dr. in it. Hi !!! all ? my name. is r.c. Hope u I.E. are well The number is 2.2 and I am doing good.";
		iterator.setText(source);
		int start = iterator.first();
		for (int end = iterator.next();
		    end != BreakIterator.DONE;
		    start = end, end = iterator.next()) {
		  System.out.println(source.substring(start,end));
		}
		
		String a="abc";
		String b="def";
		String c=a+" "+b;
		System.out.println(c);
			
		//wordnet w;
		MaxentTagger tagger = new MaxentTagger("taggers/bidirectional-distsim-wsj-0-18.tagger");
		String reviewsentence="Iphone 6s sucks and is worst then nexus 6";
		String taggedReview = tagger.tagString(reviewsentence);
		System.out.println(taggedReview);
		
		RiWordNet wordnet = new RiWordNet("C:/Program Files (x86)/WordNet/2.1");
	//	RiWordNet wordnet=new RiWordNet();
		String[] antonyms=wordnet.getAllAntonyms("quirky", "a");
		String[] synonyms = wordnet.getAllSynonyms("quirky", "a");
		System.out.println("antonyms");
		for(int i=0;i<antonyms.length;i++){
			System.out.println(antonyms[i]);
		}
		System.out.println("synonmys");
		for(int i=0;i<synonyms.length;i++){
			System.out.println(synonyms[i]);
		}
		
	}

}
