//step 1
//breaking review into sentences because post tagger takes input sentence wise
package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.Locale;

public class breakReviewIntoSentence {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "SELECT id,reviewText from electronics_review";
		rs = stmt.executeQuery(sql);

		Long reviewId;
		String reviewText;
		while (rs.next()) {
			reviewId = rs.getLong("id");
			reviewText = rs.getString("reviewText");
			BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
			iterator.setText(reviewText);
			int start = iterator.first();
			for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
				sql = "INSERT INTO reviewsentence VALUES(NULL,?,?)";
				ps = conn.prepareStatement(sql);
				System.out.println("review id =" + reviewId);
				System.out.println("senetnce = " + reviewText.substring(start, end));

				ps.setLong(1, reviewId);
				ps.setString(2, reviewText.substring(start, end));
				ps.executeUpdate();

			}
		}
		stmt.close();
		conn.close();
	}
}
