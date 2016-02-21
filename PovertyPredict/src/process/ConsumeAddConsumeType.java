package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class ConsumeAddConsumeType {
	
	
	public void consumeAddConsumeType(String dataFile,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		 		
		
		String str = null;
		
		String title = dataReader.readLine();
		title = title + "," + "type";
		resultWriter.write(title);
		resultWriter.newLine();
		int row = 0;

		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			
			String place = array[3];
			
			String type = getType(place);
			
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(str);
			stringBuffer.append(",");
			stringBuffer.append(type);
			resultWriter.write(stringBuffer.toString());
			resultWriter.newLine();
			
			row++;			
			if(row % 5000000 == 0)
				System.out.println("current row is: " + row);
			
		}
		
		resultWriter.close();
		dataReader.close();
		
		
	}
	
	
	/**
	 * 根据消费地点得到相应的消费类型.
	 * @param consumePlace
	 * @return
	 */
	public String getType(String consumePlace){
		
		String type = null;
		
		if(consumePlace.contains("食"))
			type = "mess";
		else if(consumePlace.contains("餐厅"))
			type = "mess";
		else if(consumePlace.contains("教学楼"))
			type = "classroom";
		else if(consumePlace.contains("淋"))
			type = "bathe";
		else if(consumePlace.contains("浴"))
			type = "bathe";
		else if(consumePlace.contains("开"))
			type = "water";
		else if(consumePlace.contains("洗"))
			type = "clothes";
		else if(consumePlace.contains("超市"))
			type = "supmarket";
		else if(consumePlace.contains("中间商户"))
			type = "supmarket";
		else if(consumePlace.contains("图书馆"))
			type = "library";
		else if(consumePlace.contains("车"))
			type = "driver";
		else if(consumePlace.contains("分店"))
			type = "supmarket";
		else if(consumePlace.contains("上海周记"))
			type = "supmarket";
		else if (consumePlace.contains("水果店"))
			type = "supmarket";
		else if(consumePlace.contains("校园6店"))
			type = "supmarket";
		else if(consumePlace.contains("百味屋"))
			type = "supmarket";

		else if(consumePlace.contains("文印"))
			type = "print";
		else if(consumePlace.contains("医院"))
			type = "hospital";
		else if(consumePlace.contains("咖啡"))
			type = "leisure";
		else if(consumePlace.contains("茶楼"))
			type = "leisure";
		else if(consumePlace.contains("电影"))
			type = "leisure";
		else if(consumePlace.contains("休闲水吧"))
			type = "leisure";
		else if(consumePlace.contains("游泳池"))
			type = "leisure";
		else if(consumePlace.contains("球场"))
			type = "leisure";

		return type;
		
	}
	

}
