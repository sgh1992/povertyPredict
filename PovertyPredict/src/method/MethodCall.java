package method;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dataBase.DB;
import dataBase.DB1;


public class MethodCall {
	

	/**
	 * 获得所有学生的学号与所对应的年级 sid,grade
	 * 注意这里的学号是指真实的学号.
	 * @return
	 */
	public HashMap<String, String> getStudentNoMap() throws Exception{
		
		HashMap<String, String> snoAndSidMap = sidMap();
		
		DB1 db = new DB1();
		
		String sqlString = "select * from new_studentbasicinfo";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> sidAndGradeMap = new HashMap<>();
		
		while(rSet.next()){
			
			String sid = rSet.getString("sid"); //这个学生匿名化的学号.
			
			String grade = rSet.getString("grade"); //学生所在的年级.
			
			if(snoAndSidMap.containsKey(sid))
				sid = snoAndSidMap.get(sid); //获得这个学生真实的学号.
			
			else {
				System.out.println("The anonymous sid of " + sid + " is not found in the map table!");
				//System.exit(0);
			}
			
			if(!sidAndGradeMap.containsKey(sid))
				sidAndGradeMap.put(sid, grade);
			
		}
		
		rSet.close();
		db.close();
		
		return sidAndGradeMap;
		
		
	}
	
	
	/**
	 * 获得学生的学号与对应的入学年级的Map,匿名之后的学号.
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getSidAndYear() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select sid,grade from new_studentbasicinfo";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> sidAndGradeMap = new HashMap<>();
		
		while(rSet.next()){
			
			String sid = rSet.getString("sid");
			String grade = rSet.getString("grade");
			
			if(!sidAndGradeMap.containsKey(sid))
				sidAndGradeMap.put(sid, grade);
			
		}
		
		db.close();
		return sidAndGradeMap;
		
		
		
	}
	
	public HashMap<String, String> getSidAndOtherInfoMap() throws Exception{
		
		HashMap<String, String> sidAndOtherInfoMapString = new HashMap<>();
		
		DB db = new DB();
		String sqlString = "select sid,gender,nation,college,major,grade from new_studentbasicinfo where sid >='2010'";
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> snoSidMap = sidMap();
		
		
		while(rSet.next()){
			String trueSid = null;
			String sid = rSet.getString("sid");
			if(sid.equals("2013065079"))
				System.out.println("debug");
			
			if(snoSidMap.containsKey(sid))
				trueSid = snoSidMap.get(sid);
			else {
				continue;
			}
			String gender = rSet.getString("gender");
			String major = rSet.getString("major");
			String year = rSet.getString("grade");
			String college = rSet.getString("college");

			if(gender == null)
				gender = "男";
			if(major == null)
				major = getMajor(trueSid, college);
			
			String otherInfo = gender + "," + college + "," + major + "," + year;
			if(!sidAndOtherInfoMapString.containsKey(sid))
				sidAndOtherInfoMapString.put(sid, otherInfo);
			
		}
		
		return sidAndOtherInfoMapString;
		
	}
	
	public String getMajor(String sid,String college){
		
		String majorNum = sid.substring(6,8);
		
		String major = null;
		if(college.equals("英才实验学院"))
			major = "电子信息工程";
		else if(college.equals("通信与信息工程学院"))
			if(majorNum.equals("01"))
				major = "信息工程";
			else if(majorNum.equals("02"))
				major = "网络工程";
			else 
				major = "通信工程";
		
		else if(college.equals("电子工程学院")){
			
			if(majorNum.equals("00"))
				major = "电磁场与无线技术";
			else if(majorNum.equals("10"))
				major = "电子信息工程";
			else if(majorNum.equals("30"))
				major = "信息对抗技术";
			else 
				major = "无线技术";
			
		}
		
		
		else if(college.equals("微电子与固体电子学院")){
			
			if(majorNum.equals("00"))
				major = "电子科学与技术(微电子技术)";
			else if(majorNum.equals("10"))
				major = "微电子学";
			else if(majorNum.equals("20"))
				major = "集成电路设计与集成系统";
			else if(majorNum.equals("30"))
				major = "电子科学与技术(固体电子工程)";
			else 
				major = "应用化学";
			
			
		}
		
		else if(college.equals("光电信息学院"))
			if(majorNum.equals("10"))
				major = "电子科学与技术(光电工程与光通信)";
			else if(majorNum.equals("20"))
				major = "电子科学与技术(物理电子技术)";
			else if(majorNum.equals("30"))
				major = "光信息科学与技术";
			else
				major = "信息显示与光电技术";
	
		else if(college.equals("数学科学学院"))
			if(majorNum.equals("10"))
				major = "数学与应用数学";
			else 
				major = "信息与计算科学";
		
		else if(college.equals("计算机科学与工程学院"))
			if(majorNum.equals("00"))
				major = "计算机科学与技术";
			else 
				major = "信息安全";
		else if(college.equals("经济与管理学院"))
			if(majorNum.equals("10"))
				major = "金融学";
			else 
				major = "工商管理";
		
		else if(college.equals("外国语学院"))
			if(majorNum.equals("10"))
				major = "英语";
			else 
				major = "日语";
		else if(college.equals("格拉斯哥学院"))
			major = "电子信息工程";
		
		return major;
	}
	
	
	/**
	 * 返回真实学号所对应的学生毕业去向.
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> graduateWorkMap() throws Exception{
		
		HashMap<String, String> trueSidWorkMap = new HashMap<>();
		
		HashMap<String, String> sidMap = sidMap();//匿名化的学号，真实的学号.
		
		HashMap<String, String> graduateAnnoySidMap = graduateAnnoySidMap();//匿名化的学号，毕业去向.
		
		Set<String> annoySidSet = graduateAnnoySidMap.keySet();
		for(String annoySid : annoySidSet){
			
			if(sidMap.containsKey(annoySid)){
				
				String sid = sidMap.get(annoySid);//真实的学号.
				String work = graduateAnnoySidMap.get(annoySid);
				
				if(!trueSidWorkMap.containsKey(sid))
					trueSidWorkMap.put(sid, work);
				
			}
			else {
				System.out.println("the annoySid " + annoySid + " is not in map");
			}
			
		}
		
		return trueSidWorkMap;
		
		
		
	}
	
	/**
	 * 返回所有学生的学号，之后所有的学生行为分析，都是基于这个所得的学号来做
	 * @return
	 */
	@SuppressWarnings("finally")
	public List<String> getSidList(){
		DB db = new DB();
		String sqlString = "select distinct sid from new_studentbasicinfo";
		ResultSet rSet = null;
		List<String> sidList = new ArrayList<String>();
		try {
			rSet = db.executeQuery(sqlString);
			while(rSet.next()){
				String sidString = rSet.getString("sid");
				if(!sidList.contains(sidString))
					sidList.add(sidString);
			}
			rSet.close();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return sidList;
		}
	}
	
	/**
	 * 获得毕业年级的学生所对应的毕业去向，<匿名化的学号,毕业去向>
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> graduateAnnoySidMap() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select sid,work from graduateworkinfo";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> graduateSidMap = new HashMap<>();
		
		while(rSet.next()){
			
			String sid = rSet.getString("sid");
			String work = rSet.getString("work");
			
			if(!graduateSidMap.containsKey(sid))
				graduateSidMap.put(sid, work);
			
		}
		
		rSet.close();
		db.close();
		return graduateSidMap;
		
	}
	
	
	/**
	 * 返回匿名化的学号与真实学号之间的映秀关系,Map<sno,TrueSid>
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> sidMap() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select sid,sno from map";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> sidMap = new HashMap<>();
		
		
		while(rSet.next()){
			
			String trueSid = rSet.getString("sid");
			
			String sno = rSet.getString("sno");
			
			if(!sidMap.containsKey(sno))
				sidMap.put(sno, trueSid);
				
		}
		
		rSet.close();
		db.close();
		
		return sidMap;
		
	}
	
	/**
	 * 返回真实的学号与匿名学号之间的映秀关系,Map<TrueSid,sno>
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getTrueSidMap() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select sid,sno from map";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		HashMap<String, String> sidMap = new HashMap<>();
		
		
		while(rSet.next()){
			
			String trueSid = rSet.getString("sid");
			
			String sno = rSet.getString("sno");
			
			if(!sidMap.containsKey(trueSid))
				sidMap.put(trueSid, sno);
				
		}
		
		rSet.close();
		db.close();
		
		return sidMap;
		
	}
	
	
	/**
	 * 返回图书的编号与对应的图书类别.<itemNum,bookClass>
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> bookClassMap() throws Exception{
		
		HashMap<String, String> bookClassMap = new HashMap<>();
		
		DB db = new DB();
		
		String sqlString = "select itemNum,bookClass from librarybookinfo";
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		while(rSet.next()){
			
			String itemNum = rSet.getString("itemNum");
			String bookClass = rSet.getString("bookClass");
			
			if(!bookClassMap.containsKey(itemNum)){
				
				bookClassMap.put(itemNum, bookClass);
				
			}
			
		}
		db.close();
		return bookClassMap;
		
	}
	
	
	

}
