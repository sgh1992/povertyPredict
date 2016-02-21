package analysize;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import method.MethodCall;

import dataBase.DB;

public class ImpovrishPeople {
	
	private HashMap<String, String > annoySidAndTrueSidMap = null;
	
	public ImpovrishPeople() throws Exception{
		
		MethodCall methodCall = new MethodCall();
		annoySidAndTrueSidMap = methodCall.sidMap();
		
	}
	
	/**
	 * 这个贫困信息相对比较可靠，即认定它为非常贫困.
	 * 以Impoverish 为第三高可信度!
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> judgeImpovrishMap() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select 学号,困难等级  from impoverish "; 
		
		HashMap<String, String> impoverishMap = getImpoverish2013_2014Map();
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		while(rSet.next()){
			
			String studentID = rSet.getString("学号");
			String poorRank = rSet.getString("困难等级");
			if(poorRank.equals("特别贫困"))
				poorRank = "very poverty";
			else 
				poorRank = "general povery";
			
			if(!impoverishMap.containsKey(studentID)){
				impoverishMap.put(studentID, poorRank);
			}			
		}
		
		rSet.close();
		db.close();
		
		return impoverishMap;
		
	}
	
	
	/**
	 * 贫困等级的划分!
	 * 以这个为第二高可信度.
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getImpoverish2013_2014Map() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select 学号,认定困难档次  from impoverish2013_2014_result"; 
		
		HashMap<String, String> impoverishMap = getImpoverish2014_2015Map();
		
		ResultSet rSet = db.executeQuery(sqlString);		
		while(rSet.next()){
			
			String studentID = rSet.getString("学号");
			String poorRank = rSet.getString("认定困难档次");
			
			if(poorRank.equals("特别贫困"))
				poorRank = "very poverty";
			
			else if(poorRank.equals("一般贫困"))
				poorRank = "general povery";
			
			else 
				poorRank = "poorer"; //较贫困.
			if(!impoverishMap.containsKey(studentID)){
				impoverishMap.put(studentID, poorRank);
			}
			
		}
		
		rSet.close();
		db.close();
		
		return impoverishMap;
		
	}
	
	/**
	 * 贫困等级的划分.
	 * 以2014_2015为最高可信度，因为时间最早.
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getImpoverish2014_2015Map() throws Exception{
		
		DB db = new DB();
		
		String sqlString = "select 学号,认定困难档次  from impoverish2014_2015_result"; 
		
		HashMap<String, String> impoverishMap = new HashMap<>();
		
		ResultSet rSet = db.executeQuery(sqlString);
		
		while(rSet.next()){
			
			String studentID = rSet.getString("学号");
			String poorRank = rSet.getString("认定困难档次");
			
			if(poorRank.equals("特别贫困"))
				poorRank = "very poverty";
			
			else if(poorRank.equals("一般贫困"))
				poorRank = "general povery";
			
			else 
				poorRank = "poorer"; //较贫困.
			if(!impoverishMap.containsKey(studentID)){
				impoverishMap.put(studentID, poorRank);
			}
			
		}
		
		rSet.close();
		db.close();		
		return impoverishMap;
		
	}
	
	
	/**
	 * 最终的贫困等级的分类!
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getFinallyMap() throws Exception{
		
		HashMap<String, String> map = judgeImpovrishMap();
		
		return map;
		
		
		
	}
	
	/**
	 * 这个贫困信息只是依据贷款数据来进行辨别的，即认为它是一般贫困!
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> loanPovertyMap() throws Exception{
		
		HashMap<String, String> loanPoorMap = getSubsidyMap(); //已经获得非常贫困的信息与一般贫困的信息!
		
		DB db = new DB();
		String sqlString = "select sid from loan";
		ResultSet rSet = db.executeQuery(sqlString);
		
		while(rSet.next()){
			
			String sid = rSet.getString("sid");
			if(!loanPoorMap.containsKey(sid)){
				sid = annoySidAndTrueSidMap.get(sid);
				loanPoorMap.put(sid, "very poor");
			}
						
		}
		
		rSet.close();
		db.close();
		return loanPoorMap;
		
	}
	
	
	/**
	 * 根据助学金金额数据来辨别贫困生与非贫困生.
	 * 助学金数据有2011-2012,2012-2013,2013-2014年的数据!
	 * 助学金金额占据最高的10%
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getSubsidyMap() throws Exception{
		
		HashMap<String, String> impoverishMap = judgeImpovrishMap(); //从上述得到的非常贫困的学生类别!
		
		HashMap<String, Double> subsidyMap = new HashMap<>();
		
		DB db = new DB();		
		String sqlString = "select sid,sum(payment) as sumpayment from subsidy where sid  REGEXP '^2011|^2010|^2013' group by sid";
		ResultSet rSet = db.executeQuery(sqlString);
		
		while(rSet.next()){
			
			String sid = rSet.getString("sid");
			double sumpayment = rSet.getDouble("sumpayment");
			
			if(sid.startsWith("2010"))
				sumpayment /= 3;
			else if(sid.startsWith("2011"))
				sumpayment /= 3;
			else if(sid.startsWith("2012"))
				sumpayment /= 2;
			
			
			sid = annoySidAndTrueSidMap.get(sid);
			
			if(!subsidyMap.containsKey(sid))
				subsidyMap.put(sid, sumpayment);
			
		}
		
		rSet.close();
		db.close();
		
		List<Map.Entry<String, Double>> paymentList = new ArrayList<Map.Entry<String,Double>>(subsidyMap.entrySet());
		
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(paymentList, new Comparator<Map.Entry<String, Double>>() {
			
			public int compare(Map.Entry<String, Double> o1,Map.Entry<String,Double> o2){
				
				double c = o1.getValue() - o2.getValue();
				if(c > 0)
					return -1; //降序排序.
				else 
					return 1;
				
			}
		});
		
		
		int top10 = paymentList.size()/10; //助学金最高的那个百分之10的学生定为非常贫困!其余的定为一般贫困.
		
		for(int i = 0; i < paymentList.size(); i++){			
			String studentID = paymentList.get(i).getKey();
			if(i < top10){
				if(!impoverishMap.containsKey(studentID))
					impoverishMap.put(studentID, "very poor");
			}
			else{ 
				if(!impoverishMap.containsKey(studentID))
				impoverishMap.put(studentID, "poor");			
			}
		}
		
		return impoverishMap;
		
	}
	
	
	
	
	
	

}
