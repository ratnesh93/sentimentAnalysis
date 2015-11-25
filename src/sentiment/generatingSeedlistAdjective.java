//step 6: 
//generating seedlist of adjectives which we can assign polarity
//in our seedlist we took 30 adjectives for which we are sure, this 30 are top 30 frequency wise in our data
package sentiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import rita.RiWordNet;

public class generatingSeedlistAdjective {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

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

		String sql = "SELECT Count(*) as count,opinionWord FROM potentialfeature group by opinionWord order by  count desc";
		rs = stmt.executeQuery(sql);

		int count = 0;
		String word = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (rs.next()) {
			word = rs.getString("opinionWord");

			sql = "select word from polarity where word=" + "'" + word + "'";

			rs2 = stmt2.executeQuery(sql);
			if (!rs2.next()) {
				System.out.println("word  = " + word);

				System.out.println("press 1 for entering polarity and 0 for not");
				int input = Integer.parseInt(br.readLine());
				if (input == 1) {
					System.out.println("polarity +1 = +ve, -1=-ve & 0=neutral enter polarity of the word, : " + word);
					int polarity = Integer.parseInt(br.readLine());
					sql = "INSERT INTO polarity VALUES(?,?)";
					ps = conn.prepareStatement(sql);
					ps.setString(1, word);
					ps.setInt(2, polarity);
					ps.executeUpdate();

					RiWordNet wordnet = new RiWordNet("C:/Program Files (x86)/WordNet/2.1");
					String[] antonyms = wordnet.getAllAntonyms(word, "a");
					String[] synonyms = wordnet.getAllSynonyms(word, "a");
					// inserting antonyms with opposite polarity
					for (int i = 0; i < antonyms.length; i++) {
						sql = "select word from polarity where word=" + "'" + antonyms[i] + "'";
						rs3 = stmt3.executeQuery(sql);
						if (!rs3.next()) {
							sql = "INSERT INTO polarity VALUES(?,?)";
							ps = conn.prepareStatement(sql);
							ps.setString(1, antonyms[i]);
							ps.setInt(2, -polarity);
							ps.executeUpdate();
						}
					}
					// inserting synonyms with same polarity
					for (int i = 0; i < synonyms.length; i++) {
						sql = "select word from polarity where word=" + "'" + synonyms[i] + "'";
						rs3 = stmt3.executeQuery(sql);
						if (!rs3.next()) {
							sql = "INSERT INTO polarity VALUES(?,?)";
							ps = conn.prepareStatement(sql);
							ps.setString(1, synonyms[i]);
							ps.setInt(2, polarity);
							ps.executeUpdate();
						}
					}
					count++;

				}
			}
			System.out.println("count= " + count);
			if (count == 30)
				break;
		}

	}

}
