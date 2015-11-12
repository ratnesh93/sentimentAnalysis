package sentiment;
import rita.RiWordNet;

public class wordnet2 {

	public static void main(String[] args){
		RiWordNet wordnet = new RiWordNet("C:/Program Files (x86)/WordNet/2.1");
		String[] antonyms=wordnet.getAllAntonyms("good", "a");
		String[] synonyms = wordnet.getAllSynonyms("good", "a");
		for(int i=0;i<antonyms.length;i++){
			System.out.println(antonyms[i]);
		}
		System.out.println("synonmys");
		for(int i=0;i<synonyms.length;i++){
			System.out.println(synonyms[i]);
		}
	}
}
