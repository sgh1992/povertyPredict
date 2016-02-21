package process;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class ConsumeRecordSort {
	
	private int smallFileMaxRows = 1000000; //每一个文件的大小.
	
	private int origialTotalRows = 265689888; //总的文件的大小.
	
	private int fileNums = origialTotalRows/smallFileMaxRows; //总的文件的个数.即将这个大的文件分割成若干个小文件.
	
	public void consumeRecordSort(String dataFile,String resultFile,String path,Comparator<SidAndConsumeTimeRecord> comparator,Comparator<ConsumeRecord> consumeComparator) throws Exception{
		
		System.out.println("start sort....");
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
        String str = null;
        
        //dataReader.readLine();
        
        List<BufferedWriter> fileWriterList = new ArrayList<>();
        List<String> fileList = new ArrayList<>();
        
        BufferedWriter writer = null;
        
        for(int i = 0; i < fileNums; i++){        	
        	
        	String fileName = path + i + ".csv";
        	
        	fileList.add(fileName);
        	writer =  new BufferedWriter(new FileWriter(fileName)); 
        	fileWriterList.add(writer);
        	        	        	
        }
        
        //将大文件中的记录写入到几个小文件中.
        int row = 0;
        SidAndConsumeTimeRecord record = null;
        List<SidAndConsumeTimeRecord> recordList = new ArrayList<>();
        String title = dataReader.readLine();
        resultWriter.write(title);
        resultWriter.newLine();
        
        while((str = dataReader.readLine()) != null){
        	
        	String[] arrays = str.split(",",-1);
        	String sid = arrays[0];
        	String transName = arrays[1];
        	String deviceName = arrays[2];
        	String devphyId = arrays[3];
        	String time = arrays[4];
        	String amount = arrays[5];
        	String cardbal = arrays[6];
        	String type = arrays[7];
        	String month = arrays[8];
        	        	
        	record = new SidAndConsumeTimeRecord(sid, transName, deviceName, devphyId, time, amount, cardbal,type, month);
        	recordList.add(record);
        	row++;
        	        	
        	if(row % smallFileMaxRows == 0){
        		        		
        		int fileIndex = row / smallFileMaxRows - 1;
        		writer = fileWriterList.get(fileIndex);
        		
        		Collections.sort(recordList, comparator);
        		for(SidAndConsumeTimeRecord temp : recordList){
        			
        			String string = temp.sid + "," + temp.transName + "," + temp.deviceName + "," + temp.devphyid + "," + temp.transTime + "," + temp.amount + "," + temp.cardbal + "," + temp.type + "," +  temp.month;
        			writer.write(string);
        			writer.newLine();
        			       			
        		}
        		writer.close();
        		recordList.clear();       		              		
        	}        	
        	
        	if(row % 5000000 == 0)
        		System.out.println("current row is: " + row);
        }
        
        if(recordList.size() != 0){
        	
        	Collections.sort(recordList,comparator);
        	writer = fileWriterList.get(fileNums - 1);
        	
        	for(SidAndConsumeTimeRecord temp : recordList){        		
    			String string = temp.sid + "," + temp.transName + "," + temp.deviceName + "," + temp.devphyid + "," + temp.transTime + "," + temp.amount + "," + temp.cardbal + "," + temp.type + "," +  temp.month;
    			writer.write(string);
    			writer.newLine();        		
        	}
        	
        	writer.close();
        	
        	
        }
                
        //调用归并排序的方法.
        mergeSort(fileList, resultWriter, consumeComparator);
        
        dataReader.close();
        resultWriter.close();
               		
	}
	
	
	/**
	 * 
	 * @param fileList
	 * @param resultFile
	 * @param comparator Collections.sort()的方法实现.
	 * @throws Exception
	 */
	public void mergeSort(List<String> fileList,BufferedWriter resultWriter,Comparator<ConsumeRecord> comparator) throws Exception{
		
		List<BufferedReader> readerList = new ArrayList<>();
		
		//BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile));
		
		BufferedReader temp = null;
		
		for(String fileName : fileList){
			
			temp = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "utf-8"));			
			readerList.add(temp);
						
		}
		
		ConsumeRecord consumeRecord = null;
		List<ConsumeRecord> consumeRecordList = new ArrayList<>();
		//归并排序的思想：将各个已经排好序的文件，按照归并排序的思想,依次比较各个文件记录的大小，将最大者取出来，并将之存入到结果文件中.
		for(int i = 0; i < readerList.size(); i++){
			
			temp = readerList.get(i);
						
			String str = null;
			
			if((str = temp.readLine()) == null){ //为了防止读取空文件.
				temp.close();
				continue;
			}
			
			String[] arrays = str.split(",", -1);
        	String sid = arrays[0];
        	String transName = arrays[1];
        	String deviceName = arrays[2];
        	String devphyId = arrays[3];
        	String time = arrays[4];
        	String amount = arrays[5];
        	if(amount.startsWith("."))
        		amount = "0" + amount;
        	String cardbal = arrays[6];
        	String type = arrays[7];
        	String month = arrays[8];
        	int index = i;
        	
        	consumeRecord = new ConsumeRecord(sid, transName, deviceName, devphyId, time, amount, cardbal, type,month, index);
        	consumeRecordList.add(consumeRecord);
        				
		}
		
		
		
		while(true){
			
			Collections.sort(consumeRecordList, comparator);
			ConsumeRecord firstRecord = consumeRecordList.get(0);
			int firstindex = firstRecord.index; //属于哪个文件.
			
			//注意这里需要根据你的需求来进行变化.
			String string =  firstRecord.sid + "," + firstRecord.transName + "," + firstRecord.deviceName + "," + firstRecord.devphyid + "," + firstRecord.transTime + "," + firstRecord.amount + "," + firstRecord.cardbal + "," + firstRecord.type + "," + firstRecord.month;
			resultWriter.write(string);
			resultWriter.newLine();			
			consumeRecordList.remove(0);
			
			if((string = readerList.get(firstindex).readLine()) != null){
				
				String[] arrays = string.split(",", -1);

	        	String sid = arrays[0];
	        	String transName = arrays[1];
	        	String deviceName = arrays[2];
	        	String devphyId = arrays[3];
	        	String time = arrays[4];
	        	String amount = arrays[5];
	        	
	        	if(amount.startsWith("."))
	        		amount = "0" + amount;
	        	
	        	String cardbal = arrays[6];
	        	String type = arrays[7];
	        	String month = arrays[8];
	        	int index = firstindex;
	        	
	        	consumeRecord = new ConsumeRecord(sid, transName, deviceName, devphyId, time, amount, cardbal, type,month, index);
	        	consumeRecordList.add(consumeRecord);	
	        	
			}
			
			else {				
				readerList.get(firstindex).close();				
			}
			
			if(consumeRecordList.size() == 0)
				break;			
			
			
		}
		
		resultWriter.close();
			
	}

}
