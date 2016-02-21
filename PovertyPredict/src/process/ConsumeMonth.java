package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;

import method.TimeCall;

public class ConsumeMonth {
	
	
	/**
	 * 提取出学生的消费数据，并且增加月份字段.
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void consumeDataAddMonth(String dataFile,String resultFile) throws Exception{
		
		HashMap<String, String> sidAndYearMap = new HashMap<>();
		sidAndYearMap.put("2007", "200709");
		sidAndYearMap.put("27", "200709");
		sidAndYearMap.put("2008", "200809");
		sidAndYearMap.put("28", "200809");
		sidAndYearMap.put("29", "200909");
		sidAndYearMap.put("2009", "200909");
		sidAndYearMap.put("2010", "201009");
		sidAndYearMap.put("2011", "201109");
		sidAndYearMap.put("2012", "201209");
		sidAndYearMap.put("2013", "201309");
		sidAndYearMap.put("2014", "201409");
		
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));

		
		String str = null;
		
		String title = dataReader.readLine();
		title = "studentID,transName,deviceName,devphyid,transTime,amount,balance,type,month";
		resultWriter.write(title);
		resultWriter.newLine();
		
		int row = 0;
		
		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String studentID = array[1];
			String transName = array[2];
			String deviceName = array[3];
			String devphyid = array[4];
			String transDate = array[5].trim();
			String transTime = array[6].trim();
			String amount = array[7];
			String balance = array[8];
			String type = array[10];
			
			if(transDate.length() != 8 || transTime.length() != 6)
				continue;
			
			String time = transDate + " " + transTime;
			
			if(studentID.length() < 4)
				continue;
			String subStudentID4 = studentID.substring(0,4);
			String subStudentID2 = studentID.substring(0,2);
			String year = null;
			
			if(sidAndYearMap.containsKey(subStudentID2) )				
				year = sidAndYearMap.get(subStudentID2);
			else if(sidAndYearMap.containsKey(subStudentID4))
				year = sidAndYearMap.get(subStudentID4);
			
			if(year != null){
				
				String transMonth = transDate.substring(0,6); 
				
				//如果入学时间晚于消费时间，则将这条记录删除!
				if(year.compareTo(transMonth) > 0){	
					continue; 
				}
				
				int month = TimeCall.distanceMonth(transMonth, year) + 1;//与刚开学时相差的月份.
				
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(studentID);
				stringBuffer.append(",");
				stringBuffer.append(transName);
				stringBuffer.append(",");
				stringBuffer.append(deviceName);
				stringBuffer.append(",");
				stringBuffer.append(devphyid);
				stringBuffer.append(",");
				stringBuffer.append(time);
				stringBuffer.append(",");
				stringBuffer.append(amount);
				stringBuffer.append(",");
				stringBuffer.append(balance);
				stringBuffer.append(",");
				stringBuffer.append(type);
				stringBuffer.append(",");
				stringBuffer.append(month);
				
				String string = stringBuffer.toString();
				resultWriter.write(string);
				resultWriter.newLine();						
				row++;
				if(row % 5000000 == 0)
					System.out.println("current row is: " + row);				
			}			
		}		
		dataReader.close();
		resultWriter.close();
		System.out.println(row);
		
	}

}
