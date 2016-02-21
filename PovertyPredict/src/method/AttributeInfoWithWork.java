package method;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;

import method.MethodCall;

/**
 * 根据之前所提取的特征数据，另外再加上毕业去向信息数据.
 * @author Administrator
 *
 */
public class AttributeInfoWithWork {
	
	/**
	 * 
	 * @param attributeFile 特征数据文件
	 * @param resultFile 加上毕业去向之后的结果文件.
	 * @throws Exception
	 */
	public void attributeWithWork(String attributeFile, String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(attributeFile), "utf-8"));

		String str = null;
		
		Remote_MethodCall methodCall = new Remote_MethodCall();
		
		//匿名化号的，这个根据实际需要来.
		 
		HashMap<String, String> sidWorkMap = methodCall.graduateAnnoySidMap();
		
		String title = dataReader.readLine();
		title = title + ",work";
		resultWriter.write(title);
		resultWriter.newLine();
		
		while((str = dataReader.readLine()) != null){
			
			String[] arrays = str.split(",", -1);
			String sid = arrays[0];
			if(sidWorkMap.containsKey(sid)){
				
				String work = sidWorkMap.get(sid);
				
				if(work.equals("国防生"))
					continue; //国防生不需要预测.
				
				String string = str + "," + work;
				
				resultWriter.write(string);
				resultWriter.newLine();				
				
			}
		}
		
		dataReader.close();
		resultWriter.close();
		
		
	}

}
