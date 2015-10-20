package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class featureExtraction {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		
		HashMap<String , Integer> hm =new HashMap();
		String productId;
		
		String sql = "select e.prodId,w.sentenceId,w.word,w.id from electronics_review e inner join (SELECT r.reviewId,t.sentenceId,t.word,t.id FROM tagwords t INNER JOIN reviewsentence r on t.sentenceId = r.id and (t.posTag='NN' OR t.posTag='NNS')) w on w.reviewId = e.id";
		
		
		Long sentenceId;
		String word;
		Long tagId;
		while (rs.next()) {
			sentenceId = rs.getLong("sentenceId");
			word = rs.getString("word");
			tagId = rs.getLong("tagId");
			
			
		}
		stmt.close();
		conn.close();
		
	}

}
