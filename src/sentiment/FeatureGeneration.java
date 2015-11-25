//step 4
//doing association mining using FPgrowth algorithm, results are saved in output file under package featureSetProductWise
package sentiment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth_with_strings.AlgoFPGrowth_Strings;
import ca.pfv.spmf.test.MainTestFPGrowth_strings_saveToFile;

//feature generation using association mining using FPGrowth algorithm for strings
public class FeatureGeneration {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "SELECT distinct prodId FROM electronics_review";
		rs = stmt.executeQuery(sql);

		String productId = "";
		while (rs.next()) {
			productId = rs.getString("prodId");
			System.out.println("product id = " + productId);

			// String input =
			// fileToPath("C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/featureSetProductWise/input"
			// + productId + ".txt"); // the database
			String output = "C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/featureSetProductWise/output"
					+ productId + ".txt"; // the path for saving the frequent
											// itemsets found
			String input = "C:/Users/ratnesh/Documents/GitHub/sentimentAnalysis/src/featureSetProductWise/input"
					+ productId + ".txt";

			double minsup = 0.01; // MINIMUM SUPPORT which the paper has used
									// i.e. 1%

			// Applying the FPGROWTH algorithmMainTestFPGrowth.java
			AlgoFPGrowth_Strings algo = new AlgoFPGrowth_Strings();
			algo.runAlgorithm(input, output, minsup);
			algo.printStats();

		}
		stmt.close();
		conn.close();
	}

	public static String fileToPath(String filename) throws UnsupportedEncodingException {
		URL url = MainTestFPGrowth_strings_saveToFile.class.getResource(filename);
		return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
	}
}
