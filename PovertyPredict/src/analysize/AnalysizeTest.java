package analysize;

import java.io.FileInputStream;
import java.util.Properties;

public class AnalysizeTest {
	
	private String consumeFile = null;
	private String consumeFeatureData = null;
	private String consumeFeatureDataAndClass = null;
	private String consumeFeatureTable = null;
	private String trainModelFeatureSelection = null;
	private String trainModelTwoClassFeatureSelection = null;
	private String trainModelThreeClassFeatureSelection = null;
	private String predictModelFeatureSelection = null;
	private String featureSelectionTableName = null;
	private String trainModelTempPath = null;
	
	public void setPredictModelFeatureSelection(String predictModelFeatureSelection){
		
		this.predictModelFeatureSelection = predictModelFeatureSelection;
	}
	
	public void setTrainModelTempPath(String trainModelTempPath){
		
		this.trainModelTempPath = trainModelTempPath;
	}
	
	public void setTrainModelFeatureSelection(String trainModelFeatureSelection){
		
		this.trainModelFeatureSelection = trainModelFeatureSelection;
	}
	
	public void setTrainModelTwoClassFeatureSeletion(String trainModelTwoClassFeatureSelection){
		
		this.trainModelTwoClassFeatureSelection = trainModelTwoClassFeatureSelection;
		
	}
	public void setTrainModelThreeClassFeatureSelection(String trainModelThreeClassFeatureSelection){
		
		this.trainModelThreeClassFeatureSelection = trainModelThreeClassFeatureSelection;	
	}
	
	public void setFeatureSelectionTableName(String featureSelectionTableName){
		
		this.featureSelectionTableName = featureSelectionTableName;		
	}
	
	public void setConsumeFile(String consumeFile){
		
		this.consumeFile = consumeFile;
	}
	
	public void setConsumeFeatureData(String consumeFeatureData){
		
		this.consumeFeatureData = consumeFeatureData;
	}
	
	public void setConsumeFeatureDataAndClass(String consumeFeatureDataAndClass){
		
		this.consumeFeatureDataAndClass = consumeFeatureDataAndClass;
	}
	
	public void setConsumeFeatureTable(String consumeFeatureTable){
		
		this.consumeFeatureTable = consumeFeatureTable;
	}
	
	public static void main(String[] args) throws Exception{
		
		AnalysizeTest analysize = new AnalysizeTest();
		analysize.analysizeTest();
		
	}
	
	public void analysizeTest() throws Exception{
		
		
		Properties properties = new Properties();
		FileInputStream file = new FileInputStream("dataMining.properties");
		properties.load(file);
				
		setConsumeFile(properties.getProperty("consume_StudentDataMonthSortedRemoveDuplicate"));
		setConsumeFeatureData(properties.getProperty("consume_featureData"));
		setConsumeFeatureDataAndClass(properties.getProperty("consume_featureDataAndClass"));
		setConsumeFeatureTable(properties.getProperty("consume_featureDataAndClassTableName"));
		
		setTrainModelFeatureSelection(properties.getProperty("trainModelFile"));
		setTrainModelTwoClassFeatureSeletion(properties.getProperty("trainModelTwoClass"));
		setTrainModelThreeClassFeatureSelection(properties.getProperty("trainModelThreeClass"));
		setTrainModelTempPath(properties.getProperty("trainModelTempPath"));	
		setPredictModelFeatureSelection(properties.getProperty("predictFile"));
		
		setFeatureSelectionTableName(properties.getProperty("featureSelectionTable"));
		
		//1.特征抽取.
		MonthAnalysize monthAnalysize = new MonthAnalysize();
		monthAnalysize.consumePlaceAnalysize(consumeFile, consumeFeatureData);
		
		//2.将具有类别信息的添加到特征数据中.
		DataDivide dataDivide = new DataDivide();
		dataDivide.getModelData(consumeFeatureData, consumeFeatureDataAndClass);
//		
//		//3.将特征数据导入到数据库中.
		CreateTable createTable = new CreateTable();
		createTable.createTableWithClassAndLoadData(consumeFeatureDataAndClass, consumeFeatureTable);
		
		//特征工程进行下一步的加工.
		
		//4.获得训练模型数据集.
		FeatureSelection featureSelection = new FeatureSelection();
		featureSelection.trainModelFile(consumeFeatureTable, trainModelFeatureSelection, trainModelTempPath);
		
		//5.训练模型中的两层训练集.
		featureSelection.getTwoLayerData(trainModelFeatureSelection, trainModelTwoClassFeatureSelection, trainModelThreeClassFeatureSelection);
		
		//6.生成用于预测的所有学生的经过特征选择的数据集.
		featureSelection.getPredictData(consumeFeatureTable, predictModelFeatureSelection);
		
		//7.将经过特征选择之后的数据载入到数据库中.
		createTable.createTableWithClassAndLoadData(predictModelFeatureSelection, featureSelectionTableName);					
	}
}
