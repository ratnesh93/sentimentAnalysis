package sentiment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class partofspeech {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Connection conn = null;
		Statement stmt = null;
		java.sql.PreparedStatement ps = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String[] pos = { "Coordinate conjuction", "Cardinal number", "Determiner", "Existential there", "Foriegn word",
				"Preposition or subordinating conjuction", "Adjective", "Adjective, comparative",
				"Adjective, suoerlative", "List item marker", "Modal", "Noun, singular or mass", "Noun, plural",
				"Proper Noun, singular", "Proper Noun, plural", "Predeterminer", "Possessive ending",
				"Personal pronoun", "Possessive pronoun", "Adverb", "Adverb, comparative", "Adverb, superlative",
				"Particle", "Symbol", "to", "Interjection", "Verb, base form", "Verb, past tense",
				"Verb, gerund or present participle", "Verb, past participle", "Verb, non-3rd person singular present",
				"Verb, 3rd person singular present", "Wh-determiner", "Wh-pronoun", "Possessive wh-pronoun",
				"Wh-adverb", "", "" };
		String[] tag = { "CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NP", "NPS",
				"PDT", "POS", "PP", "PP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN",
				"VBP", "VBZ", "WDT", "WP", "WP$", "WRB" };

		String sql = "INSERT INTO partofspeech VALUES(NULL,?,?)";
		ps = conn.prepareStatement(sql);

		for (int i = 0; i < pos.length; i++) {
			System.out.println("\t"+pos[i]+"\t\t"+tag[i]);
			ps.setString(1, pos[i]);
			ps.setString(2, tag[i]);
			ps.executeUpdate();
		}

		stmt.close();
		conn.close();
	}
}
