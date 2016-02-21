package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


/**
 * 数据去重.去掉记录相同的记录.
 * 主要策略就是根据学号与时间都相同的情况下进行去重.
 * @author Administrator
 *
 */

public class ConsumeDuplicate {
	
	private String dataFile = null;
	
	private String resultFile = null;
	
	private void setDataFile(String dataFile){
		
		this.dataFile = dataFile;
	}
	
	public void setResultFile(String resultFile){
		
		this.resultFile = resultFile;
	}
	
	public void dataRemoveDuplicate(String dataFile,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		String  beforeFlag = "";
		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String sid = array[0];
			String transTime = array[4];
			String currentFlag = sid + transTime;
			
			if(!beforeFlag.equals(currentFlag)){				
				resultWriter.write(str);
				resultWriter.newLine();				
			}
			
			beforeFlag = currentFlag;
 			
			
		}
		
		dataReader.close();
		resultWriter.close();
		
	}

}
