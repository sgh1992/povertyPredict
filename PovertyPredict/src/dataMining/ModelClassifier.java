package dataMining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;



import dataBase.FileLoad;
import analysize.CreateTable;

import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;
import weka.classifiers.trees.RandomForest;

import weka.core.Instance;
import weka.core.Instances;


public class ModelClassifier {
		
	
	private String removeLayer1 = null; //第一层分类器的模型参数.
	
	private String removeLayer2 = null; //第二层分类的模型参数.
	
	public void setLayer1Remove(String layer1Remove){
		
		this.removeLayer1 = layer1Remove;
	}
	
	public void setLayer2Remove(String layer2Remove){
		
		this.removeLayer2 = layer2Remove;
	}
	
	public ModelClassifier(){
		
		removeLayer1 = "1,15,131,16,14,31,23,29,30,18,19,17,25,220,24,94,211,41,202,95,96,97,65,200,86,87,91,92,90,88,89,61,60,59,93,49,43,51,42,50,52,58,56,57,55,53,54,47";
		removeLayer2 = "1";

		
	}
	
	/**
	 * 事先操作，将四分类有数据转换为二分类或者三分类的数据.分别对应第一层与第二层的模型数据!
	 * @throws Exception
	 */
	public void dataTransfer(String dataFile,String twoFile,String threeFile) throws Exception{
		
		ModelRun modelRun = new ModelRun();
		
		//将四分类的文件转换为二分类
		modelRun.getDataFile(dataFile, twoFile, 2);
		
		//将四分类的文件转换为三分类
		modelRun.getDataFile(dataFile, threeFile, 3);
		
	}
	
	public void evaluationOnLine(String dataFile,String layer1File,String layer2File,String testFile,String predictResultFile,String predictResultTable) throws Exception{
				
		testData(dataFile, layer1File, layer2File, testFile,predictResultFile);	
		
		//将预测的结果导入到数据库中
		CreateTable createTable = new CreateTable();
		createTable.createTableWithClassAndLoadData(predictResultFile, predictResultTable);
		
	}
	
	/**
	 * 运用dataFile进行训练模型，运用testFile作为线上测试数据来进行测试效果!!!
	 * @param dataFile
	 * @param layer1File
	 * @param layer2File
	 * @param testFile
	 * @throws Exception
	 */
	public void testData(String dataFile,String layer1File, String layer2File,String testFile,String predictResult) throws Exception{
		
		
		BufferedWriter	predictWriter = new BufferedWriter(new FileWriter(predictResult)); //将预测结果写入到文件中!!! 		
		
		ModelRun modelRun = new ModelRun();
		
		Instances data = modelRun.getInstances(dataFile); //训练集.	
		
		Instances layer1Insts = modelRun.getInstances(layer1File);
		Instances layer2Insts = modelRun.getInstances(layer2File);
		
		Instances test = getEqualHeaderInsts(data, testFile);
		test = modelRun.addSidStr(testFile, test);
		
		//预测结果文件的头标题!!!
		String errorTitle = getPredictTitle(testFile);
		predictWriter.write(errorTitle);
		predictWriter.newLine();
		

		data.setClassIndex(data.numAttributes() - 1);
		data = modelRun.removeFeature(data, "1");
		test.setClassIndex(test.numAttributes() - 1);
		layer1Insts.setClassIndex(layer1Insts.numAttributes() - 1);
		layer2Insts.setClassIndex(layer2Insts.numAttributes() - 1);
		

		
		//两层分类器所使用的数据集!!!		
		Instances trainLayer1 = modelRun.removeFeature(layer1Insts, removeLayer1);
		Instances trainLayer2 = modelRun.removeFeature(layer2Insts, removeLayer2);
		
		Instances testLayer1 = modelRun.removeFeature(test, removeLayer1);
		Instances testLayer2 = modelRun.removeFeature(test, removeLayer2);
		
		//获得训练集与测试集的头文件相同的测试集，这点非常重要！因为weka中部分算法的分类结果与分类时的的Instance的类别有关!!!
		//这里是weka中设计的一个bug!!!
		testLayer1 = modelRun.getEqualHeadData(trainLayer1, testLayer1);
		testLayer2 = modelRun.getEqualHeadData(trainLayer2, testLayer2);
				
		Logistic logistic = new Logistic(); //layer1 
		RandomForest randomForest = new RandomForest(); //layer2
		randomForest.setNumTrees(100);
		
		TwoLayerClassifier twoLayerClassifier = getTrainClassifier(data, trainLayer1, trainLayer2, logistic, randomForest);

		
		for(int i = 0; i < test.numInstances(); i++){
			
			Instance layer1Inst = testLayer1.instance(i);
			
			Instance layer2Inst = testLayer2.instance(i);
			
			int predictValue = (int)twoLayerClassifier.classifyInstance(layer1Inst,layer2Inst);
			
			Instance actualIns = test.instance(i);
			String valueStr = modelRun.getInstValue(actualIns);
			String predictClass = test.classAttribute().value(predictValue);
			valueStr = valueStr + "," + predictClass;
			predictWriter.write(valueStr);
			predictWriter.newLine();
			
			
		}	
		predictWriter.close();
	}
	
	public TwoLayerClassifier getTrainClassifier(Instances data,Instances layer1Insts, Instances layer2Insts,Classifier layer1Classifier, Classifier layer2Classifier) throws Exception{
		
		TwoLayerClassifier twoLayerClassifier = new TwoLayerClassifier(layer1Classifier, layer2Classifier, layer1Insts, layer2Insts);
		
		twoLayerClassifier.buildClassifier(data);
		
		return twoLayerClassifier;
		
		
	}
	
	/**
	 * 将学生的数据
	 * @param trainData
	 * @param testFile 要确保 这个测试数据集与训练数据集必须确保头文件保持一致!!!
	 * @return
	 */
	public Instances getEqualHeaderInsts(Instances trainData,String testFile) throws Exception{
		
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(testFile), "utf-8"));
				
		Instances testData = new Instances(trainData,0);
		
		String str = null;
		int numAttributes = trainData.numAttributes();
		
		dataReader.readLine();

		while((str = dataReader.readLine()) != null){
			

			Instance inst = new Instance(numAttributes);
			inst.setDataset(trainData);
			
			String[] arrays = str.split(",", -1);
			for(int j = 0; j < numAttributes; j++){
				
				if(j == 0){
					inst.setMissing(0);
					continue;
				}
				String valueStr = arrays[j];
				if(trainData.attribute(j).isNumeric()){
					
					double value = Double.parseDouble(valueStr);
					inst.setValue(j, value);					
				}
				else 
					inst.setValue(j, valueStr);				
			}
			
			testData.add(inst);			
		}		
		dataReader.close();		
		return testData;		
	}	
	/**
	 * 预测结果的导入
	 * @param tableName
	 * @param errorFile
	 * @throws Exception
	 */
	public void predictResultLoad(String tableName,String errorFile) throws Exception{
		
		CreateTable createTable = new CreateTable();
		createTable.createTableWithClassType(errorFile, tableName);
		
		FileLoad fileLoad = new FileLoad();
		fileLoad.LoadTemplateFull(errorFile, tableName, ",");		
	}
	

	public String getPredictTitle(String dataFile) throws Exception{
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));		
		String title = dataReader.readLine();

		dataReader.close();
		return title;
		
		
	}

}
