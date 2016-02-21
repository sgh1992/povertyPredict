package dataBase;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.text.StyleContext.SmallAttributeSet;

public class DB1{
  	private String userName=null;
		private String userPassword=null;
		private String url=null;
		private Connection connect=null;
		private ResultSet rs=null;
		private Statement stmt = null;
		private String driverName = null;//这个实际上就是一个驱动类
		public DB1(){
			try{
				
			 /**
			  * 从配置文件中读取对应的连接数据库的JDBC设置!
			  */
			  Properties pr = new Properties();
			  FileInputStream in = new FileInputStream("database1.properties");
			  pr.load(in);
			  driverName = pr.getProperty("driverName");
			  userName = pr.getProperty("userName");
			  url = pr.getProperty("url");
			  userPassword = pr.getProperty("userPassWord");
			  
			  Class.forName(driverName);
			  connect=DriverManager.getConnection(url,userName,userPassword);
			  stmt=connect.createStatement();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		//更新数据库
		public int executeUpdate(String sql){
			int result=0;
			try{
				result=stmt.executeUpdate(sql);
			}
			catch(SQLException ex){
				System.err.println(ex.getMessage());
			}
			return result;
		}
		
		//查询数据库
		public  ResultSet executeQuery(String sql){
			try{
				rs=stmt.executeQuery(sql);
			}
			catch(SQLException ex){
				System.err.println(ex.getMessage());
			}
			return rs;
		}
  	  
  	   //文本插入
  	   public void insertText(String sql){
  			try {	
  				stmt.executeQuery(sql);
  			} catch (SQLException e) {
  				e.printStackTrace();
  			}
  		}
  	     
  	     
  	  public void close(){
  		  if(rs != null){
  			  try {
				rs.close();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
  		  }
  		  if(stmt != null){
  			  try {
				stmt.close();
				stmt = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
  		  }
  		  if(connect != null){
  			  try {
  				  connect.close();
  				  connect = null;
  			  }
  			  catch(SQLException e) {
  				  System.err.println(e.getMessage());
  			  }
  		  }
  	  }
  	  
  	 public void connect()
  	 {
   		try{
   			Class.forName("com.mysql.jdbc.Driver");
 			connect=DriverManager.getConnection(url);
 			stmt=connect.createStatement();
 		}
 		catch(Exception e){
 			e.printStackTrace();
 		}
   	  }
   	  
   	  public Connection getConnection(){
   		  return connect;
   	  }

  	
	}