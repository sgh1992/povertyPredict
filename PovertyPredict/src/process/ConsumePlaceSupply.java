package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

import method.*;

import dataBase.DB;


/**
 * 消费数据缺失的地点字段补全.
 * 1.补全思想:将消费数据中deviceName中为null的缺失数据提取出来,并且保证devphyid不能为空
 * 2.根据之前的消费数据，即消费地点没有为空的数据用来与缺失的数据进行对比,然后进行补全.
 * @author Administrator
 *
 */
public class ConsumePlaceSupply {
	
	/**
	 * 地步缺失的记录提取!!!
	 * @param dataFile
	 * @param missingFile
	 * @throws Exception
	 */
	public void extractConsumeMissingPlace(String dataFile,String missingFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(missingFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		String title = dataReader.readLine();
		resultWriter.write(title);
		resultWriter.newLine();
		
		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String deviceName = array[3];
			String dephyid = array[4];
			
			if(deviceName.equals("null") && !dephyid.equals("null")){				
				resultWriter.write(str);
				resultWriter.newLine();				
			}
			
		}
		
		dataReader.close();
		resultWriter.close();
		
	}
	
	/**
	 * 将之前的2013年至2014年的消费数据导入到本地中，
	 * @param resultFile
	 * @throws Exception
	 */
	public void getBeforeDataFile(String resultFile) throws Exception{
		
		DB db = new DB();
		
		int row = 5000000;
		int index = 0;
		
		boolean flag = true;
		
		ResultSet rSet = null;
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 	
		
		MethodCall methodCall = new MethodCall();
		
		HashMap<String, String> sidMap  = methodCall.sidMap(); //匿名化的学号与真实学号之间的映射关系!!!
		
		int r = 0;
		
		while(flag){
			
			String sqlString = "select sid,time,place,amount,balance from consumptionnew limit " + index + "," + row;		
			rSet = db.executeQuery(sqlString);			
			index += row;
			flag = false;
			while(rSet.next()){					
				flag = true;
				StringBuffer stringBuffer = new StringBuffer();
				String sid = rSet.getString("sid");
				String time = rSet.getString("time");
				String place = rSet.getString("place");
				String amount = rSet.getString("amount");
				String balance = rSet.getString("balance");
				
				if(sidMap.containsKey(sid))
					sid = sidMap.get(sid); //将匿名化的学号转换为真实的学号!!!
				else 
					continue;
				
				stringBuffer.append(sid);
				stringBuffer.append(",");
				stringBuffer.append(time);
				stringBuffer.append(",");
				stringBuffer.append(place);
				stringBuffer.append(",");
				stringBuffer.append(amount);
				stringBuffer.append(",");
				stringBuffer.append(balance);
				
				resultWriter.write(stringBuffer.toString());
				resultWriter.newLine();		
				
				r++;
				if(r % 5000000 == 0)
					System.out.println("current row is: " + r);
				
			}			
			
		}
		
		rSet.close();
		db.close();
		resultWriter.close();
				
	}
	
	/**
	 * 数据补全!!!
	 * @param placeSafeFile
	 * @param placeMissingFile
	 * @parm repairedFile 已经补全的设备ID数，这是因为需要
	 * @param resultFile
	 * @throws Exception
	 */
	public void consumePlaceRepair(String placeSafeFile,String placeMissingFile,String repairedFile,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile,true)); 		
		
		BufferedReader placeSafeReader = new BufferedReader(new InputStreamReader(new FileInputStream(placeSafeFile), "utf-8"));

		BufferedReader placeMissingReader = new BufferedReader(new InputStreamReader(new FileInputStream(placeMissingFile), "utf-8"));

		String str = null;
		
		HashMap<String, String> sidTimeMapdevphyid = new HashMap<>(); //<sid + time,devphyid> 标志位.
		
		HashSet<String> devphyidSet = new HashSet<>(); //用于记录devphyid的Set
		
		int row = 0;//
		//获得唯一的ID.
		placeMissingReader.readLine();
		while((str = placeMissingReader.readLine()) != null){
			
			row++;
			if(row <= 30000000)
				continue;
			String[] array = str.split(",", -1);
			String sid = array[1];
			String devphyid = array[4].trim();
			String date = array[5].trim();
			String hour = array[6].trim();
			String time = date + " " + hour;
			String sidTime = sid + "," + time;
			if(!sidTimeMapdevphyid.containsKey(sidTime)){
				sidTimeMapdevphyid.put(sidTime, devphyid);
				if(!devphyidSet.contains(devphyid))
					devphyidSet.add(devphyid);
			}
			
			if(row % 5000000 == 0){
				System.out.println("current row is : " + row);
				System.out.println("current sidTimeMap size is :" + sidTimeMapdevphyid.size());
			}
			
			if(row % 30000000 == 0)
				break;
			
		}
		
		System.out.println("start Repair!!");
		int totalDevphyidNum = devphyidSet.size(); //总的缺失的设备ID数.
		devphyidSet.clear();
		devphyidSet = getRepairSet(repairedFile); //已经补全的设备ID.
		int repairNum = 0;
		while((str = placeSafeReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String sid = array[0];
			String time = array[1];
			String place = array[2];
			String sidTime = sid + "," + time;
			if(sidTimeMapdevphyid.containsKey(sidTime)){
				
				String devphyid = sidTimeMapdevphyid.get(sidTime);
				
				if(!devphyidSet.contains(devphyid)){
					devphyidSet.add(devphyid);
					String string = devphyid + "," + place;
					resultWriter.write(string);
					resultWriter.newLine();
					repairNum++;
				}
								
			}
			
		}
		
		System.out.println("缺失的设备ID个数有: " + totalDevphyidNum);
		System.out.println("补全的设备ID个数有: " + repairNum + "\n" + "补全的设备ID数所占的比例为: " + (repairNum/(totalDevphyidNum + 0.0)));
		
		
		placeSafeReader.close();
		placeMissingReader.close();
		resultWriter.close();
		
		
	}
	
	/**
	 * 得到补全的设备ID号与地点之间的映射表!!!
	 * @param repairFile
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getRepairMap(String repairFile) throws Exception{
		
		HashMap<String, String> repairMap = new HashMap<>();
		
		BufferedReader repairPlaceReader = new BufferedReader(new InputStreamReader(new FileInputStream(repairFile), "utf-8"));

		String str = null;
		
		while((str = repairPlaceReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String devhpyid = array[0];
			String place = array[1];
			if(!repairMap.containsKey(devhpyid))
				repairMap.put(devhpyid, place);
			
		}
		
		repairPlaceReader.close();
		return repairMap;
		
		
	}
	/**
	 * 得到补全的设备ID号Set
	 * @param repairFile
	 * @return
	 * @throws Exception
	 */
	public HashSet<String> getRepairSet(String repairFile) throws Exception{
		
		HashSet<String> repairSet = new HashSet<>();
		
		BufferedReader repairPlaceReader = new BufferedReader(new InputStreamReader(new FileInputStream(repairFile), "utf-8"));

		String str = null;
		
		while((str = repairPlaceReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String devhpyid = array[0];
			if(!repairSet.contains(devhpyid))
				repairSet.add(devhpyid);
			
		}
		
		repairPlaceReader.close();
		return repairSet;
		
		
	}
	
	public void consumePlaceRepairedApply(String consumeDataFile,String repairPlace,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(consumeDataFile), "utf-8"));
		
		String str = null;
		
		HashMap<String, String> devphyidMapPlace = getRepairMap(repairPlace);

		
		String title = dataReader.readLine();
		resultWriter.write(title);
		resultWriter.newLine();
		int row = 0;
		int missRow = 0; //仍然缺失设备ID的行数.
		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			if(array.length < 10)
				continue;
			String deviceName = array[3];
			String devphyid = array[4];
			
			StringBuffer stringBuffer = new StringBuffer();
			if(deviceName.equals("null") ){
				if(devphyidMapPlace.containsKey(devphyid)){
					array[3] = devphyidMapPlace.get(devphyid);				
					for(int i = 0; i < array.length; i++){					
						stringBuffer.append(array[i]);
						stringBuffer.append(",");				
					}
					str = stringBuffer.toString();
					str = str.substring(0,str.length() - 1);

				}
				else 
					missRow++;
			}

			resultWriter.write(str);
			resultWriter.newLine();
		
			row++;
			if(row % 5000000 == 0)
				System.out.println("current row is : " + row);
		}
		
		resultWriter.close();
		dataReader.close();
		System.out.println("miss place row still is: " + missRow);


		
	}
	
	
	
	public static void main(String[] args) throws Exception{
		
		ConsumePlaceSupply placeSupply = new ConsumePlaceSupply();
		String consumeDataFile = "F:/consumeCombine2007_2015.csv";
		String consumePlaceMissing = "E:/processTemp/consumePlaceMissing.csv";
		String repairPlaceFile = "E:/processTemp/consumePlaceRepair.csv";
		String consumeRepairedFile = "E:/processTemp/consumeRepairApply.csv";
		String consumeRepaired1File = "E:/processTemp/consumeRepair1.txt";
		//根据最新全部的消费数据得到地点缺失的数据!!
		//placeSupply.extractConsumeMissingPlace(consumeDataFile, consumePlaceMissing);
		
		
		//根据之前的历史消费数据得到地点比较全的消费数据.
		String consumeptionOld = "E:/processTemp/consumptionOld.csv";
		//placeSupply.getBeforeDataFile(consumeptionOld);
		
		//根据上述步骤得到所对应的值.来补全缺失的地点所对应的设备ID
		//placeSupply.consumePlaceRepair(consumeptionOld, consumePlaceMissing, consumeRepaired1File,repairPlaceFile);
		
		//根据所得到的补全地点设备ID,，应用于整体消费数据
		placeSupply.consumePlaceRepairedApply(consumeDataFile, repairPlaceFile, consumeRepairedFile);
		
	}

}
