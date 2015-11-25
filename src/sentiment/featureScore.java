//step : 10
//generating feature wise score
package sentiment;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class featureScore {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		// TODO Auto-generated method stub

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

			sql = "select F.feature, S.sentenceId, S.score from potentialfeature F,sentencescore S where F.reviewsentence_id = S.sentenceId and F.productId="
					+ '"' + productId + '"';
			rs2 = stmt2.executeQuery(sql);

			HashMap<String, Integer> hm = new HashMap<>();
			Long sentenceId;
			String feature = "";
			Integer positivescore;

			while (rs2.next()) {
				feature = rs2.getString("feature");
				positivescore = rs2.getInt("score");
				sentenceId = rs2.getLong("sentenceId");
				if (positivescore > 0) {
					if (hm.containsKey(feature)) {
						hm.put(feature, hm.get(feature) + positivescore);

					} else {
						hm.put(feature, positivescore);

					}
					sql = "INSERT INTO findfeaturesentences VALUES(Null,?,?,?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, productId);
					ps.setString(2, feature);
					ps.setLong(3, sentenceId);
					ps.setInt(4, 1);
					ps.executeUpdate();
				}
			}
			Set set = hm.entrySet();
			// Get an iterator
			Iterator i = set.iterator();
			// Display elements
			while (i.hasNext()) {
				Map.Entry<String, Integer> me = (Map.Entry) i.next();

				sql = "INSERT INTO featurewisescore VALUES(?,?,?,?)";
				ps = conn.prepareStatement(sql);
				ps.setString(1, productId);
				ps.setString(2, me.getKey());
				ps.setInt(3, me.getValue());
				ps.setInt(4, 0);
				ps.executeUpdate();

			}
			hm.clear();
			sql = "select F.feature, S.score,S.sentenceId from potentialfeature F,sentencescore S where F.reviewsentence_id = S.sentenceId and F.productId="
					+ '"' + productId + '"';
			rs2 = stmt2.executeQuery(sql);
			Integer negativescore;
			while (rs2.next()) {
				feature = rs2.getString("feature");
				negativescore = rs2.getInt("score");
				sentenceId = rs2.getLong("sentenceId");
				if (negativescore < 0) {
					if (hm.containsKey(feature)) {
						hm.put(feature, hm.get(feature) + negativescore);

					} else {
						hm.put(feature, negativescore);
					}

					sql = "INSERT INTO findfeaturesentences VALUES(Null,?,?,?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, productId);
					ps.setString(2, feature);
					ps.setLong(3, sentenceId);
					ps.setInt(4, -1);
					ps.executeUpdate();
				}
			}

			set = hm.entrySet();
			// Get an iterator
			i = set.iterator();
			// Display elements
			while (i.hasNext()) {
				Map.Entry<String, Integer> me = (Map.Entry) i.next();

				sql = "Select * from featurewisescore WHERE feature = " + '"' + me.getKey() + '"' + " and productId = "
						+ '"' + productId + '"';
				rs3 = stmt3.executeQuery(sql);
				if (rs3.next()) {
					String updateTableSQL = "UPDATE featurewisescore SET negativeScore = ? WHERE feature = " + '"'
							+ me.getKey() + '"' + "and productId = " + '"' + productId + '"';
					ps = conn.prepareStatement(updateTableSQL);
					ps.setInt(1, me.getValue());
					ps.executeUpdate();
				} else {
					sql = "INSERT INTO featurewisescore VALUES(?,?,?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, productId);
					ps.setString(2, me.getKey());
					ps.setInt(3, 0);
					ps.setInt(4, me.getValue());
					ps.executeUpdate();
				}
			}
			hm.clear();
		}
	}
}
