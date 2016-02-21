package dataBase;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;




public class FileLoad {
	
	
	private String tableName = null;
	
	private String dataFile = null;
	
	private String splitLetter = null;
	
	public void setDataFile(String dataFile){
		
		this.dataFile = dataFile;
	}
	
	public void setTableName(String tableName){
		
		this.tableName = tableName;
	}
	
	public void setSplitLetter(String splitLetter){
		
		this.splitLetter = splitLetter;
	}
	
	/**
	 * 直接插入数据.
	 * @param dataFile
	 * @param tableName
	 * @param splitLetter
	 * @throws Exception
	 */
	public void LoadTemplateFull(String dataFile,String tableName,String splitLetter) throws Exception{
		String sql = null;
		DB db = new DB();
		sql = "LOAD DATA LOCAL INFILE '" + dataFile + "' INTO TABLE `"+tableName+"` FIELDS TERMINATED BY '"+ splitLetter + "' LINES TERMINATED BY '\r\n' IGNORE 1 LINES";
		db.insertText(sql);
		db.close();
	}
	
	
	/**
	 * 先删除原有数据表中的数据，然后再插入，本质上是一个覆盖操作.
	 * @param dataFile
	 * @param tableName
	 * @param splitLetter
	 * @throws Exception
	 */
	public void createAndLoad(String dataFile,String tableName,String tableContent,String splitLetter) throws Exception{
		
		//先判断这个TableName是否存在，如果存在的话，则删除这个表，然后再新建一个表，如果不存在的话，就直接新建一个表.
		createTable(tableName,tableContent);		
		Remote_DB db = new Remote_DB();
		//再插入.
		String insertSql = "LOAD DATA LOCAL INFILE '" + dataFile +  "' INTO TABLE " + tableName + " FIELDS TERMINATED BY '" + splitLetter + "' LINES TERMINATED BY '\r\n' IGNORE 1 LINES";
		db.insertText(insertSql);		
		db.close();
		
	}
	
	
	/**
	 * 判断某表是否存在.
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public boolean judgeTabelExist(String tableName) throws Exception{
		
		Remote_DB db = new Remote_DB();
		
		DatabaseMetaData metaData = db.getConnection().getMetaData();
		
		String[] types = {"TABLE"};
		ResultSet rSet = metaData.getTables(null, null, tableName, types);
		if(rSet.next()){
			
			rSet.close();
			db.close();
			return true;
			
		}
		else {
			rSet.close();
			db.close();
			return false;
		}		
	}
	
	/**
	 * 判断是否存在表，如果存在，则删除现有现有表，如果不存在，则删除表.
	 * @param tableName 表名.
	 * @param tableContent 表中的内容.
	 * @throws Exception
	 */
	public void createTable(String tableName,String tableContent) throws Exception{
		
		Remote_DB db = new Remote_DB();
		
		boolean isHave = judgeTabelExist(tableName);

				
		if(isHave){			
			String dropSql = "drop table " + tableName;
			db.executeUpdate(dropSql);
			db.close();
			db = new Remote_DB();
			
		}
					
		String createSql = tableContent;
		db.executeUpdate(createSql);
					
		db.close();
		
	}
	
	

}
