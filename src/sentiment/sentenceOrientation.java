package sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

public class sentenceOrientation {

	// JDBC driver name and database URL
		static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		static final String DB_URL = "jdbc:mysql://localhost/sentiment";

		// Database credentials
		static final String USER = "root";
		static final String PASS = "ratneshchandak";
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		Statement stmt = null, stmt2 = null, stmt3 = null;
		ResultSet rs = null, rs2 = null, rs3 = null;
		java.sql.PreparedStatement ps = null;
		
		HashMap<String, Boolean> negatewords = new HashMap<>(); //just because search is o(1)
		
		List<Long> list = new ArrayList<>();
		
		
		Class.forName("com.mysql.jdbc.Driver");
		conn = (Connection) DriverManager.getConnection(DB_URL, USER, PASS);
		stmt = (Statement) conn.createStatement();
		stmt2 = (Statement) conn.createStatement();
		stmt3 = (Statement) conn.createStatement();
		String sql="";
		String sql2="";
		sql = "select * from negate_words";
		
//		sql = "SELECT t.id,word,posTag,sentenceId FROM sentiment.tagwords t "
//				+ "INNER JOIN (select distinct reviewsentence_id from opinion) "
//				+ "o on t.sentenceId=o.reviewsentence_id";
		
		
//		 sql="select t2.id,t2.word,t2.sentenceId from "
//				+ "(SELECT t.id,word,posTag,sentenceId FROM sentiment.tagwords t "
//				+ "INNER JOIN (select distinct reviewsentence_id from opinion) o on t.sentenceId=o.reviewsentence_id) t2 "
//				+ "INNEr join negate_words n where t2.word = n.word ";
		 rs = stmt.executeQuery(sql);
		 int count=0;
		while(rs.next()){
			
			//Long id = rs.getLong("id");
			String word = rs.getString("word");
			count++;
			negatewords.put(word, true);
		}
		System.out.println(count);
		
		
		sql = "SELECT distinct reviewsentence_id from opinion";
		
		
		rs = stmt.executeQuery(sql);
		
		Long sentenceId;
		Long id;
		String word;
		String posTag;
		Long curr_neg;
		
		while(rs.next()){
			sentenceId = rs.getLong("reviewsentence_id");
			//System.out.println(sentenceId);
			sql2 = "Select id,word,posTag,sentenceId from tagwords where sentenceId="+sentenceId;
			rs2 = stmt2.executeQuery(sql2);
			
			curr_neg=-5L;
			List<Long> sentence_adj = new ArrayList<>();

			while(rs2.next()){
				id = rs2.getLong("id");
				word = rs2.getString("word");
				posTag = rs2.getString("posTag");
				if(posTag.equals("JJ") || posTag.equals("JJR")||posTag.equals("JJS")){
					
					if(Math.abs(id-curr_neg)<=5l){
						
						list.add(id);
					}
					else{
						sentence_adj.add(id);
					}
					
				}
				else if(negatewords.containsKey(word.toLowerCase())){
					//System.out.println(word+" "+id);
					curr_neg=id;
					for(int i=0;i<sentence_adj.size();i++){
						if(curr_neg-sentence_adj.get(i)<=5){
							list.add(sentence_adj.get(i));
							sentence_adj.remove(i);
						}
							
					}
				}
	
			}
			
		}
		
		
		///
		
		System.out.println(list.size());
		
		for(int i=0;i<list.size();i++){
			sql = "INSERT INTO negateproximity VALUES(?,-1)";
			ps = conn.prepareStatement(sql);
			ps.setLong(1, list.get(i));
			ps.executeUpdate();
		}
		
		

	}

}

