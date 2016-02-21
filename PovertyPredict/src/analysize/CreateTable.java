package analysize;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import dataBase.DB;
import dataBase.FileLoad;

public class CreateTable {
	
	
	
	private String tableName = null;
	
	private String dataFile = null;
	
	public void setTableName(String tableName){
		
		this.tableName = tableName;
		
	}
	
	public void setDataFile(String dataFile){
		
		this.dataFile = dataFile;
	}
	
	/**
	 * 创建带有类别信息的数据表，并且将数据导入数据库中.
	 * @param dataFile
	 * @param tableName
	 * @throws Exception
	 */
	public void createTableWithClassAndLoadData(String dataFile,String tableName) throws Exception{
		
		createTableWithClassType(dataFile, tableName);
		
		FileLoad fileLoad = new FileLoad();
		fileLoad.LoadTemplateFull(dataFile, tableName, ",");
		
	}
	
	/**
	 * 创建不带类别信息的数据表,并且将数据导入数据库中.
	 * @param dataFile
	 * @param tableName
	 * @throws Exception
	 */
	public void createTableAndLoadData(String dataFile,String tableName) throws Exception{
		
		createTableWithClassType(dataFile, tableName);
		
		FileLoad fileLoad = new FileLoad();
		fileLoad.LoadTemplateFull(dataFile, tableName, ",");
		
	}
	
	/**
	 * 不带类别的统计数据导入.
	 * @param dataFile
	 * @param tableName
	 * @throws Exception
	 */
	public void createTable(String dataFile,String tableName) throws Exception{
		
		
		if(this.dataFile != null)
			dataFile = this.dataFile;
		
		if(this.tableName != null)
			tableName = this.tableName;
		
		
		DB db = new DB();
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String title = dataReader.readLine();
		
		String[] array = title.split(",", -1);
		
		StringBuffer createTableBuffer = new StringBuffer();
		createTableBuffer.append("create table ");
		createTableBuffer.append(tableName);
		createTableBuffer.append("( ");
		for(int i = 0; i < array.length; i++){
			
			String variable = array[i];
			String type = "double";
			if(i == 0)
				type = "varchar(50)";
			
			createTableBuffer.append(variable + " ");
			createTableBuffer.append(type + " ");
			createTableBuffer.append(",");
			
		}
		
		String createTableSql = createTableBuffer.toString();
		createTableSql = createTableSql.substring(0,createTableSql.length() - 1);
		createTableSql = createTableSql + " )";
		
		db.executeUpdate(createTableSql);
		
		dataReader.close();
		db.close();
		
	}
	
	public void createTableWithClassType(String dataFile,String tableName) throws Exception{
		
		DB db = new DB();
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String title = dataReader.readLine();
		
		String[] array = title.split(",", -1);
		
		StringBuffer createTableBuffer = new StringBuffer();
		createTableBuffer.append("create table ");
		createTableBuffer.append(tableName);
		createTableBuffer.append("( ");
		for(int i = 0; i < array.length; i++){
			
			String variable = array[i];
			String type = "double";
			if(i == 0 || i == array.length - 1 || variable.contains("class")) //可能包括预测的类别!!!
				type = "varchar(50)";
			
			createTableBuffer.append(variable + " ");
			createTableBuffer.append(type + " ");
			createTableBuffer.append(",");
			
		}
		
		String createTableSql = createTableBuffer.toString();
		createTableSql = createTableSql.substring(0,createTableSql.length() - 1);
		createTableSql = createTableSql + " )";
		
		db.executeUpdate(createTableSql);
		
		dataReader.close();
		db.close();
		
		
	}
	
	

}
