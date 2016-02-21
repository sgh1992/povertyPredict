package reader;

import java.io.FileInputStream;
import java.util.Properties;




/**
 * 数据读取功能.
 * @author Administrator
 *
 */
public class ReaderTest{
	
	
	public static void main(String[] args) throws Exception{
		
		ReaderTest readerTest = new ReaderTest();
		readerTest.readerTest();
		
	}
	
	public void readerTest() throws Exception{
		
		Properties properties = new Properties();
		FileInputStream file = new FileInputStream("dataMining.properties");		
		properties.load(file);
		
		String consumeTableName = properties.getProperty("consume_tableName");
		String consumeLocalFile = properties.getProperty("consume_localFile");
		
		//读取数据库文件.
		Reader reader = new Reader();
		reader.setTableName(consumeTableName);
		reader.setConsumeLocal(consumeLocalFile);
		
		reader.dataReader();
	
	}
	
	
	

	
	
	

}
