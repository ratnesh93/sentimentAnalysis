package sentiment;

import java.text.BreakIterator;
import java.util.Locale;

public class temp {
	public static void main(String[] args) {

		String str = "Hi !!! all ? my name. is r.c. Hope u I.E. are well The number is 2.2 and I am doing good.";
		String[] sentenceHolder = str.split("[/?|/.|/!][^a-zA-Z0-9]");
		for (int i = 0; i < sentenceHolder.length; i++) {
			System.out.println(sentenceHolder[i]);
		}	

		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = "This is a test. This is a T.L.A. test. Now with a Dr. in it. Hi !!! all ? my name. is r.c. Hope u I.E. are well The number is 2.2 and I am doing good.";
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
			
	}

}
