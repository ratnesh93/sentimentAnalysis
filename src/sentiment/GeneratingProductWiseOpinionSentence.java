//step : 11(final step)
//generating aggregate reviews in the text file under package ProductWiseOpinionSenntences
package sentiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GeneratingProductWiseOpinionSentence {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null, stmt2 = null, stmt3 = null, stmt4 = null;
		ResultSet rs = null, rs2 = null, rs3 = null, rs4 = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		stmt3 = (Statement) conn.createStatement();
		stmt4 = (Statement) conn.createStatement();

		String sql = "SELECT distinct prodId FROM electronics_review";
		rs = stmt.executeQuery(sql);

		String productId = "";
		while (rs.next()) {

			BufferedWriter writer = null;
			productId = rs.getString("prodId");

			writer = new BufferedWriter(new FileWriter(
					"C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/ProductWiseOpinionSentences/" + productId
							+ ".txt"));

			sql = "select distinct feature from findfeaturesentences where productId=" + '"' + productId + '"';
			rs2 = stmt2.executeQuery(sql);
			String feature = "";
			while (rs2.next()) {
				feature = rs2.getString("feature");

				Integer positiveScore, negativeScore;
				sql = "select positiveScore,negativeScore from featurewisescore where productId=" + '"' + productId
						+ '"' + "and feature=" + '"' + feature + '"';
				rs4 = stmt4.executeQuery(sql);

				if (rs4.next()) {
					positiveScore = rs4.getInt("positiveScore");
					negativeScore = rs4.getInt("negativeScore");

					sql = "SELECT sentence,orientation FROM findfeaturesentences inner join reviewsentence r on sentenceId=r.id where feature="
							+ '"' + feature + '"' + " and productId = +" + '"' + productId + '"'
							+ "order by orientation desc";
					rs3 = stmt3.executeQuery(sql);

					writer.newLine();
					writer.write("------------------------------------------------------------------");
					writer.newLine();
					writer.write("feature : " + feature + ", positive Score : " + positiveScore
							+ " and negative Score :" + negativeScore);
					writer.newLine();
					writer.write("------------------------------------------------------------------");
					writer.newLine();
					int p = 0, n = 0, i = 1;
					while (rs3.next()) {
						if (rs3.getInt("orientation") == 1 && p == 0) {
							writer.write("positive sentences : ");
							writer.newLine();
							p++;
						}
						if (rs3.getInt("orientation") == -1 && n == 0) {
							writer.newLine();
							writer.write("negative sentences : ");
							writer.newLine();
							n++;
							i = 1;
						}
						writer.write("" + i + ". " + rs3.getString("sentence"));
						writer.newLine();
						i++;
					}

				}
			}
			if (writer != null)
				writer.close();
		}

	}
}
