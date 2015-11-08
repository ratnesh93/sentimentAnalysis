package sentiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class creatingFeatureSetForAssociationMining {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null, stmt2 = null;
		ResultSet rs = null, rs2 = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();

		String sql = "SELECT distinct prodId FROM electronics_review";
		rs = stmt.executeQuery(sql);

		String productId = "";
		while (rs.next()) {
			BufferedWriter writer = null;
			productId = rs.getString("prodId");

			sql = "Select w.sentenceId,w.word from electronics_review e inner join (SELECT r.reviewId,t.sentenceId,t.word,t.id FROM tagwords t INNER JOIN reviewsentence r on t.sentenceId = r.id and (t.posTag='NN' OR t.posTag='NNS')) w on w.reviewId = e.id and e.prodId="
					+ "'" + productId + "'";

			rs2 = stmt2.executeQuery(sql);
			writer = new BufferedWriter(
					new FileWriter("C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/featureSetProductWise/input" + productId + ".txt"));
			String word = "";
			Long prevsentenceId = -99L, sentenceId;
			StringBuilder buffer = new StringBuilder();
			while (rs2.next()) {
				sentenceId = rs2.getLong("sentenceId");
				word = rs2.getString("word");
				word = word.toLowerCase();
				if (sentenceId != prevsentenceId && prevsentenceId!=-99L){
					writer.write(buffer.toString());
					writer.newLine();
					buffer = new StringBuilder();
				}

				if(!buffer.toString().contains(word)){
					buffer.append(word+" ");
				}
				prevsentenceId = sentenceId;
			}
			writer.write(buffer.toString());
			
			if (writer != null)
				writer.close();

		}

		stmt.close();
		conn.close();
	}

}
