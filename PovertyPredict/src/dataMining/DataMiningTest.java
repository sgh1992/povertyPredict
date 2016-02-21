package dataMining;

import java.io.FileInputStream;
import java.util.Properties;

public class DataMiningTest {
	
	private String trainModelFile = null;
	
	private String predictFile = null;
	
	private String predictResultFile = null;
	
	private String predictResultTable = null;
	
	private String trainModelLayer1File = null;
	
	private String trainModelLayer2File = null;
	
	public void setTrainModelLayer1File(String trainModelLayer1File){
		
		this.trainModelLayer1File = trainModelLayer1File;
	}
	
	public void setTrainModelLayer2File(String trainModelLayer2File){
		
		this.trainModelLayer2File = trainModelLayer2File;
	}
	
	public void setTrainModelFile(String trainModelFile){
		
		this.trainModelFile = trainModelFile;
	}
	
	public void setPredictFile(String predictFile){
		
		this.predictFile = predictFile;
	}
	
	public void setPredictResultFile(String predictResultFile){
		
		this.predictResultFile = predictResultFile;
	}
	
	public void setPredictResultTable(String predictResultTable){
		
		this.predictResultTable = predictResultTable;
	}
	

	
	public void dataMiningTest() throws Exception{
		
		Properties properties = new Properties();
		FileInputStream file = new FileInputStream("dataMining.properties");
		properties.load(file);
		
		setTrainModelFile(properties.getProperty("trainModelFile"));
		setTrainModelLayer1File(properties.getProperty("trainModelTwoClass"));
		setTrainModelLayer2File(properties.getProperty("trainModelThreeClass"));
		
		setPredictFile(properties.getProperty("predictFile"));
		setPredictResultFile(properties.getProperty("predictResultFile"));
		setPredictResultTable(properties.getProperty("predictResultTable"));
		
		//1.训练模型，并用来对未知学生进行预测!!!
		// 并将结果文件载入到数据库中.
		ModelClassifier modelClassifier = new ModelClassifier();
		modelClassifier.evaluationOnLine(trainModelFile, trainModelLayer1File, trainModelLayer2File, predictFile, predictResultFile, predictResultTable);
		
	}
	
	public static void main(String[] args) throws Exception{
		
		DataMiningTest dataMining = new DataMiningTest();
		dataMining.dataMiningTest();
	}
	

}
