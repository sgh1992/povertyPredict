package method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * 学生的所有行为数据整合
 * 包括学生基本信息，借阅行为，成绩行为，奖助，贷款行为，图书门禁行为，消费行为
 * @author Administrator
 *
 */
public class StudentAllBehaviorDataCombine {
	public void StudentAllBehaviorDataIntegerd(List<String> behaviorDataFileList,String Printfile){
		List<BufferedReader> studentbehaviorReadersList = new ArrayList<BufferedReader>();
		//<sid,所有行为数据(以一条语句的方式表达)>
		HashMap<String, String> studentAllbehaviorMap = new HashMap<String, String>();
		
		//初始化，使之后续处理尽量统一
		InitialMap(studentAllbehaviorMap);
		String title = null;
		BufferedReader dataReader = null;
		try {
			for(String datafile : behaviorDataFileList){
				dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(datafile),"utf-8"));
				studentbehaviorReadersList.add(dataReader);
			}
			title = StudentDataIntegrationDetails(studentAllbehaviorMap, studentbehaviorReadersList);
			for(BufferedReader reader : studentbehaviorReadersList)
				reader.close();
			PrintStudentAllBehaviorMap(studentAllbehaviorMap, title, Printfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 打印学生所有行为数据
	 * @param studentAllbehaviorMap
	 * @param title 标题字段
	 * @param Printfile
	 */
	public void PrintStudentAllBehaviorMap(HashMap<String, String> studentAllbehaviorMap,
			String title,String Printfile){
		String string = null;
		System.out.println("title length: " + title.split(",",-1).length);
		try {
			BufferedWriter studentInfoWriter = new BufferedWriter(new FileWriter(Printfile));
			studentInfoWriter.write(title);
			studentInfoWriter.newLine();
			Iterator<Map.Entry<String, String>> iterator = studentAllbehaviorMap.entrySet().iterator();
			Map.Entry<String, String> entry = null;
			while(iterator.hasNext()){
				entry = iterator.next();
				String sid = entry.getKey();
				string = entry.getValue();
				string = string.substring(0,string.length() - 1);
				String[] arrays = string.split(",", -1);
				if(arrays.length < 2)
					continue;
				System.out.println(string.split(",",-1).length);
				studentInfoWriter.write(string);
				studentInfoWriter.newLine();
			}
			studentInfoWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**<sid,所有行为数据(以一条语句的方式表达)>
	 *初始化map
	 * @param studentAllbehaviorMap
	 */
	public void InitialMap(HashMap<String, String> studentAllbehaviorMap){
		MethodCall method = new MethodCall();
		List<String> sidList = method.getSidList();
		for(String sid : sidList){
			studentAllbehaviorMap.put(sid, sid + ",");
		}
	}
	/**
	 * 学生行为数据整合的详细操作
	 * @param studentAllbehaviorMap
	 * @param studentbehaviorReadersList
	 * @return 返回学生行为的title字段
	 */
	@SuppressWarnings("finally")
	public String StudentDataIntegrationDetails(HashMap<String, String> studentAllbehaviorMap,
			List<BufferedReader> studentbehaviorReadersList){
		String sid = null;
		String string = null;
		String str = null;
		String title = "sid,";
		try {
			for(BufferedReader reader : studentbehaviorReadersList){
				int row = 0;
				while((str = reader.readLine()) != null){
					String[] array = str.split(",",-1); //split方法的使用要非常小心，
					//第一行为title字段
					if(row == 0){
						
						for(int i = 1; i < array.length; i++)
							title = title + array[i] + ",";
						row++;
						continue;
					}
					sid = array[0];
					if(studentAllbehaviorMap.containsKey(sid)){
						string = studentAllbehaviorMap.get(sid);
						//将学号去掉
						for(int i = 1; i < array.length; i++)
							string = string + array[i] + ",";
						studentAllbehaviorMap.put(sid, string);
					}					
				}
			}
			System.out.println(title);
			title = title.substring(0,title.length() - 2);
			System.out.println(title);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return title;
		}
		
	}
}
