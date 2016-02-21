package dataMining;

import java.util.HashMap;


import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

public class TwoLayerClassifier extends Classifier{
	
	/**
	 * 两层分类器,一层用来训练贫困生与正常学生,
	 * 第二层用来训练一般贫困，较贫困，非常贫困!
	 */
	private static final long serialVersionUID = 1L;
	private Classifier layer1Classifier = null; //第一层,基本分类器.
	private Classifier layer2Classifier = null; //第二层,基本分类器.
	private Instances layer1Insts = null; //第一层的训练集.
	private Instances layer2Insts = null;//第二层的训练集.
	private HashMap<String, Integer> classTypeIndexMap = null; // <normal,0>,<general poverty,1>,<poorer,2>,...
	private int numClasses;
	
	public TwoLayerClassifier(Classifier classifier1,Classifier classifier2,Instances layer1Insts,Instances layer2Insts){
		
		layer1Classifier = classifier1;
		layer2Classifier = classifier2;
		this.layer1Insts = layer1Insts;
		this.layer2Insts = layer2Insts;
	}
	
	public TwoLayerClassifier(){
		
				
	}
	
	public void setLayer1Insts(Instances layer1Insts){
		
		this.layer1Insts = layer1Insts;
		
	}
	
	public void setLayer2Insts(Instances layer2Insts){
		this.layer2Insts = layer2Insts;
	}
	
		
	public void setLayer1Classifier(Classifier classifier){
		
		layer1Classifier = classifier;		
	}
	
	
	public void setLayer2Classifier(Classifier classifier){
		
		layer2Classifier = classifier;
		
	}
	
	
	/**
	 * 两层分类器
	 * layer1Classifier用来对第一层正常学生与贫困生进行二分类
	 * layer2Classifier用来对预测为贫困生的学生进行再次分类，划分为general poverty,poorer,very poverty
	 * @param data 包含全部分类的学生数据.即normal,general poverty,poorer,very poverty总共四类!
	 */
	public void buildClassifier(Instances data) throws Exception{
		
		layer1Classifier.buildClassifier(layer1Insts);
		layer2Classifier.buildClassifier(layer2Insts);
		numClasses = data.numClasses();
		classTypeIndexMap = new HashMap<>();
		for(int c = 0; c < numClasses; c++){
			String classTypeName = data.classAttribute().value(c);
			classTypeIndexMap.put(classTypeName, c);
		}		
	}
	
	/**
	 * 这里需要预测的inst是含有四个类别的数据.
	 * layer1Inst 第一层分类时的实例
	 * layer2Inst 第二层分类时的实例
	 */
	public double[] distributionForInstance(Instance layer1Inst,Instance layer2Inst) throws Exception{
		
		double[] predictProbs = new double[numClasses];
				
		double[] predictLayer1Probs = layer1Classifier.distributionForInstance(layer1Inst);
		
		int predictLayer1 = Utils.maxIndex(predictLayer1Probs);
		
		int normalIndex = classTypeIndexMap.get("normal");
		int generalPovertyIndex = classTypeIndexMap.get("general povery");
		int poorerIndex = classTypeIndexMap.get("poorer");
		int veryPovertyIndex = classTypeIndexMap.get("very poverty");
		
		//如果第一层分类结果为非贫困生，则直接返回结果.
		if(layer1Insts.classAttribute().value(predictLayer1).equals("normal")){
						
			predictProbs[normalIndex] = predictLayer1Probs[predictLayer1];
			
			//如果预测为非贫困生，则其余三类贫困生的概率依次划分为:3,2,1的比例.对应的是general povery,poorer,very poverty
			double otherPredicts = (1 - predictLayer1Probs[predictLayer1])/6;
						
			predictProbs[generalPovertyIndex] = otherPredicts * 3;
						
			predictProbs[poorerIndex] = otherPredicts * 2;
			
			predictProbs[veryPovertyIndex] = otherPredicts * 1;
			
		}
		
		//如果预测为贫困生，则需要进行进一步的预测
		//进一步预测该个学生属于哪类贫困生.同时对于正常学生的概率计算,则进行归一化处理数据.
		//为确保归一化之后，贫困生的概率值要比正常学生的概率值要大，那么将normal的概率值先除以3.
		else{
			
			double predictLayer1NoramlProbs = predictLayer1Probs[1 - predictLayer1]/3;
			double sumProbs = 1 + predictLayer1NoramlProbs; //归一化时，总的值，作为分母.
			predictProbs[normalIndex] = predictLayer1NoramlProbs/sumProbs;
						
			double[] predictLayer2Probs = layer2Classifier.distributionForInstance(layer2Inst);//第二层分类时的结果.
			for(int i = 0; i < layer2Insts.numClasses(); i++){

				String predictLayer2ClassType = layer2Insts.classAttribute().value(i);				
				int classIndex = classTypeIndexMap.get(predictLayer2ClassType);			
				predictProbs[classIndex] = predictLayer2Probs[i]/sumProbs;
				
				
			}
		
		}
		
		return predictProbs;
			
		
		
	}
	
	public double classifyInstance(Instance layer1Inst,Instance layer2Inst) throws Exception{
		
		double[] predictProbs = distributionForInstance(layer1Inst,layer2Inst);
		
		return Utils.maxIndex(predictProbs);
		
	}
	
	

}
