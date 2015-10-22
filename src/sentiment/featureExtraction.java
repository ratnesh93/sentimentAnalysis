package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class featureExtraction {

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
		Statement stmt2 = null;
		ResultSet rs2 = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();

		String sql = "SELECT distinct prodId FROM electronics_review";
		rs2 = stmt2.executeQuery(sql);

		String productId = "";
		while (rs2.next()) {
			HashMap<String, Integer> hm = new HashMap();
			productId = rs2.getString("prodId");

			sql = "Select w.sentenceId,w.word,w.id from electronics_review e inner join (SELECT r.reviewId,t.sentenceId,t.word,t.id FROM tagwords t INNER JOIN reviewsentence r on t.sentenceId = r.id and (t.posTag='NN' OR t.posTag='NNS')) w on w.reviewId = e.id and e.prodId="
					+ "'" + productId + "'";

			rs = stmt.executeQuery(sql);
			Long sentenceId, prevsentenceId = -99L, lastsecondsentenceid = -99L;
			String word, prevword = "", lastsecondprev = "";
			Long tagId, prevtagId = -99L, lastsecondtagid = -99L;
			while (rs.next()) {

				sentenceId = rs.getLong("sentenceId");
				word = rs.getString("word").toLowerCase();
				tagId = rs.getLong("id");

				if (prevtagId == (tagId - 1L) && sentenceId == prevsentenceId) {

					if (lastsecondtagid == (tagId - 2L) && sentenceId == prevsentenceId) {
						if (hm.containsKey(lastsecondprev + " " + prevword)) {
							if (hm.get(lastsecondprev + " " + prevword) == 1) {
								hm.remove(lastsecondprev + " " + prevword);
								if (!hm.containsKey(lastsecondprev + " " + prevword + " " + word)) {
									hm.put(lastsecondprev + " " + prevword + " " + word, 1);
									System.out.println(
											lastsecondprev + " " + prevword + " " + word + " triple first time");
								} else {
									hm.put(word, hm.get(lastsecondprev + " " + prevword + " " + word) + 1);
									System.out.println(
											lastsecondprev + " " + prevword + " " + word + " triple second time");
								}
							} else {
								hm.put(lastsecondprev + " " + prevword, hm.get(lastsecondprev + " " + prevword) - 1);
								if (!hm.containsKey(lastsecondprev + " " + prevword + " " + word)) {
									hm.put(lastsecondprev + " " + prevword + " " + word, 1);
									System.out.println(prevword + " " + word + " triple first time");
								} else {
									hm.put(lastsecondprev + " " + prevword + " " + word,
											hm.get(lastsecondprev + " " + prevword + " " + word) + 1);
									System.out.println(
											lastsecondprev + " " + prevword + " " + word + " triple second time");
								}
							}
						} else {
							if (!hm.containsKey(lastsecondprev + " " + prevword + " " + word)) {
								hm.put(lastsecondprev + " " + prevword + " " + word, 1);
								System.out.println(prevword + " " + word + " triple first time");
							} else {
								hm.put(lastsecondprev + " " + prevword + " " + word,
										hm.get(lastsecondprev + " " + prevword + " " + word) + 1);
								System.out
										.println(lastsecondprev + " " + prevword + " " + word + " triple second time");
							}
						}

					} else {

						if (hm.get(prevword) == 1) {
							hm.remove(prevword);
							if (!hm.containsKey(prevword + " " + word)) {
								hm.put(prevword + " " + word, 1);
								System.out.println(prevword + " " + word + " double first time");
							} else {
								hm.put(word, hm.get(prevword + " " + word) + 1);
								System.out.println(prevword + " " + word + " double second time");
							}
						} else {
							hm.put(prevword, hm.get(prevword) - 1);
							if (!hm.containsKey(prevword + " " + word)) {
								hm.put(prevword + " " + word, 1);
								System.out.println(prevword + " " + word + " double first time");
							} else {
								hm.put(prevword + " " + word, hm.get(prevword + " " + word) + 1);
								System.out.println(prevword + " " + word + " double second time");
							}
						}

					}

				} else {
					if (!hm.containsKey(word)) {
						hm.put(word, 1);
						System.out.println(word + " single");
					} else
						hm.put(word, hm.get(word) + 1);
				}
				lastsecondprev = prevword;
				lastsecondtagid = prevtagId;
				lastsecondsentenceid = prevsentenceId;
				prevword = word;
				prevtagId = tagId;
				prevsentenceId = sentenceId;
			}
			Set set = hm.entrySet();
			// Get an iterator
			Iterator i = set.iterator();
			// Display elements
			java.sql.PreparedStatement ps = null;
			while (i.hasNext()) {
				Map.Entry<String, Integer> me = (Map.Entry) i.next();
				sql = "INSERT INTO feature_frequency VALUES(NULL,?,?,?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, productId);
				ps.setString(2, me.getKey());
				ps.setInt(3, me.getValue());
				ps.executeUpdate();

			}

			hm.clear();
			
		}
		stmt.close();
		stmt2.close();
		conn.close();
	}

}
