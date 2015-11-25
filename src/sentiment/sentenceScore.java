//step : 9
//generating score of each sentences
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

public class sentenceScore {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		java.sql.PreparedStatement ps = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "select s1.*,s2.p2 from (select s.reviewsentence_id,s.productId,sum(s.ResultantPolarity) p1 from (select  distinct r.opinionword,r.productId,r.reviewsentence_id,(r.ResultantPolarity)  from (select t2.id,t2.feature,t2.opinionWord,t2.productId,t2.reviewsentence_id,t2.posTagId,t2.isNegateNear*t2.Polarity ResultantPolarity from (select t.*,ifnull(p.Polarity,0) Polarity from (select o.id,o.feature,o.opinionWord,o.productId,o.reviewsentence_id,o.posTagId, ifnull(n.isNegateNear,1) isNegateNear from potentialfeature o left join negateproximity n on o.posTagId = n.id) t left join polarity p on p.word = t.opinionWord) t2)r)s group by s.reviewsentence_id) s1 inner join (select s.reviewsentence_id,sum(s.ResultantPolarity) p2 from (select  r.opinionword,r.reviewsentence_id,(r.ResultantPolarity) from (select t2.id,t2.feature,t2.opinionWord,t2.productId,t2.reviewsentence_id,t2.posTagId,t2.isNegateNear*t2.Polarity ResultantPolarity from (select t.*,ifnull(p.Polarity,0) Polarity from (select o.id,o.feature,o.opinionWord,o.productId,o.reviewsentence_id,o.posTagId, ifnull(n.isNegateNear,1) isNegateNear from potentialfeature o left join negateproximity n on o.posTagId = n.id) t left join polarity p on p.word = t.opinionWord) t2)r)s group by s.reviewsentence_id) s2 on s1.reviewsentence_id=s2.reviewsentence_id";
		rs = stmt.executeQuery(sql);
		Long sentenceId;
		Integer p1, p2;
		HashMap<Long, Integer> hm = new HashMap<>();
		while (rs.next()) {
			sentenceId = rs.getLong("reviewsentence_id");
			p1 = rs.getInt("p1");
			p2 = rs.getInt("p2");

			if (p1 != 0) {
				hm.put(sentenceId, p1);
			} else if (p2 != 0) {
				hm.put(sentenceId, p2);
			} else {
				if (hm.containsKey(sentenceId - 1L)) {
					hm.put(sentenceId, hm.get(sentenceId - 1L));
				}
			}
		}

		Set set = hm.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		// Display elements
		while (i.hasNext()) {
			Map.Entry<Long, Integer> me = (Map.Entry) i.next();

			sql = "INSERT INTO sentencescore VALUES(?,?)";
			ps = conn.prepareStatement(sql);

			ps.setLong(1, me.getKey());
			ps.setInt(2, me.getValue());
			ps.executeUpdate();

		}
		hm.clear();
	}
}
