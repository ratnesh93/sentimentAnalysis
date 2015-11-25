//step 5
//extract the opinion, those features which got opinions insert into potential feature table
package sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class opinionExtraction {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null, stmt2 = null, stmt3 = null;
		ResultSet rs = null, rs2 = null, rs3 = null;
		java.sql.PreparedStatement ps = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		stmt3 = (Statement) conn.createStatement();
		String sql = "SELECT distinct prodId FROM electronics_review";
		rs = stmt.executeQuery(sql);

		String productId = "";
		while (rs.next()) {
			productId = rs.getString("prodId");
			String input = "C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/featureSetProductWise/output"
					+ productId + ".txt";
			BufferedReader reader = new BufferedReader(new FileReader(input));
			String line;
			System.out.println("\nproduct = " + productId);
			while (((line = reader.readLine()) != null)) {
				String[] lineSplited = line.split(":");
				System.out.println("feature : " + lineSplited[0]);
				System.out.println("support : " + lineSplited[1]);

				sql = "Select t1.id,t1.sentenceId from ((SELECT t.id,t.word,t.sentenceId,t.posTag,r.reviewId FROM tagwords t inner join reviewsentence r on t.sentenceId = r.id) t1 inner join electronics_review e on e.id = t1.reviewId) where e.prodId="
						+ "'" + productId + "'" + "and t1.word=" + "'" + lineSplited[0] + "'";

				rs2 = stmt2.executeQuery(sql);

				// String word="";
				Long sentenceId;
				Long tagId;
				while (rs2.next()) {
					sentenceId = rs2.getLong("sentenceId");
					tagId = rs2.getLong("id");

					sql = "Select word,id from tagwords where sentenceId=" + "'" + sentenceId + "'" + "and ( posTag="
							+ "'" + "JJ" + "'" + "or posTag=" + "'" + "JJR" + "'" + "or posTag=" + "'" + "JJS" + "'"
							+ ")";
					rs3 = stmt3.executeQuery(sql);

					Long opinionTagId;
					String word = "";
					Long minDistanceId = Long.MAX_VALUE;
					String nearestOpinion = "";
					while (rs3.next()) {
						word = rs3.getString("word");
						opinionTagId = rs3.getLong("id");

						if (Math.abs(opinionTagId - tagId) <= 5l) {
							if (opinionTagId < minDistanceId) {
								nearestOpinion = word;
								minDistanceId = opinionTagId;
							}

						}
					}
					if (minDistanceId != Long.MAX_VALUE) {

						sql = "INSERT INTO potentialfeature VALUES(NULL,?,?,?,?,?)";
						ps = conn.prepareStatement(sql);
						ps.setString(1, lineSplited[0]);
						ps.setString(2, nearestOpinion);
						ps.setString(3, productId);
						ps.setLong(4, sentenceId);
						ps.setLong(5, minDistanceId);
						ps.executeUpdate();

					}
				}
			}

		}
		stmt.close();
		conn.close();
	}

}
