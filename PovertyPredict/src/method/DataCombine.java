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

import javax.swing.text.WrappedPlainView;

import weka.gui.visualize.LegendPanel;

public class DataCombine {
	
	/**
	 * 合并的思想，将各种行为数据，以各个文件的形式存放
	 * 
	 * 1.确保每个学生在各个行为领域都必须要有数据，那么就需要有一个统一的sidSet：
	 * 即学生至少在各个行为领域至少出现过一次，所有的sidSet都以行为文件为主,而不是以原来的学生基本信息表为主.
	 * 
	 * 2.如何将每个学生的各个行为领域的数据合并成一条记录呢.
	 * <sid,BehaviorStr>,以behaviorMap 为存储结构，能够快速的匹配这个学生的各种行为数据.
	 * 
	 * 3.如何确保每个学生在各个行为领域都有数据，在每次遍历一次文件时，都应该有一个包含所有学生的sidSet(新的)，
	 * 如果这个文件中包含了这个sid，则用这个领域的数据填充之，如果没有，则将之设置为缺省值，
	 * 
	 * 
	 * 
	 * @param dataFileList
	 * @param resultFile
	 * @throws Exception
	 */
	public void studentAllDataCombine(List<String> dataFileList,String resultFile) throws Exception{
		
		HashMap<String, String > sidAndBehaviorMap = new HashMap<>();
				
		HashMap<String, String> sidMap = getSidMap(dataFileList);
		
		Set<String> origialSet = sidMap.keySet(); //注意，这个origialSet是不能修改的，一旦改变了这个值，就对应的改变了sidMap中的map对.
				
		initalMap(sidAndBehaviorMap, sidMap);
		
		BufferedReader dataReader = null;
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		

		StringBuffer titleBuffer = new StringBuffer();
		
		for(String file : dataFileList){
			
			
			dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			String dataTitle = dataReader.readLine();
			String[] dataTitleArray = dataTitle.split(",", -1);
			
			if(titleBuffer.length() == 0){
				titleBuffer.append(dataTitle);
				titleBuffer.append(",");
			}
			
			else {
				for(int i = 1; i < dataTitleArray.length; i++){
					titleBuffer.append(dataTitleArray[i]);
					titleBuffer.append(",");
				}
			}
			
			String str = null;			
			
			/**
			 * 这里要非常的注意，不能直接sidSet = sidMap.keySet()
			 * 这样所创造的Set只是一个引用，如果改变了sidSet的值，那么相对应的sidMap中的map也会相应的改变.!!!!!!
			 * 
			 */
			Set<String> sidSet = new HashSet<String>();
			int length = dataTitleArray.length;
			for(String sid : origialSet)
				sidSet.add(sid);

			while((str = dataReader.readLine()) != null){
								
				String[] arrays = str.split(",", -1);
				String sid = arrays[0];				
				sidSet.remove(sid);				
				String valueStr = sidAndBehaviorMap.get(sid);
				
				
				StringBuffer valueBuffer = new StringBuffer();
				valueBuffer.append(valueStr);

				for(int i = 1; i < arrays.length; i++){
					
					valueBuffer.append(arrays[i]);
					valueBuffer.append(",");
				}
				
				valueStr = valueBuffer.toString();

				sidAndBehaviorMap.put(sid, valueStr);
				
			}
			
			for(String sid : sidSet){
				

				String valueStr = sidAndBehaviorMap.get(sid);
				StringBuffer valueBuffer = new StringBuffer();
				valueBuffer.append(valueStr);
				for(int i = 1; i < length; i++){
					
					valueBuffer.append("");
					valueBuffer.append(",");
				}
				valueStr = valueBuffer.toString();
								
				sidAndBehaviorMap.put(sid, valueStr);				
			}
			
			dataReader.close();

		}
		
		String title = titleBuffer.toString();
		title = title.substring(0,title.length() - 1);
		System.out.println("title length is: " + title.split(",", -1).length);
		resultWriter.write(title);
		resultWriter.newLine();
		
		PrintDataCombine(sidAndBehaviorMap, resultWriter);
		resultWriter.close();
		
	}
	
	/**
	 * 打印合并的的学生所有行为的数据.
	 * @param sidAndBehaviorMap
	 * @param writer
	 * @throws Exception
	 */
	public void PrintDataCombine(HashMap<String, String> sidAndBehaviorMap,BufferedWriter writer) throws Exception{
		
				
		Set<String> sidSet = sidAndBehaviorMap.keySet();
		for(String sid : sidSet){
			
			String valueStr = sidAndBehaviorMap.get(sid);
			valueStr = valueStr.substring(0,valueStr.length() - 1);
			valueStr = sid + "," + valueStr;
			
			System.out.println(valueStr.split(",", -1).length);
			writer.write(valueStr);
			writer.newLine();
			
		}
		
		
	}
	
	
	/**
	 * 将若干个文件整合成一个文件，即要统一的sid，一切都以这个sid为标准.
	 * @param sidAndAllBehaviorMap
	 * @param sidMap
	 */
	public void initalMap(HashMap<String, String> sidAndAllBehaviorMap,HashMap<String, String> sidMap){
		
		Set<String> sidSet = sidMap.keySet();
		for(String sid : sidSet){
			sidAndAllBehaviorMap.put(sid, "");
		}
		
		
		
	}
	
	
	/**
	 * 根据不同的行为数据，来获取至少在一个领域内有行为的学生学号.
	 * @param fileList
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getSidMap(List<String> fileList) throws Exception{
		
		BufferedReader dataReader = null;
		
		HashMap<String, String> sidAndBehaviorMap = new HashMap<>();
		
		for(String file : fileList){
			
			dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			
			String str = null;
			
			dataReader.readLine();
			while((str = dataReader.readLine()) != null){
				
				String sid = str.split(",", -1)[0];
				if(!sidAndBehaviorMap.containsKey(sid))
					sidAndBehaviorMap.put(sid, "");
			}
			
			dataReader.close();
			
		}
		
		return sidAndBehaviorMap;
		
	}

}
