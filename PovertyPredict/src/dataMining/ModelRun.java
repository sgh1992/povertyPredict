package dataMining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


/**
 * 模型的运行
 * @author Administrator
 *
 */
public class ModelRun {
	
	
	private int m_NumFolds = 10; //默认设置为十折交叉验证!
	
	private int m_NumModels = 3; // 这个模型的个数与两层分类与仅仅只是针对某一层分类没有什么差别!
	
	private Classifier[][] m_Classifiers = new Classifier[m_NumModels][2]; //默认为3个模型.每个模型有两层分类器.
	
	private String[][] m_RemoveStr = new String[m_NumModels][2]; //每个模型都有第一层与第二层分类器所需要的数据!
	
	private String[] m_singleRemoveStr = new String[m_NumModels]; //测试单一的模型时所要用到的数据!比如针对贫困生与非贫困生，贫困生的三个类别时的数据.
	
	private Classifier[] m_singleClassifiers = new Classifier[m_NumModels]; //交叉验证单一模型时所要用到的分类器!
	
	private Instances m_Data = null;
	
	private int m_NumClasses;
	
	//初始化二层模型!!!
	public void setNumModels(int numModels){		
		m_NumModels = numModels;	
		m_Classifiers = new Classifier[m_NumModels][2];
		m_RemoveStr = new String[m_NumModels][2];
	}
	
	//初始化单一模型所涉及到的数据!
	public void setSingleNumModels(int numModels){	
		
		m_NumModels = numModels;
		
		m_singleClassifiers = new Classifier[m_NumModels];
		
		m_singleRemoveStr = new String[m_NumModels];		
	}
	
	public void setNumClasses(int numClasses){
		
		m_NumClasses = numClasses;
	}
	
	public void setData(Instances data){
		
		m_Data = data;
	}
			
	public void setModelInfo(int index,Classifier[] classifiers,String[] removeStr){
		
		m_RemoveStr[index] = removeStr;
		
		m_Classifiers[index] = classifiers;
		
		m_RemoveStr[index] = removeStr;
		
	}
	
	/**
	 * 单一模型的相关信息的使用!!!
	 * @param index
	 * @param classifier
	 * @param removeStr
	 */
	public void setSingleModelInfo(int index,Classifier classifier,String removeStr){
		
		m_singleClassifiers[index] = classifier;
		m_singleRemoveStr[index] = removeStr;		
	}
	
	/**
	 * 
	 * @param numModels
	 * @param twoLayerFalg 根据这个标志位，判断是否是初始化二层模型，还是中针对单一的模型进行测试!!!
	 */
	public ModelRun(int numModels,boolean twoLayerFalg){
		
		if(twoLayerFalg)
			setNumModels(numModels);
		else 
			setSingleNumModels(numModels);
	}
	
	public ModelRun(){
		
	}
		
	public Instances getInstances(String dataFile) throws Exception{
		
		DataSource source = new DataSource(dataFile);
		Instances data = source.getDataSet();
		return data;
		
	}	
	/**
	 * 返回含有两个类别的数据集!
	 * normal and poverty!
	 * @param dataFile
	 * @return
	 * @throws Exception
	 */
	public Instances getTwoClassInstances(Instances data) throws Exception{

		int numAttributes = data.numAttributes();
		
		FastVector fastVector = new FastVector();
		fastVector.addElement("normal");
		fastVector.addElement("poverty");		
		Attribute attribute = new Attribute("class", fastVector);
		
		Instances twoClassInstances = new Instances(data);
		twoClassInstances.setClassIndex(-1); //这里类别的设置必须先设置为-1，才能删除类别属性!
		twoClassInstances.deleteAttributeAt(twoClassInstances.numAttributes() - 1);
		twoClassInstances.insertAttributeAt(attribute, twoClassInstances.numAttributes());
		
		for(int i = 0; i < data.numInstances(); i++){
			
			Instance temp = data.instance(i);
			int valueInd = (int)temp.value(numAttributes - 1);
			String classType = temp.attribute(numAttributes - 1).value(valueInd);
			
			String classValue = "normal";
			if(!classType.equals("normal"))
				classValue = "poverty";
			
			twoClassInstances.instance(i).setValue(numAttributes - 1,classValue);			
		}
		
		twoClassInstances.setClassIndex(twoClassInstances.numAttributes() - 1);
		return twoClassInstances;
		
	}
	
	/**
	 * 用于得到第二层分类器的训练集.总共有3个类别,general povery,poorer,very poverty
	 * @param dataFile
	 * @return
	 * @throws Exception
	 */
	public Instances getThreeClassInstances(Instances data) throws Exception{
		
		FastVector fastVector = new FastVector(3);
		fastVector.addElement("general povery");
		fastVector.addElement("very poverty");
		fastVector.addElement("poorer");		
		Attribute attribute = new Attribute("class1", fastVector);
		
		Instances newData = new Instances(data);//中间过渡 		
		newData.insertAttributeAt(attribute, newData.numAttributes());	
		
		newData.setClassIndex(-1);
		Instances resultData = new Instances(newData, 0);

		int numAttributes = newData.numAttributes();
		
		for(int i = 0; i < newData.numInstances(); i++){
			
			Instance temp = newData.instance(i);			
			int classValue = (int)temp.value(numAttributes - 2);
			String classType = newData.attribute(numAttributes-2).value(classValue);
			if(!classType.equals("normal")){
				temp.setValue(numAttributes - 1, classType);
				resultData.add(temp); //这个行为是非常危险的，如果两者的头标题不一致的话，可能导致错误，这个错误非常的隐藏!!!
			}
		}
		
		resultData.deleteAttributeAt(numAttributes - 2);
		
		resultData.setClassIndex(resultData.numAttributes() - 1);
		return resultData;
	}
	
	/**
	 * 将Instances中的数据集写入到文件中，并以csv格式保存.
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void getThreeDataFile(String dataFile,String resultFile) throws Exception{
		
		CSVLoader csvLoader = new CSVLoader();
		csvLoader.setSource(new File(dataFile));
		csvLoader.setStringAttributes("1");		
		Instances data = csvLoader.getDataSet();
		
		CSVSaver csvSaver = new CSVSaver();
		csvSaver.setInstances(data);
		csvSaver.setFile(new File(resultFile));
		csvSaver.writeBatch();
				
	}
	
	/**
	 * 根据实际需要获得两类或者三类的学生数据.
	 * 即分别对应的是第一层的数据与第二层的数据!
	 * @param dataFile
	 * @param resultFile
	 * @param classNum 如果classNum = 4，则说明是在general poverty,poorer,very poverty 中再进行二分类即 very poverty + poorer,general poverty一类.
	 * @throws Exception
	 */
	public void getDataFile(String dataFile,String resultFile,int classNum) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		String title = dataReader.readLine();
		resultWriter.write(title);
		resultWriter.newLine();
		
		while((str = dataReader.readLine()) != null){
			
			String[] arrays = str.split(",", -1);
			String classType = arrays[arrays.length - 1];
			if(classNum == 2){
				
				if(!classType.equals("normal"))
					classType = "poverty";				
			}
			else if(classNum == 3){
				
				if(classType.equals("normal"))
					continue;
				
			}
			else if(classNum == 4){
				
				if(!classType.equals("very poverty"))
					classType = "generalAndPoorer";
				
			}
			StringBuffer stringBuffer = new StringBuffer();
			for(int i = 0; i < arrays.length - 1 ; i++){
				
				stringBuffer.append(arrays[i]);
				stringBuffer.append(",");
			}
			stringBuffer.append(classType);
			resultWriter.write(stringBuffer.toString());
			resultWriter.newLine();
			
		}
		
		resultWriter.close();
		dataReader.close();
		
	}
	

	
	
	/**
	 * 将四类的数据保存成二类的数据，即正常与非正常!!!
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void getTwoDataFile(String dataFile,String resultFile) throws Exception{
		
		CSVLoader csvLoader = new CSVLoader();
		csvLoader.setSource(new File(dataFile));
		csvLoader.setStringAttributes("1");		
		Instances data = csvLoader.getDataSet();
		
		CSVSaver csvSaver = new CSVSaver();
		csvSaver.setInstances(data);
		csvSaver.setFile(new File(resultFile));
		csvSaver.writeBatch();
		
		
	}
	
	/**
	 * 十交叉验证测试模型的性能!
	 * @param dataFile
	 * dataFile指的是全部的特征体系，包括第一层与第二层的特征体系.
	 * 两层特征体系在训练上是相同的
	 * @throws Exception
	 */
	public void modelCrossValidation(String dataFile) throws Exception{
		
		Instances data = getInstances(dataFile);
	
		data.setClassIndex(data.numAttributes() - 1);
		
		m_Data = new Instances(data, 0);//仅仅只是需要这个数据集的头文件!
		
		m_NumClasses = m_Data.numClasses();
		
		int numClasses = data.numClasses();
		
		int[][][] confuseMartix = new int[m_NumModels][numClasses][numClasses];
		
		Random random = new Random(16);
		
		//数据需要事先按各个类别的比例进行事先分层.这样对后续的交叉验证才有意义,这点对目标为离散型的数据特别重要.
		data.stratify(m_NumFolds);		
		for(int numfold = 0; numfold < m_NumFolds; numfold++){
			
			Instances origialTrain = data.trainCV(m_NumFolds, numfold,random);			
			Instances origialTest = data.testCV(m_NumFolds, numfold); //整个数据集是适用于所有层的数据!
			                                                         	
			Instances origialLayer1Train = getTwoClassInstances(origialTrain);
			Instances origialLayer2Train = getThreeClassInstances(origialTrain);
						
			for(int m = 0; m < m_NumModels; m++){
				
				int[][] martix = confuseMartix[m];//混淆矩阵!
				String[] removeStr = m_RemoveStr[m];
				
				//分类时需要用到两层分类.第一层是二分类，第二层是三分类.其中removeStr已经是考虑了不同层级之间的差异!!!
				Instances layer1Train = removeFeature(origialLayer1Train, removeStr[0]);
				Instances layer2Train = removeFeature(origialLayer2Train, removeStr[1]);
				
				Instances layer1Test = removeFeature(origialTest, removeStr[0]);
				Instances layer2Test = removeFeature(origialTest, removeStr[1]);
				
				//确保训练集与测试集的头文件保持一致.
				layer1Test = getEqualHeadData(layer1Train, layer1Test);
				layer2Test = getEqualHeadData(layer2Train, layer2Test);
								
				Classifier layer1Classifier = Classifier.makeCopy(m_Classifiers[m][0]);
				Classifier layer2Classifier = Classifier.makeCopy(m_Classifiers[m][1]);
				
				TwoLayerClassifier twoLayerClassifier = 
						new TwoLayerClassifier(layer1Classifier, layer2Classifier, layer1Train, layer2Train);
				
				twoLayerClassifier.buildClassifier(origialTest); //buildClassifier中的origialtest是没有多大意义的.
				
				for(int i = 0; i < origialTest.numInstances(); i++){
					
					Instance inst1 = layer1Test.instance(i);
					Instance inst2 = layer2Test.instance(i);
					int actualValue = (int)origialTest.instance(i).classValue();
					int predictValue = (int)twoLayerClassifier.classifyInstance(inst1,inst2);					
					martix[actualValue][predictValue] += 1;			
					
				}				
			}			
		}
		
		System.out.println(evaluation(confuseMartix).toString());
		
		
	
	}
	
	/**
	 *根据训练集的头文件，确保测试数据集的头文件保持一致.
	 *主要是为了避免分类数据时，由于测试数据与训练数据的头文件不一致，而导致错误!!!
	 *这个错误如果发生，则非常隐蔽!!!
	 * @param trainData 
	 * @param testData
	 * @return
	 */
	public Instances getEqualHeadData(Instances trainData,Instances testData){
		
		Instances newData = new Instances(testData);
		newData.setClassIndex(-1);
		newData.deleteAttributeAt(newData.numAttributes() - 1);
		
		Attribute classAttribute = trainData.classAttribute();
		newData.insertAttributeAt(classAttribute, newData.numAttributes());
		
		newData.setClassIndex(newData.numAttributes() - 1);
		
		return newData;
		
	}
	
	/**
	 * 由于weka本身的原因,它将大数字一律都以科学记数法来表示，然而学号必须为string类型，才能用来跟踪错误分类的学生.
	 * @param dataFile
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Instances addSidStr(String dataFile,Instances data) throws Exception{
				
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		String str = null;
		Instances newData = new Instances(data);
		newData.deleteAttributeAt(0);
		
		FastVector fastVector = null;
		Attribute attribute = new Attribute("sid",fastVector);
		newData.insertAttributeAt(attribute, 0);
		int row = 0;
		dataReader.readLine();
		while((str = dataReader.readLine()) != null){
			
			String[] array = str.split(",", -1);
			String sid = array[0];
			Instance inst = newData.instance(row);
			inst.setValue(0, sid);	
			row++;
		}
		
		dataReader.close();
		
		return newData;
		
		
	}
	
	public String getErrorTitle(String dataFile) throws Exception{
		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));		
		String title = dataReader.readLine();
		
		title = title + ",predictclass";
		dataReader.close();
		return title;
		
		
	}
	
	/**
	 * 针对单一的模型进行交叉验证，然后再进行模型选择!!!
	 * @param dataFile
	 * @throws Exception
	 */
	public void singleModelEvaluation(String dataFile,String errorResultFile) throws Exception{
		
		//错误预测的结果写入!!!用于后续的分析使用
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(errorResultFile)); 		
		String errorTitle = getErrorTitle(dataFile);
		resultWriter.write(errorTitle);
		resultWriter.newLine();
		
		Instances data = getInstances(dataFile);
		
		data = addSidStr(dataFile, data);//学号字段字符串化!!!
		
		data.setClassIndex(data.numAttributes() - 1);
		
		int numClasses = data.numClasses();
		
		m_Data = new Instances(data, 0);
		
		m_NumClasses = m_Data.numClasses();
		
		int[][][] confuseMartix = new int[m_NumModels][numClasses][numClasses];
		
		Random random = new Random(16);
		
		//数据需要事先按各个类别的比例进行事先分层.这样对后续的交叉验证才有意义,这点对目标为离散型的数据特别重要.
		data.stratify(m_NumFolds);		
		for(int numfold = 0; numfold < m_NumFolds; numfold++){			
			Instances origialTrain = data.trainCV(m_NumFolds, numfold,random);			
			Instances origialTest = data.testCV(m_NumFolds, numfold); //整个数据集是适用于所有层的数据!
						
			for(int m = 0; m < m_NumModels; m++){
				
				int[][] martix = confuseMartix[m];//混淆矩阵!
								
				Instances train = removeFeature(origialTrain, m_singleRemoveStr[m]);
				Instances test = removeFeature(origialTest, m_singleRemoveStr[m]);
				
				//确保训练集与测试集的头文件保持一致!!! 单独的分类时，这里并不需要更改测试集的头文件.
				//Instances testEqualHead = getEqualHeadData(train, test); 
				
				Classifier classifier = Classifier.makeCopy(m_singleClassifiers[m]);
				classifier.buildClassifier(train);
				
				for(int i = 0; i < test.numInstances(); i++){
					
					Instance inst = test.instance(i);					
					int actualValue = (int)inst.classValue();
					int predictValue = (int)classifier.classifyInstance(inst);					
					martix[actualValue][predictValue] += 1; //执行这步要确保训练集与测试集的头文件要保持一致!!!
					
					//预测错误结果反查!! 这里只针对第一个模型的预测结果来反查!!!
					if(actualValue == predictValue && m == m_NumModels - 2){
						
						Instance origialInst = origialTest.instance(i);
						String predictClassName = origialTest.classAttribute().value(predictValue);
						String string = getInstValue(origialInst);
						//string = string + "," + predictClassName;
						resultWriter.write(string);
						resultWriter.newLine();						
					}
				}				
			}			
		}		
		System.out.println(evaluation(confuseMartix).toString());	
		resultWriter.close();
	}
	
	/**
	 * 获得每行的值!但不包括类别信息.
	 * @param inst
	 * @return
	 */
	public String getInstValue(Instance inst){
		
		StringBuffer stringBuffer = new StringBuffer();
		for(int i = 0; i < inst.numAttributes() - 1; i++){
			
			String value = null;
			if(!inst.attribute(i).isNumeric())
				value = inst.stringValue(i);
			else 
				value = inst.value(i) + "";
			stringBuffer.append(value);
			stringBuffer.append(",");
		}
		
		String string = stringBuffer.toString();
		string = string.substring(0,string.length() - 1);
		return string;
	}
	
	/**
	 * 对各个模型的结果进行评估.
	 * @param confuseMatrix
	 * @return
	 */
	public StringBuffer evaluation(int[][][] confuseMatrix){
		
		StringBuffer resultBuffer = new StringBuffer();
	
		for(int m = 0; m < m_NumModels; m++){
			
			int[][] matrix = confuseMatrix[m];
			String modelTitle = "===Model " + m + " ===";
			resultBuffer.append(modelTitle);
			resultBuffer.append("\n");
			
			String summary = evaluationMatrix(matrix).toString();
			resultBuffer.append(summary);
			
		}
		
		return resultBuffer;
		
	}
	
	/**
	 * 对某一个混淆矩阵进行评估!!!
	 * @param matrix
	 * @return
	 */
	public StringBuffer evaluationMatrix(int[][] matrix){
		
		String detailsSummary = summaryAndDetails(matrix).toString();
		String confuseMatrixSummary = confuseMatrixSummary(matrix).toString();
		
		StringBuffer resultBuffer = new StringBuffer();
		resultBuffer.append(detailsSummary);
		resultBuffer.append("\n");
		resultBuffer.append(confuseMatrixSummary);
		resultBuffer.append("\n\n");
		
		return resultBuffer;
		
		
	}
	
	/**
	 * 返回这个模型的总的准确率与各个类别的分类结果的详细值!
	 * @param confuseMatrix
	 * @return
	 */
	public StringBuffer summaryAndDetails(int[][] confuseMatrix){
		
		StringBuffer stringBuffer = new StringBuffer();
		
		DecimalFormat df = new DecimalFormat("0.000");
		
		double totalPrecisionRate = totalPrecisionRate(confuseMatrix);
		stringBuffer.append("the total precision rate is:" + "\t" + df.format(totalPrecisionRate));
		stringBuffer.append("\n\n");
		stringBuffer.append("precision" + "\t\t" + "recall" + "\t\t" + "class");
		stringBuffer.append("\n");
		
		for(int c = 0; c < m_NumClasses; c++){
			
			double precision = precisionRate(c, confuseMatrix);
			double recall = reCallRate(c, confuseMatrix);
			String className = m_Data.classAttribute().value(c);
			
			stringBuffer.append(df.format(precision));
			stringBuffer.append("\t\t");
			stringBuffer.append(df.format(recall));
			stringBuffer.append("\t\t");
			stringBuffer.append(className);
			stringBuffer.append("\n");		
		}
		
		return stringBuffer;
		
	}
	
	/**
	 * 描述出混淆矩阵的形状及相关内容!
	 * @param confuseMatrix
	 * @return
	 */
	public StringBuffer confuseMatrixSummary(int[][] confuseMatrix){
		
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("===Confuse Matrix===");
		stringBuffer.append("\n\n");
		for(int c = 0; c < m_NumClasses; c++){
			
			for(int j = 0; j < m_NumClasses; j++){				
				stringBuffer.append(confuseMatrix[c][j]);
				stringBuffer.append("\t");
			}
			String className = m_Data.classAttribute().value(c);
			stringBuffer.append("|\t");
			stringBuffer.append(className);
			stringBuffer.append("\n");			
		}
		
		return stringBuffer;
		
	}
	
	/**
	 * 记录某个类别的准确率.
	 * @param index
	 * @param confuseMatrix
	 * @return
	 */
	public double precisionRate(int index,int[][] confuseMatrix){
		
		int predictRight = confuseMatrix[index][index]; //正确预测为这个类别的个数.
		int predictSum = 0; //无论正确与否，预测为这个类别的总的个数.
		
		
		for(int i = 0; i < m_NumClasses; i++){
			
			predictSum += confuseMatrix[i][index];
			
		}
		
		return (predictRight/(predictSum + 0.0));
		
	}
	
	/**
	 * 预测某个类别的召回率.
	 * @param index
	 * @param confuseMatrix
	 * @return
	 */
	public double reCallRate(int index,int[][] confuseMatrix){
		
		int predictRight = confuseMatrix[index][index]; //预测这个类别对的总个数
		int classSumCount = 0; //这个类别本身总的个数.
		for(int i = 0; i < m_NumClasses; i++){
			
			classSumCount += confuseMatrix[index][i];
			
		}
		
		return predictRight/(classSumCount + 0.0);
		
	}
	
	/**
	 * 这个分类器总的准确率.
	 * @param confuseMatrix
	 * @return
	 */
	public double totalPrecisionRate(int[][] confuseMatrix){
		
		int predictRight = 0; //预测为对的总的个数
		int totalCount = 0; //总的样本个数.
		for(int i = 0; i < m_NumClasses; i++){
			
			predictRight += confuseMatrix[i][i];
			for(int j = 0; j < m_NumClasses; j++)
				totalCount += confuseMatrix[i][j];
			
		}
		return predictRight/(totalCount + 0.0);		
	}
	

	
	
	
	

	
	
	
	/**
	 * 根据需要删除的特征的下标，下标从1开始,返回删除过后的数据集.
	 * @param data
	 * @param removeStr
	 * @return
	 * @throws Exception
	 */
	public Instances removeFeature(Instances data,String removeStr) throws Exception{
		
		Remove remove = new Remove();
		remove.setAttributeIndices(removeStr);
		remove.setInputFormat(data);
		remove.setInvertSelection(false);
		
		Instances newData = Filter.useFilter(data, remove);		
		return newData;
		
	}

}
