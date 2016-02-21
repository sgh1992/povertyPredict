package analysize;

import java.util.ArrayList;
import java.util.List;

import dataMining.ModelRun;


/**
 * 在获得统一的特征数据时，对学生特征数据进行进一步的特征选择，加工，生成最新的特征数据!
 * 这些数据也就是训练模型与测试模型时，所用的数据.
 * 如果最终模型的特征数据发生改变了，那么这个方法就是适应那种改变的方法!!!
 * @author Administrator
 *
 */
public class FeatureSelection {
	

	/**
	 * 由于标注存在一定问题，故将一些比较显著的异常标注删除.
	 * 得到一些相对正确的标注.
	 * @param featureFile
	 * @param modelFeatureFile
	 * @param tempPath
	 * @throws Exception
	 */
	public void trainModelFile(String tableClassName,String modelResultFeatureFile,String tempPath) throws Exception{
		
		DataReAbstract dataReAbstract = new DataReAbstract();

		int normalRow = 2480;
		String normalFile = tempPath + "normal.csv";
		
		int generalPovertyRow = 641;
		String generalPovertyFile = tempPath + "generalPoverty.csv";
		
		int poorerRow = 576;
		String poorerFile = tempPath + "poorer.csv";
		
		int veryPovertyRow = 511;
		String veryPovertyFile = tempPath + "veryPoverty.csv";
		

		dataReAbstract.dataReDivide("normal", true, normalRow, normalFile,tableClassName);
		dataReAbstract.dataReDivide("general povery", false, generalPovertyRow, generalPovertyFile,tableClassName);
		dataReAbstract.dataReDivide("poorer", false, poorerRow, poorerFile,tableClassName);
		dataReAbstract.dataReDivide("very poverty", false, veryPovertyRow, veryPovertyFile,tableClassName);

		List<String> fileList = new ArrayList<>();
		fileList.add(normalFile);
		fileList.add(generalPovertyFile);
		fileList.add(poorerFile);
		fileList.add(veryPovertyFile);
		dataReAbstract.dataCombine(fileList, modelResultFeatureFile);
		
	}
	
	/**
	 * 特征进一步加工，得到最终需要预测数据的数据格式!!!
	 * 这里有一个非常大的不灵活性，即这个方法中的sql语句的内容必须与训练集中的sql语句的内容保持一致.
	 * @param tableClassName
	 * @param predictFile
	 */
	public void getPredictData(String tableClassName,String predictFile) throws Exception{
		
		DataReAbstract dataReAbstract = new DataReAbstract();
		
		//提取出测试数据!!!
		//String sqlString = "select *,consume_sumAmount/consume_sumDays as consume_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days) as mess_sumDays,(consume_sumAmount - consume_mess_9_10_amount - consume_mess_11_1_amount - consume_mess_2_amount - consume_mess_3_5_amount)/consume_sumDays as removeMess_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days)/consume_SumDays as mess_RatioDays,(consume_mess_9_10_Amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/consume_sumAmount as mess_RatioAmount,(consume_bathe_9_10_days + consume_bathe_11_1_days + consume_bathe_2_days + consume_bathe_3_5_days)/consume_sumDays as bathe_RatioDays,(consume_bathe_9_10_amount + consume_bathe_11_1_amount + consume_bathe_2_amount + consume_bathe_3_5_amount)/consume_sumAmount as bathe_RatioAmount,(consume_clothes_9_10_days + consume_clothes_11_1_days + consume_clothes_2_days + consume_clothes_3_5_days)/consume_sumDays as clothes_RatioDays,(consume_clothes_9_10_amount + consume_clothes_11_1_amount + consume_clothes_3_5_amount + consume_clothes_2_amount)/consume_sumAmount as clothes_RatioAmount,(consume_water_9_10_days + consume_water_11_1_days + consume_water_2_days + consume_water_3_5_days)/consume_sumDays as water_RatioDays,(consume_water_9_10_amount + consume_water_11_1_amount + consume_water_2_amount + consume_water_3_5_amount)/consume_sumAmount as water_RatioAmount,(consume_supmarket_9_10_days + consume_supmarket_11_1_days + consume_supmarket_2_days + consume_supmarket_3_5_days)/consume_sumDays as supmarket_RatioDays,(consume_supmarket_9_10_amount + consume_supmarket_11_1_amount + consume_supmarket_2_amount + consume_supmarket_3_5_amount)/consume_sumamount as supmarket_Ratioamount,(consume_library_9_10_days + consume_library_11_1_days + consume_library_2_days + consume_library_3_5_days)/consume_sumDays as library_sumRatioDays,(consume_library_9_10_amount + consume_library_11_1_amount + consume_library_2_amount + consume_library_3_5_amount)/consume_sumamount as library_sumRatioamount,(consume_driver_9_10_days + consume_driver_11_1_days + consume_driver_2_days + consume_driver_3_5_days)/consume_sumDays as driver_sumRatioDays,(consume_driver_9_10_amount + consume_driver_11_1_amount + consume_driver_2_amount + consume_driver_3_5_amount)/consume_sumamount as driver_sumRatioamount,(consume_classroom_9_10_days + consume_classroom_11_1_days + consume_classroom_2_days + consume_classroom_3_5_days)/consume_sumDays as classroom_sumRatioDays,(consume_classroom_9_10_amount + consume_classroom_11_1_amount + consume_classroom_2_amount + consume_classroom_3_5_amount)/consume_sumamount as classroom_sumRatioamount,(consume_mess_9_10_amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/meal_sumCount as AverMeal,(meal_9_10_lunch_count)/consume_9_10_days as meal_9_10_lunch_DaysRatio,meal_11_1_lunch_count/consume_11_1_days as meal_11_1_lunch_RatioDay,meal_2_lunch_count/consume_2_days as lunch_2_RatioDays,meal_3_5_lunch_count/consume_3_5_days as lunch_3_5_RatioDays ,(meal_9_10_breakfast_count)/consume_9_10_days as meal_9_10_breakfast_DaysRatio,meal_11_1_breakfast_count/consume_11_1_days as meal_11_1_breakfast_RatioDay,meal_2_breakfast_count/consume_2_days as breakfast_2_RatioDays,meal_3_5_breakfast_count/consume_3_5_days as breakfast_3_5_RatioDays,(meal_9_10_dinner_count)/consume_9_10_days as meal_9_10_dinner_DaysRatio,meal_11_1_dinner_count/consume_11_1_days as meal_11_1_dinner_RatioDay,meal_2_dinner_count/consume_2_days as dinner_2_RatioDays,meal_3_5_dinner_count/consume_3_5_days as dinner_3_5_RatioDays  from " + tableClassName +  " where (sid like '2012%' or sid like '2013%' or sid like '2014%') and consume_sumDays > 130 "; 
	    //这里省略掉了条件过滤语句，主要是用来对学生成绩预测进行预测时需要用到这个.贫困生预测，则没有这个需求!!!
		String sqlString = "select *,consume_sumAmount/consume_sumDays as consume_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days) as mess_sumDays,(consume_sumAmount - consume_mess_9_10_amount - consume_mess_11_1_amount - consume_mess_2_amount - consume_mess_3_5_amount)/consume_sumDays as removeMess_AverDays,(consume_mess_9_10_days + consume_mess_11_1_days + consume_mess_2_days + consume_mess_3_5_days)/consume_SumDays as mess_RatioDays,(consume_mess_9_10_Amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/consume_sumAmount as mess_RatioAmount,(consume_bathe_9_10_days + consume_bathe_11_1_days + consume_bathe_2_days + consume_bathe_3_5_days)/consume_sumDays as bathe_RatioDays,(consume_bathe_9_10_amount + consume_bathe_11_1_amount + consume_bathe_2_amount + consume_bathe_3_5_amount)/consume_sumAmount as bathe_RatioAmount,(consume_clothes_9_10_days + consume_clothes_11_1_days + consume_clothes_2_days + consume_clothes_3_5_days)/consume_sumDays as clothes_RatioDays,(consume_clothes_9_10_amount + consume_clothes_11_1_amount + consume_clothes_3_5_amount + consume_clothes_2_amount)/consume_sumAmount as clothes_RatioAmount,(consume_water_9_10_days + consume_water_11_1_days + consume_water_2_days + consume_water_3_5_days)/consume_sumDays as water_RatioDays,(consume_water_9_10_amount + consume_water_11_1_amount + consume_water_2_amount + consume_water_3_5_amount)/consume_sumAmount as water_RatioAmount,(consume_supmarket_9_10_days + consume_supmarket_11_1_days + consume_supmarket_2_days + consume_supmarket_3_5_days)/consume_sumDays as supmarket_RatioDays,(consume_supmarket_9_10_amount + consume_supmarket_11_1_amount + consume_supmarket_2_amount + consume_supmarket_3_5_amount)/consume_sumamount as supmarket_Ratioamount,(consume_library_9_10_days + consume_library_11_1_days + consume_library_2_days + consume_library_3_5_days)/consume_sumDays as library_sumRatioDays,(consume_library_9_10_amount + consume_library_11_1_amount + consume_library_2_amount + consume_library_3_5_amount)/consume_sumamount as library_sumRatioamount,(consume_driver_9_10_days + consume_driver_11_1_days + consume_driver_2_days + consume_driver_3_5_days)/consume_sumDays as driver_sumRatioDays,(consume_driver_9_10_amount + consume_driver_11_1_amount + consume_driver_2_amount + consume_driver_3_5_amount)/consume_sumamount as driver_sumRatioamount,(consume_classroom_9_10_days + consume_classroom_11_1_days + consume_classroom_2_days + consume_classroom_3_5_days)/consume_sumDays as classroom_sumRatioDays,(consume_classroom_9_10_amount + consume_classroom_11_1_amount + consume_classroom_2_amount + consume_classroom_3_5_amount)/consume_sumamount as classroom_sumRatioamount,(consume_mess_9_10_amount + consume_mess_11_1_amount + consume_mess_2_amount + consume_mess_3_5_amount)/meal_sumCount as AverMeal,(meal_9_10_lunch_count)/consume_9_10_days as meal_9_10_lunch_DaysRatio,meal_11_1_lunch_count/consume_11_1_days as meal_11_1_lunch_RatioDay,meal_2_lunch_count/consume_2_days as lunch_2_RatioDays,meal_3_5_lunch_count/consume_3_5_days as lunch_3_5_RatioDays ,(meal_9_10_breakfast_count)/consume_9_10_days as meal_9_10_breakfast_DaysRatio,meal_11_1_breakfast_count/consume_11_1_days as meal_11_1_breakfast_RatioDay,meal_2_breakfast_count/consume_2_days as breakfast_2_RatioDays,meal_3_5_breakfast_count/consume_3_5_days as breakfast_3_5_RatioDays,(meal_9_10_dinner_count)/consume_9_10_days as meal_9_10_dinner_DaysRatio,meal_11_1_dinner_count/consume_11_1_days as meal_11_1_dinner_RatioDay,meal_2_dinner_count/consume_2_days as dinner_2_RatioDays,meal_3_5_dinner_count/consume_3_5_days as dinner_3_5_RatioDays  from " + tableClassName +  ""; 
		dataReAbstract.testDataAbstract(sqlString, predictFile);		
	}
	
	/**
	 * 将上述得到的整个的训练数据，分为两层数据.
	 * 第一层数据用来训练正常学生与贫困学生
	 * 第二层数据用来继续分类贫困生的三种类型,normal,general povery,very poverty.
	 * @param dataFile
	 */
	public void getTwoLayerData(String dataFile,String twoClassFile,String threeClassFile) throws Exception{
		
		dataTransfer(dataFile, twoClassFile, threeClassFile);
		
		
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
	
	

}
