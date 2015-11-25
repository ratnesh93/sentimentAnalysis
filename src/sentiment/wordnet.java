//step : 7
//finding polarity of remaining opinion with the help of wordnet
//you have to install Wordnet software from their official site and give the installation directory while initializing
//this program is an infinite loop stop after few minutes when your database is not changing(i am too lazy to handle infinite loop,sorry)
package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import rita.RiWordNet;

public class wordnet {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/sentiment";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "ratneshchandak";

	public static void addToDB(String opinion, Integer polarity) throws SQLException {
		java.sql.PreparedStatement ps = null;
		String sql = "INSERT INTO polarity VALUES(?,?)";
		Connection conn = null;
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		ps = conn.prepareStatement(sql);
		ps.setString(1, opinion);
		ps.setInt(2, polarity);
		ps.executeUpdate();
		conn.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		RiWordNet wordnet = new RiWordNet("C:/Program Files (x86)/WordNet/2.1");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();

		String sql = "select word,Polarity from polarity";
		rs = stmt.executeQuery(sql);

		HashMap<String, Integer> seedlist = new HashMap<String, Integer>();
		String word = "";
		Integer polarity;
		while (rs.next()) {
			word = rs.getString("word");
			polarity = rs.getInt("Polarity");
			seedlist.put(word, polarity);
		}
		System.out.println(seedlist.size());
		int lActiveList = 0;

		sql = "SELECT distinct opinionWord FROM sentiment.opinion";
		rs = stmt.executeQuery(sql);
		word = "";
		ArrayList<String> activelist = new ArrayList<String>();

		while (rs.next()) {
			word = rs.getString("opinionWord");
			activelist.add(word);
			lActiveList++;
		}
		boolean[] visited = new boolean[2000];
		int rem = 0;
		int prev = lActiveList;

		for (int i = 0; i < lActiveList; i++) {
			visited[i] = false;
		}
		int prevSeedListLength = seedlist.size();
		int activeSeedListLength;
		while (true) {

			rem = 0;
			for (int i = 0; i < lActiveList; i++) {

				if (!visited[i]) {
					if (seedlist.containsKey(activelist.get(i))) {
						visited[i] = true;
						prev--;
					}

					else {
						String[] antonyms = wordnet.getAllAntonyms(activelist.get(i), "a");
						String[] synonyms = wordnet.getAllSynonyms(activelist.get(i), "a");

						int lAntonyms = antonyms.length;
						int lSynonyms = synonyms.length;
						boolean gotfromSysnonym = false;
						for (int j = 0; j < lSynonyms; j++) {
							if (seedlist.containsKey(synonyms[j])) {
								polarity = seedlist.get(synonyms[j]);
								seedlist.put(activelist.get(i), polarity);

								sql = "select word from polarity where word=" + "'" + activelist.get(i) + "'";
								rs = stmt.executeQuery(sql);
								if (!rs.next()) {
									addToDB(activelist.get(i), polarity);
								}
								String[] antonymsActive = wordnet.getAllAntonyms(activelist.get(i), "a");
								String[] synonymsActive = wordnet.getAllSynonyms(activelist.get(i), "a");
								// inserting antonyms with opposite polarity
								for (int k = 0; k < antonymsActive.length; k++) {
									sql = "select word from polarity where word=" + "'" + antonymsActive[k] + "'";
									rs = stmt.executeQuery(sql);
									if (!seedlist.containsKey(antonymsActive[k])) {
										seedlist.put(antonymsActive[k], -polarity);
									}
									if (!rs.next()) {
										addToDB(antonymsActive[k], -polarity);
									}
								}
								// inserting synonyms with same polarity
								for (int k = 0; k < synonymsActive.length; k++) {
									if (!seedlist.containsKey(synonymsActive[k])) {
										seedlist.put(synonymsActive[k], polarity);
									}

									sql = "select word from polarity where word=" + "'" + synonymsActive[k] + "'";
									rs = stmt.executeQuery(sql);
									if (!rs.next()) {
										addToDB(synonymsActive[k], polarity);
									}
								}
								visited[i] = true;
								gotfromSysnonym = true;
								prev--;
								break;
							}
						}
						if (!gotfromSysnonym) {
							for (int j = 0; j < lAntonyms; j++) {
								if (seedlist.containsKey(antonyms[j])) {
									polarity = seedlist.get(antonyms[j]);
									seedlist.put(activelist.get(i), -polarity);

									sql = "select word from polarity where word=" + "'" + activelist.get(i) + "'";
									rs = stmt.executeQuery(sql);
									if (!rs.next()) {
										addToDB(activelist.get(i), -polarity);
									}

									String[] antonymsActive = wordnet.getAllAntonyms(activelist.get(i), "a");
									String[] synonymsActive = wordnet.getAllSynonyms(activelist.get(i), "a");
									// inserting antonyms with opposite polarity
									for (int k = 0; k < antonymsActive.length; k++) {
										if (!seedlist.containsKey(antonymsActive[k])) {
											seedlist.put(antonymsActive[k], polarity);
										}

										sql = "select word from polarity where word=" + "'" + antonymsActive[k] + "'";
										rs = stmt.executeQuery(sql);
										if (!rs.next()) {
											addToDB(antonymsActive[k], polarity);
										}
									}
									// inserting synonyms with same polarity
									for (int k = 0; k < synonymsActive.length; k++) {

										if (!seedlist.containsKey(synonymsActive[k])) {
											seedlist.put(synonymsActive[k], -polarity);
										}

										sql = "select word from polarity where word=" + "'" + synonymsActive[k] + "'";
										rs = stmt.executeQuery(sql);
										if (!rs.next()) {
											addToDB(synonymsActive[k], -polarity);
										}
									}

									visited[i] = true;
									prev--;
									break;
								}
							}
						}
					}
				}
				if (!visited[i]) {
					rem++;
				}
			}
			activeSeedListLength = seedlist.size();
			if (activeSeedListLength == prevSeedListLength) {
				System.out.println("rem = " + rem);
				System.out.println("prev = " + prev);
				break;
			}
		}
	}

}
