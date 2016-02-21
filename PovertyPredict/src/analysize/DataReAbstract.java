package analysize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import dataBase.DB;

/**
 * 数据类别的重新划分.
 * 划分的标准：
 * 1.以每个学生在校的平均消费金额取各自群体的80%的数据.
 * @author Administrator
 *
 */
public class DataReAbstract {
	
	/**
	 * 
	 * @param classType
	 * @param revertFlag 判断是否逆序.只针对普通学生时，才需要序列的排序.
	 * @param resultFile
	 * @param row 行数.
	 */
	public void dataReDivide(String classType,Boolean revertFlag,int row,String resultFile,String tableName) throws Exception{
		
		DB db = new DB();
		
		String sort = "asc";
		if(revertFlag)
			sort = "desc";
		String sqlString = "select *,consume_sumAmount/consume_sumDays as consume_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days) as mess_sumDays,(consume_sumAmount - consume_mess_9_10_amount - consume_mess_11_1_amount - consume_mess_2_amount - consume_mess_3_5_amount)/consume_sumDays as removeMess_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days)/consume_SumDays as mess_RatioDays,(consume_mess_9_10_Amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/consume_sumAmount as mess_RatioAmount,(consume_bathe_9_10_days + consume_bathe_11_1_days + consume_bathe_2_days + consume_bathe_3_5_days)/consume_sumDays as bathe_RatioDays,(consume_bathe_9_10_amount + consume_bathe_11_1_amount + consume_bathe_2_amount + consume_bathe_3_5_amount)/consume_sumAmount as bathe_RatioAmount,(consume_clothes_9_10_days + consume_clothes_11_1_days + consume_clothes_2_days + consume_clothes_3_5_days)/consume_sumDays as clothes_RatioDays,(consume_clothes_9_10_amount + consume_clothes_11_1_amount + consume_clothes_3_5_amount + consume_clothes_2_amount)/consume_sumAmount as clothes_RatioAmount,(consume_water_9_10_days + consume_water_11_1_days + consume_water_2_days + consume_water_3_5_days)/consume_sumDays as water_RatioDays,(consume_water_9_10_amount + consume_water_11_1_amount + consume_water_2_amount + consume_water_3_5_amount)/consume_sumAmount as water_RatioAmount,(consume_supmarket_9_10_days + consume_supmarket_11_1_days + consume_supmarket_2_days + consume_supmarket_3_5_days)/consume_sumDays as supmarket_RatioDays,(consume_supmarket_9_10_amount + consume_supmarket_11_1_amount + consume_supmarket_2_amount + consume_supmarket_3_5_amount)/consume_sumamount as supmarket_Ratioamount,(consume_library_9_10_days + consume_library_11_1_days + consume_library_2_days + consume_library_3_5_days)/consume_sumDays as library_sumRatioDays,(consume_library_9_10_amount + consume_library_11_1_amount + consume_library_2_amount + consume_library_3_5_amount)/consume_sumamount as library_sumRatioamount,(consume_driver_9_10_days + consume_driver_11_1_days + consume_driver_2_days + consume_driver_3_5_days)/consume_sumDays as driver_sumRatioDays,(consume_driver_9_10_amount + consume_driver_11_1_amount + consume_driver_2_amount + consume_driver_3_5_amount)/consume_sumamount as driver_sumRatioamount,(consume_classroom_9_10_days + consume_classroom_11_1_days + consume_classroom_2_days + consume_classroom_3_5_days)/consume_sumDays as classroom_sumRatioDays,(consume_classroom_9_10_amount + consume_classroom_11_1_amount + consume_classroom_2_amount + consume_classroom_3_5_amount)/consume_sumamount as classroom_sumRatioamount,(consume_mess_9_10_amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/meal_sumCount as AverMeal,(meal_9_10_lunch_count)/consume_9_10_days as meal_9_10_lunch_DaysRatio,meal_11_1_lunch_count/consume_11_1_days as meal_11_1_lunch_RatioDay,meal_2_lunch_count/consume_2_days as lunch_2_RatioDays,meal_3_5_lunch_count/consume_3_5_days as lunch_3_5_RatioDays ,(meal_9_10_breakfast_count)/consume_9_10_days as meal_9_10_breakfast_DaysRatio,meal_11_1_breakfast_count/consume_11_1_days as meal_11_1_breakfast_RatioDay,meal_2_breakfast_count/consume_2_days as breakfast_2_RatioDays,meal_3_5_breakfast_count/consume_3_5_days as breakfast_3_5_RatioDays,(meal_9_10_dinner_count)/consume_9_10_days as meal_9_10_dinner_DaysRatio,meal_11_1_dinner_count/consume_11_1_days as meal_11_1_dinner_RatioDay,meal_2_dinner_count/consume_2_days as dinner_2_RatioDays,meal_3_5_dinner_count/consume_3_5_days as dinner_3_5_RatioDays  from " + tableName +  " where (sid like '2013%') and consume_sumDays > 130 and class = '" + classType + "' order by  (consume_sumAmount)/consume_sumDays " + sort + " limit 0," + row; 
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		ResultSetMetaData metaData = rSet.getMetaData();
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		int columns = metaData.getColumnCount();
		StringBuffer titleBuffer = new StringBuffer();
		int classIndex = 0;
		for(int i = 1; i <= columns; i++){
			String columnName = metaData.getColumnName(i);
			if(columnName.equals("class")){
				classIndex = i;
				continue;
			}
			titleBuffer.append(columnName);
			titleBuffer.append(",");
		}
		titleBuffer.append("class"); //类别信息放到最后一行!
		String title = titleBuffer.toString();
		resultWriter.write(title);
		resultWriter.newLine();
		
		while(rSet.next()){
			
			StringBuffer valueBuffer = new StringBuffer();
			for(int i = 1; i <= columns; i++){				
				if(i == classIndex)
					continue;
				String value = rSet.getString(i);
				if(value == null || value.equals("null")) //如果除数为0，则需要考虑到这种情况.
					value = "0";
				valueBuffer.append(value);
				valueBuffer.append(",");				
			}
				
			valueBuffer.append(rSet.getString(classIndex));
			String valueStr = valueBuffer.toString();
			resultWriter.write(valueStr);
			resultWriter.newLine();
			
		}
		
		rSet.close();
		db.close();
		resultWriter.close();
		
	}
	
	
	/**
	 * 抽取测试数据，相当于在线测试数据.
	 * @param sqlString 这个sqlString 一定要与作为训练集的上面的sql 语句要保持一致!!!
	 * @param resultFile
	 * @throws Exception
	 */
	public void testDataAbstract(String sqlString,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		
		DB db = new DB();
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		ResultSetMetaData metaData = rSet.getMetaData();
		
		StringBuffer titleBuffer = new StringBuffer();
		
		int columns = metaData.getColumnCount();
		
		
		int classIndex = -1;
		
		for(int i = 1; i <= columns; i++){
			
			
			String columnName = metaData.getColumnName(i);
			if(columnName.equals("class")){
				
				classIndex = i;
				continue;
				
			}
			titleBuffer.append(columnName);
			titleBuffer.append(",");			
		}
		
		if(classIndex == -1){
			
			System.out.println("WithOut class Index !");
			System.exit(0);
			
		}
		titleBuffer.append("class");
		String title = titleBuffer.toString();
		resultWriter.write(title);
		resultWriter.newLine();
		
		
		while(rSet.next()){
			
			StringBuffer valueBuffer = new StringBuffer();
			String value = null;
			for(int i = 1; i <= columns; i++){
				
				if(i != classIndex){					
					value = rSet.getString(i);
					if(value == null || value.equals("null"))
						value = "0";
					valueBuffer.append(value);
					valueBuffer.append(",");					
				}				
			}
			
			value = rSet.getString(classIndex);
			valueBuffer.append(value);
			String string = valueBuffer.toString();
			resultWriter.write(string);
			resultWriter.newLine();			
		}
		
		rSet.close();
		db.close();
		resultWriter.close();
		
	}
	
	/**
	 * 将上述所重新划分的学生整合在一起!
	 * @param fileList
	 * @param resultFile
	 * @throws Exception
	 */
	public void dataCombine(List<String> fileList,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile));
		
		BufferedReader dataReader = null;		
		
		List<BufferedReader> readerList = new ArrayList<>();
		
		for(String file : fileList){
			
			dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			readerList.add(dataReader);			
		}
		
		List<String> resultList = new ArrayList<>();

		boolean titleFlag = true;
		for(BufferedReader reader : readerList){
			
			String str = null;
			String title = reader.readLine();
			if(titleFlag){
				resultWriter.write(title);
				resultWriter.newLine();
				titleFlag = false;
			}
			while((str = reader.readLine()) != null){								
				resultList.add(str);				
			}
			
			reader.close();
			
		}
		Collections.shuffle(resultList);
		for(String str : resultList){		
			
			resultWriter.write(str);
			resultWriter.newLine();		
			
		}		
		resultWriter.close();		
	}
	
}
