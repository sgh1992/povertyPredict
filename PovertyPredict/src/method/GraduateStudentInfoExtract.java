package method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import method.MethodCall;
import method.TimeCall;


/**
 * 消费数据毕业年级数据提取.暂时只提取2009级，2010级，2011级，2012级，2013级，2014级学生的消费数据.
 * @author Administrator
 *
 */
public class GraduateStudentInfoExtract {
	
	/**
	 * 从原始消费数据表中提取出毕业年级的数据.
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void dataExtract(String dataFile,String resultFile) throws Exception{
		
		
		MethodCall methodCall = new MethodCall();
		HashMap<String, String> sidAndGradeMap = methodCall.getStudentNoMap();
		
		
        long startTime = System.currentTimeMillis();
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		String string = "";
		
		String title = "cardNo,stuempNo,transName,deviceName,devphyid,transDate,transTime,amount,cardftbal,rn,month";
		resultWriter.write(title);
		resultWriter.newLine();
		
		int row = 0;
		int debugRow = 0;
		while((str = dataReader.readLine()) != null){
			
			String[] arrays = str.split(",",-1);
			
			if(arrays.length < 6){ //导出数据时，可能会有问题.
				
				debugRow++;
				continue;				
			}
			String transDate = arrays[5];
        			
			String studentNo = arrays[1];
			
			String enrollYear = null; //入学年级，如2008级，2009级,2010级,2011级，2012级，2013级
					
			if(sidAndGradeMap.containsKey(studentNo)){
				enrollYear = sidAndGradeMap.get(studentNo);
				
				String enrollMonth = enrollYear + "09";
				
				//判断这个学生是否为延期毕业的学生.
				int month = 1;
				String transMonth = transDate.substring(0,6); 
				while(enrollMonth.compareTo(transMonth) > 0){
								
					enrollMonth = TimeCall.getMonth(enrollMonth, -month * 12);
					month++;	
					continue; //
				}
				
				int distanceMonth = TimeCall.distanceMonth(transMonth, enrollMonth) + 1; //与刚开学时相差的月份.
				
				string = str + "," + distanceMonth;
				
				resultWriter.write(string);
				resultWriter.newLine();
							
				row++;
				
				if(row % 50000000 == 0)
					System.out.println("solves the row of " + row);			
			}				
			
		}
		
		dataReader.close();
		resultWriter.close();
		
		System.out.println("total row is: " + row);
		System.out.println("the array length < 5 is: " + debugRow);
		
		long endTime = System.currentTimeMillis();
		System.out.println("the function takes " + ((endTime - startTime)/1000) + " seconds");

		
	}
	

	/**
	 * 从已经提取出的消费统计表中，由于数据量太大，month24HoursConsume, 只提取一个年级的数据来可视化操作.
	 * @param dataFile
	 * @param resultFile
	 * @param year 是所对应的应该抽取的年级的学生. 例如:2010级，2011级，2012级，2013级。
	 * @throws Exception
	 */
	public void MonthConsumeDataAbstract(String dataFile,String resultFile,String year) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		MethodCall methodCall = new MethodCall();
		
		
		//学生真实学号与匿名化学号的映射关系.
		HashMap<String, String> tureSidSnoMap = methodCall.getTrueSidMap();//<tureSid,sno>
		
		//这两个sidMap是有区别的.这个是一个匿名化之后的学号与学年的映射关系.
		HashMap<String, String> sidMap = methodCall.getSidAndYear();
		HashSet<String> yearSidSet = new HashSet<>();
		
		Set<String> keySet = sidMap.keySet();
		for(String sid : keySet){
			String admission = sidMap.get(sid);
			if(admission.equals(year))
				yearSidSet.add(sid);
		}
		
		String title = dataReader.readLine();
		System.out.println("the title length is: " + title.split(",", -1).length);
		resultWriter.write(title);
		resultWriter.newLine();
		
		while((str = dataReader.readLine()) != null){
			
			String[] arrays = str.split(",", -1);
			String sid = arrays[0];
			
			if(tureSidSnoMap.containsKey(sid)){
				
				sid = tureSidSnoMap.get(sid);//先将真实的学号转化为匿名化后的学号,再按照匿名化的学号来获得学生的学年.
				if(sidMap.containsKey(sid)){
					
					String gradeYear = sidMap.get(sid);

					if(gradeYear.equals(year)){
						yearSidSet.remove(sid);
						String string = sid + ",";
						for(int i = 1; i < arrays.length; i++)
							string = string + arrays[i] + ",";
						string = string.substring(0,string.length() - 1);
						resultWriter.write(string);
						resultWriter.newLine();
						//System.out.println(str.split(",", -1).length);						
					}
					
				}
				else {
					System.out.println("基本信息表中没有这个学生:" + sid);
				}
				
			}
			else {
				
				System.out.println("Map 表中没有这个学生: " + sid);
				
			}
			

			
		}
		
		dataReader.close();
		resultWriter.close();

		System.out.println("在 " + year + " 年入学的学生在行为表中没有出现的学号为: ");
		for(String sid : yearSidSet)
			System.out.println(sid);
		
	}
	
	/**
	 * 这个方法与之前的方法的不同之处是，这个方法是完全针对匿名化的数据，不需要将学号转化为匿名化的学号，再操作.
	 * 1.将毕业年级中的某个年级的学生数据单独抽出来，再加上它的毕业去向数据，用来做建模使用.如2010级.
	 * 2.将非毕业年级的学生数据单独抽出来，用来做预测时用的着!
	 * @param dataFile
	 * @param resultFile
	 * @param year 是所对应的应该抽取的年级的学生. 例如:2010级，2011级，2012级，2013级。
	 * @throws Exception
	 */
	public void YearDataAbstractNew(String dataFile,String GraduateStudentResultFile,String unGraduateStudentResultFile,String year,Set<String> unGraduateSYearSet) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(GraduateStudentResultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		BufferedWriter	unGraduateStudentresultWriter = new BufferedWriter(new FileWriter(unGraduateStudentResultFile)); 		

		
		String str = null;
		
		Remote_MethodCall methodCall = new Remote_MethodCall();
		
		//这个是一个匿名化之后的学号与学年的映射关系.
		HashMap<String, String> sidMap = methodCall.getSidAndYear();
		HashMap<String, String> sidAndWorkMap = methodCall.graduateAnnoySidMap(); //匿名化的学号与毕业去向.
		
		String title = dataReader.readLine();
		System.out.println("the title length is: " + title.split(",", -1).length);
		resultWriter.write(title);
		resultWriter.newLine();
		
		unGraduateStudentresultWriter.write(title);
		unGraduateStudentresultWriter.newLine();
		
		while((str = dataReader.readLine()) != null){
			
			String[] arrays = str.split(",", -1);
			String sid = arrays[0];
				
			if(sidMap.containsKey(sid)){
					
				String gradeYear = sidMap.get(sid);

				
				//毕业年级的数据,即用来建模时的数据.
				if(gradeYear.equals(year)){

					if(sidAndWorkMap.containsKey(sid)){						
						String work = sidAndWorkMap.get(sid);
						StringBuffer stringBuffer = new StringBuffer();
						stringBuffer.append(str);
						stringBuffer.append(",");
						stringBuffer.append(work);
						
						str = stringBuffer.toString();
						resultWriter.write(str);
						resultWriter.newLine();
						System.out.println(str.split(",", -1).length);
					}					
				}
				
				//针对非毕业年级的数据，用来做预测时的数据!
				else if(unGraduateSYearSet.contains(gradeYear)){
					
					unGraduateStudentresultWriter.write(str);
					unGraduateStudentresultWriter.newLine();					
				}					
			}							
		}		
		dataReader.close();
		resultWriter.close();
		unGraduateStudentresultWriter.close();

		
	}
	
	
	
	

}
