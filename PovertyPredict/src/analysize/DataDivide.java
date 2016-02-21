package analysize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * 选取出贫困生与非贫困生，做为训练集.
 * @author Administrator
 *
 */
public class DataDivide {
	
	
	private String dataFile = null;
	
	private String resultFileAll = null;
	
	private String resultFile2014 = null;
	
	
	private HashSet<String> someSidSet = null; //需要预测的学生年级.
	
	public DataDivide(){
		
		someSidSet = new HashSet<>();
		someSidSet.add("2010");
		someSidSet.add("2011");
		someSidSet.add("2012");
		someSidSet.add("2013");
		someSidSet.add("2014");
		
		
	}
	
	public void setDataFile(String dataFile){
		
		this.dataFile = dataFile;
		
	}
	
	public void setSidSet(HashSet<String> sidSet){
		
		this.someSidSet = sidSet;
	}
	
	public void setResultFileAll(String resultFileAll){
		
		this.resultFileAll = resultFileAll;
	}
	
	public void setResultFile2014(String resultFile2014){
		
		this.resultFile2014 = resultFile2014;
		
	}
	

	
	/**
	 * 获取算法模型数据!
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void getModelData(String dataFile,String resultFile2012_2013) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile2012_2013)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		ImpovrishPeople impoverish = new ImpovrishPeople();
		HashMap<String, String> poorMap =  impoverish.getFinallyMap();
						
		String str = null;
		
		String title = dataReader.readLine();
		title = title + "," + "class";
		resultWriter.write(title);
		resultWriter.newLine();
				
		int poorNum = 0;		
		while((str = dataReader.readLine()) != null){			
			String[] arrays = str.split(",", -1);
			String sid = arrays[0];
			String sidSubstr = sid.substring(0,4);
			String classType = "normal";
			
			if(sid.length() <= 12)
				continue; //只针对本科生来进行分析!
			
			if(someSidSet.contains(sidSubstr)){				
				if(poorMap.containsKey(sid)){
					//classType的值根据需求在变化.
					//classType = "poverty";					
					classType = poorMap.get(sid);
					poorNum++;
				}
				
				String string = str + "," + classType;				
							
				resultWriter.write(string);
				resultWriter.newLine();								
			}			
		}
		
		dataReader.close();
		resultWriter.close();
		System.out.println("poor Num is: " + poorNum);		
	}

}
