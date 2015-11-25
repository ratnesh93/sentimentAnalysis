//step 2
//now tagging the sentences using stanFord post tagger
package sentiment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class TagText {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	static void findPOC(String s, Long sentenceId) throws ClassNotFoundException, SQLException {

		String[] Stringparts = s.split("/");

		Connection conn = null;
		Statement stmt = null;
		java.sql.PreparedStatement ps = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "INSERT INTO tagwords VALUES(NULL,?,?,?)";
		ps = conn.prepareStatement(sql);
		ps.setString(1, Stringparts[0]);
		ps.setString(2, Stringparts[1]);
		ps.setLong(3, sentenceId);
		ps.executeUpdate();
		stmt.close();
		conn.close();
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

		// Initialize the tagger
		MaxentTagger tagger = new MaxentTagger("taggers/bidirectional-distsim-wsj-0-18.tagger");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "SELECT id,sentence from reviewsentence";
		rs = stmt.executeQuery(sql);

		String reviewsentence;
		String taggedReview;
		Long id;
		while (rs.next()) {
			id = rs.getLong("id");
			reviewsentence = rs.getString("sentence");
			reviewsentence = reviewsentence.replaceAll("[^a-zA-Z0-9 '-]", " ");
			taggedReview = tagger.tagString(reviewsentence);
			StringTokenizer st = new StringTokenizer(taggedReview);
			while (st.hasMoreTokens()) {
				String term = st.nextToken();
				System.out.println(term);
				findPOC(term, id);
			}
		}

	}
}
