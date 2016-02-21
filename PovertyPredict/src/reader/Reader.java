package reader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.ResultSet;

import dataBase.DB;

public class Reader {
	
	private String consumeLocal = null;
	private String tableName = null;

	
	public String getTableName(){
		
		return tableName;
	}
	
	public void setTableName(String tableName){
		
		this.tableName = tableName;
		
	}
	
	public String getConsumeLocal(){
		
		return consumeLocal;
		
	}
	
	public void setConsumeLocal(String consumeLocal){
		
		
		this.consumeLocal = consumeLocal;
		
	}
	
	
	
	public void dataReader(){
		
		readMethod(); 
		
		
	}
	
	public void readMethod(){
		
		BufferedWriter	resultWriter = null;
		DB db = new DB();
		ResultSet rSet = null;
		int row = 0;
		try {
						
			resultWriter = new BufferedWriter(new FileWriter(consumeLocal));			
			//读取数据列名，这里的数据列名必须在事先是固定好的.
			String title = "studentID,transName,deviceName,devphyid,transTime,amount,balance";
			resultWriter.write(title);
			resultWriter.newLine();
			
			int index = 0;
			int rowLimit = 3000000; //每页查询3000000行.			
			boolean flag = true;			
			while(flag){
				
				flag = false;
				String sqlString = "select * from " + tableName + " limit " + index + "," + rowLimit;
				rSet = db.executeQuery(sqlString);
				index += rowLimit;
				
				while(rSet.next()){						
					flag = true;
					String studentID = rSet.getString("studentID");
					String transName = rSet.getString("transName");
					String deviceName = rSet.getString("devphyid");
					String devphyid = rSet.getString("devphyid");
					String transDate = rSet.getString("transDate");
					String transTime = rSet.getString("transTime");
					
					//数据的异常处理，如果transDate与transTime的数据字段不符合要求，则将这条记录丢弃.
					transDate = transDate.trim();
					transTime = transTime.trim();				
					if(transDate.length() != 8 | transTime.length() != 6)
						continue;
					
					double amount = rSet.getDouble("amount");
					double balance = rSet.getDouble("balance");
					String time = transDate + " " + transTime;
					
					String string = studentID + "," + transName + "," + deviceName + "," + devphyid + "," + time + "," + amount + "," + balance;
					resultWriter.write(string);
					resultWriter.newLine();	
					row++;
				}
				
				if(row % 6000000 == 0)
					System.out.println("current row is: " + row);
			}
			
			rSet.close();
			db.close();
			resultWriter.close();
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		
	}
	

}
