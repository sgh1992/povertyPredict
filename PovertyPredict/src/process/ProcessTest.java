package process;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * 数据预处理.
 * @author Administrator
 *
 */
public class ProcessTest {
	
	private String consumeFile = null;
	
	private String consumeAddType = null;
	
	private String consumeStudentDataAddMonth = null;
	
	private String consumeStudentDataMonthSorted = null;
	
	private String consumeStudentDataMonthSortedRemoveDuplicate = null;
	
	private String sortTempPath = null;
	
	public void setSortTempPath(String sortTempPath){
		
		this.sortTempPath = sortTempPath;
	}
	
	public void setConsumeLocalFile(String consumeLocalFile){
		
		this.consumeFile = consumeLocalFile;
		
	}
	
	public void setConsumeAddType(String consumeAddType){
		
		this.consumeAddType = consumeAddType;
	}
	
	public void setConsumeStudentDataAddMonth(String consumeStudentDataAddMonth){
		
		this.consumeStudentDataAddMonth = consumeStudentDataAddMonth;
	}
	
	public void setConsumeStudentDataMonthSorted(String consumeStudentDataMonthSorted){
		
		this.consumeStudentDataMonthSorted = consumeStudentDataMonthSorted;
	}
	
	public void setConsumeStudentDataMonthSortedRemoveDuplicate(String consumeStudentDataMonthSortedRemoveDuplicate){
		
		this.consumeStudentDataMonthSortedRemoveDuplicate = consumeStudentDataMonthSortedRemoveDuplicate;
		
	}
	
	
	public void processTest() throws Exception{
				
		Properties properties = new Properties();
		FileInputStream file = new FileInputStream("dataMining.properties");
		properties.load(file);
		
		//配置文件设置.
		setConsumeLocalFile(properties.getProperty("consume_localFile"));
		setConsumeAddType(properties.getProperty("consume_addType"));		
		setConsumeStudentDataAddMonth(properties.getProperty("consume_studentDataMonth"));
		setConsumeStudentDataMonthSorted(properties.getProperty("consume_StudentDataMonthSorted"));
		setConsumeStudentDataMonthSortedRemoveDuplicate(properties.getProperty("consume_StudentDataMonthSortedRemoveDuplicate"));
		setSortTempPath(properties.getProperty("consume_StudentsortedTempPath"));
		
		//1.消费类型字段添加.
		ConsumeAddConsumeType addType = new ConsumeAddConsumeType();				
		addType.consumeAddConsumeType(consumeAddType, consumeAddType);
		
		//2.抽取学生消费数据,并将消费月份字段添加
		ConsumeMonth consumeMonth = new ConsumeMonth();
		consumeMonth.consumeDataAddMonth(consumeAddType, consumeStudentDataAddMonth);
		
		//3.排序.
		ConsumeRecordSort sort = new ConsumeRecordSort();
		sort.consumeRecordSort(consumeStudentDataAddMonth, consumeStudentDataMonthSorted, sortTempPath, new SidConsumeTimeSortPattern(), new ConsumeSortSidPattern());
		
		//4.去重
		ConsumeDuplicate duplicate = new ConsumeDuplicate();
		duplicate.dataRemoveDuplicate(consumeStudentDataMonthSorted, consumeStudentDataMonthSortedRemoveDuplicate);
						
	}
	
	/**
	 * 测试.
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		ProcessTest processTest = new ProcessTest();
		processTest.processTest();
		
	}
	

}
