package analysize;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;



/**
 * 消费数据的统计，加上了每个学生总的消费天数!
 * @author Administrator
 *
 */

public class MonthAnalysize {
		
	private HashMap<Integer, String> timeRangeMap = null; //具体月份的划分,例如:1-5,6,7-10,11-12 为默认划分.
	
	private TreeSet<String> consumePlaceSet = null;
	
	private TreeSet<String> monthRangeSet = null;
	
	private TreeSet<String> mealTypeSet = null; //<早餐，午餐，晚餐> 食堂消费的划分!!!
	
	private TreeSet<String> mealTypeOtherSet = null; //<早餐,午餐,晚餐,早午，早晚，....> 食堂消费种类的其它划分!!!
	
	private HashMap<Integer, String> hourMealTypeMap = null; //<1-10,breakfast>
	
	public static int debug = 0;
	
	
	public void setMealTypeSet(TreeSet<String> mealSet){
		
		mealTypeSet = mealSet;		
	}
	
	public void setMealOtherTypeSet(TreeSet<String> mealOtherSet){
		
		mealTypeOtherSet = mealOtherSet;
		
	}
	
	public void setTimeRangeMap(HashMap<Integer, String> timeRangeMap){
		
		this.timeRangeMap = timeRangeMap;
		
		//初始化timeRangeMap时，也必须初始化monthRangeSet，即总共有多少不同的月份划分.
		monthRangeSet = new TreeSet<>();		
		Set<Integer> timeRangeSet = timeRangeMap.keySet();
		for(int month : timeRangeSet){			
			String monthRange = timeRangeMap.get(month);
			monthRangeSet.add(monthRange);			
		}		
	}
	
	public void setConsumePlaceSet(TreeSet<String> consumePlaceSet){
		
		this.consumePlaceSet = consumePlaceSet;		
	}
	
	public MonthAnalysize(){
				
		
		//inital timeRangeMap 这里的timeRange是可以根据需要进行改变的!!!

		timeRangeMap = new HashMap<>(); //月份.
		
//		for(int i = 1; i <= 5; i++)
//			timeRangeMap.put(i, "1_5");
//		
//		timeRangeMap.put(6, "6");
//		
//		for(int i = 7; i <= 9; i++)
//			timeRangeMap.put(i, "7_9");
		
		for(int i = 1; i <= 9; i++){
		
		    if(i == 1 || i == 2)
			   timeRangeMap.put(i, "9_10"); //之所以如此划分，是因为暑假时,夏天在宿舍洗衣服能够在一定程度上反映学生的贫困程度.			
		    else if(i <= 5)
			   timeRangeMap.put(i, "11_1"); //11月至1月.
		    else if(i == 6)
			  timeRangeMap.put(i, "2");
		   else 
			  timeRangeMap.put(i, "3_5");
		
	}
		
//		for(int i = 11; i <= 12; i++)
//			timeRangeMap.put(i, "11_12");
		
		
		//inital placeSet
		consumePlaceSet = new TreeSet<>();
		consumePlaceSet.add("mess");
		consumePlaceSet.add("classroom");
		consumePlaceSet.add("bathe");
		consumePlaceSet.add("water");
		consumePlaceSet.add("clothes");
		consumePlaceSet.add("supmarket");
		consumePlaceSet.add("library");
		consumePlaceSet.add("driver");
		consumePlaceSet.add("print");
		consumePlaceSet.add("leisure");
		consumePlaceSet.add("unknow");
		
		//inital monthRangeSet
		monthRangeSet = new TreeSet<>();		
		Set<Integer> timeRangeSet = timeRangeMap.keySet();
		for(int month : timeRangeSet){			
			String monthRange = timeRangeMap.get(month);
			monthRangeSet.add(monthRange);			
		}
		
		
		//inital mealTypeSet
		mealTypeSet = new TreeSet<>();
		mealTypeSet.add("breakfast");
		mealTypeSet.add("lunch");
		mealTypeSet.add("dinner");
		
	
		//inital mealTypeOtherSet
		mealTypeOtherSet = new TreeSet<>();		
		mealTypeOtherSet.add("breakfast");
		mealTypeOtherSet.add("lunch");
		mealTypeOtherSet.add("dinner");
		mealTypeOtherSet.add("breakfast_lunch");
		mealTypeOtherSet.add("breakfast_dinner");
		mealTypeOtherSet.add("lunch_dinner");
		mealTypeOtherSet.add("breakfast_lunch_dinner");
		
		
		//inital hourMealMap
		
		hourMealTypeMap = new HashMap<>();
		for(int i = 0; i <= 24; i++){
			
			if(i < 10)
				hourMealTypeMap.put(i, "breakfast");
			else if(i < 14)
				hourMealTypeMap.put(i, "lunch");
			else 
				hourMealTypeMap.put(i, "dinner");
			
		}

						
	}
	
	/**
	 * 每个学生在各类消费地点的消费情况.<食堂,<1-5,list<消费金额,消费次数>>>
	 * @param dataFile
	 * @param resultFile
	 * @throws Exception
	 */
	public void consumePlaceAnalysize(String dataFile,String resultFile) throws Exception{
		
		BufferedWriter	resultWriter = new BufferedWriter(new FileWriter(resultFile)); 		
		BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf-8"));
		
		String str = null;
		
		//inital <type,<timeRange,consumeCount,consumeBeforeDate>>
		HashMap<String, HashMap<String, String>> consumeTypeDateMap = new HashMap<>(); //<食堂，<某个时间段,在食堂的最近一次消费时的日期>>
		TreeMap<String, TreeMap<String, Integer>> consumeTypeDayMap = new TreeMap<>(); // <食堂,<某个时间段,在食堂总的消费天数>>
		initalConsumeDateMap(consumeTypeDateMap,consumeTypeDayMap);
		
		//<place,<timeRange,list<money,count>>>
		TreeMap<String, TreeMap<String, List<Double>>> consumeTypeMap = new TreeMap<>();
		initalMap(consumeTypeMap);
		
		
	
		//<timeRange,<早餐,List<消费天数,消费金额>>>
		TreeMap<String, TreeMap<String, Double>> mealCountMap = new TreeMap<>();		
		initalMealMap(mealCountMap);
		
		//<timeRange,<date,<type(早中晚),boolen>>>
		TreeMap<String,TreeMap<String, TreeMap<String, Boolean>>> mealBoolenMap = new TreeMap<>();
		initalMealBoolenMap(mealBoolenMap);
		
		dataReader.readLine(); //title
		String beforeID = null;
		
		Set<Integer> timeRangeSet = timeRangeMap.keySet();
		
		int maxMonth = Collections.max(timeRangeSet);//时间段内的最大值.
		int minMonth = Collections.min(timeRangeSet);//时间段内的最小值.
		
		String title = getTitle();
		
		//meal title
		String mealTitle = getMealTitle();
		StringBuffer titleBuffer = new StringBuffer();
		titleBuffer.append(title);
		titleBuffer.append(",");
		titleBuffer.append(mealTitle);
				
		resultWriter.write(titleBuffer.toString());
		resultWriter.newLine();
		
		//获得某个时间段内学生的有效消费天数!!!
		TreeMap<String, String> timeRangeBeforeDateMap = new TreeMap<>(); //<timeRange,beforeDate>
		TreeMap<String, Integer> timeRangeDayMap = new TreeMap<>();       //<timeRange,days>
		initalTimeRangeDayMap(timeRangeBeforeDateMap, timeRangeDayMap);
		

		String beforeDate = "";
		while((str = dataReader.readLine()) != null){			
			String[] array = str.split(",", -1);
			String studentID = array[0].trim();
			String transName = array[1].trim();
			String amount = array[5].trim();	
			String time = array[4].trim();
			
			//字段处，存在一个bug，parseDouble不能将非数值型数据转换为数值型数据.
			if(amount.equals("amount")){
				System.out.println(studentID);
				continue;
			}
			double amountValue = Double.parseDouble(amount);		
			String type = array[7];
			String month = array[8];
			String date = time.substring(0,8); //消费日期.
			
			//只针对在这个时间段的值才有效.
			int monthInt = Integer.parseInt(month);
			if(monthInt < minMonth || monthInt > maxMonth)
				continue;
			
			if(transName.equals("校园卡充值"))
				continue;			
			
			if(type.equals("null"))
				type = "unknow";		
			
			if(beforeID == null)
				beforeID = studentID;				
			
			if(!beforeID.equals(studentID)){								
				
				String string = getRecordValue(consumeTypeMap, beforeID,consumeTypeDayMap);	

				TreeMap<String, TreeMap<String, Integer>> mealTimeCountMap = getEveryMealCountMap(mealBoolenMap); //每个时间段内每餐消费的消费次数.
				String mealString = getMealRecord(mealTimeCountMap, mealCountMap,timeRangeDayMap);
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append(string);
				stringBuffer.append(",");
				stringBuffer.append(mealString);
				
				resultWriter.write(stringBuffer.toString());
				resultWriter.newLine();
				
				consumeTypeMap.clear();				
				initalMap(consumeTypeMap);
				
				//清空有关meal的各类值，并且对其进行初始化!!!
				mealBoolenMap.clear();
				initalMealBoolenMap(mealBoolenMap);
				
				mealCountMap.clear();
				initalMealMap(mealCountMap);
				
				
				timeRangeDayMap.clear();
				timeRangeBeforeDateMap.clear();
				
				initalTimeRangeDayMap(timeRangeBeforeDateMap, timeRangeDayMap);
				
				//清空记录各个地点的消费天数，然后重新初始化.
				consumeTypeDateMap.clear();
				consumeTypeDayMap.clear();
				initalConsumeDateMap(consumeTypeDateMap, consumeTypeDayMap);
				beforeDate = "";
								
			}
			
			TreeMap<String, List<Double>> consumeTimeRangeMap = consumeTypeMap.get(type);
			String timeRange = timeRangeMap.get(monthInt);
			List<Double> amountAndCountList = consumeTimeRangeMap.get(timeRange);
			double sumAmount = amountAndCountList.get(0) + amountValue;
			double sumCount = amountAndCountList.get(1) + 1;
			
			amountAndCountList.set(0, sumAmount);
			amountAndCountList.set(1, sumCount);
			
			
			//更新每天,每餐的消费金额,并且额外的统计每天每餐的消费情况!!!
			//这里的每餐的消费情况，仅仅只是针对的是食堂的消费情况,即前提条件是type = mess!!!
			if(type.equals("mess")){
				
				String hour = time.substring(9,11);
				int hourInt = Integer.parseInt(hour);
				String mealType = hourMealTypeMap.get(hourInt);
						
				double mealValue = mealCountMap.get(timeRange).get(mealType);
				mealCountMap.get(timeRange).put(mealType, mealValue + amountValue);
				
				TreeMap<String, TreeMap<String, Boolean>> mealDateMap = mealBoolenMap.get(timeRange);
				if(mealDateMap.containsKey(date)){				
					mealDateMap.get(date).put(mealType, true);				
				}
				else {
					TreeMap<String, Boolean> typeBoolMap = new TreeMap<>();
					for(String meal : mealTypeSet)
						typeBoolMap.put(meal, false);
					typeBoolMap.put(mealType, true);
					mealDateMap.put(date, typeBoolMap);
				}
			}
					
			//更新某个消费地点的总的消费天数!
			String beforeTypeDate = consumeTypeDateMap.get(type).get(timeRange); //某个特定地点的上次消费时间.
			if(!beforeTypeDate.equals(date)){				
				int count = consumeTypeDayMap.get(type).get(timeRange);
				consumeTypeDayMap.get(type).put(timeRange, ++count);
				consumeTypeDateMap.get(type).put(timeRange, date);
			}	
			
			//更新每个时间段的有效消费天数.
			beforeDate = timeRangeBeforeDateMap.get(timeRange);
			if(!beforeDate.equals(date)){
				
				int count = timeRangeDayMap.get(timeRange);
				timeRangeDayMap.put(timeRange, ++count);
				timeRangeBeforeDateMap.put(timeRange, date);
				
			}
			
			//更新总的消费天数!
			beforeDate = consumeTypeDateMap.get("all").get(timeRange);
			if(!beforeDate.equals(date)){				
				int count = consumeTypeDayMap.get("all").get(timeRange);
				consumeTypeDayMap.get("all").put(timeRange, ++count);
				consumeTypeDateMap.get("all").put(timeRange, date);				
			}			
			beforeID = studentID;			
		}
		
		if(consumeTypeMap.size() != 0){			
			String string = getRecordValue(consumeTypeMap, beforeID,consumeTypeDayMap);	
			
			TreeMap<String, TreeMap<String, Integer>> mealTimeCountMap = getEveryMealCountMap(mealBoolenMap);
			String mealStr = getMealRecord(mealTimeCountMap, mealCountMap,timeRangeDayMap);
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(string);
			stringBuffer.append(",");
			stringBuffer.append(mealStr);
			
			resultWriter.write(stringBuffer.toString());
			resultWriter.newLine();
		}
		
		resultWriter.close();
		dataReader.close();
		
		
	}
	
	public void initalTimeRangeDayMap(TreeMap<String, String> timeRangeBeforeDateMap,TreeMap<String, Integer> timeRangeDayMap){
		
		
		for(String timeRange : monthRangeSet){
			
			timeRangeBeforeDateMap.put(timeRange, "");
			timeRangeDayMap.put(timeRange, 0);
			
		}
		
	}
	
	
	/**
	 * 得出有关食堂消费的早，中，晚三类消费的消费金额及行为习惯的Title！！！
	 * @return
	 */
	public String getMealTitle(){
		
		StringBuffer stringBuffer = new StringBuffer();
		
		
		for(String timeRange : monthRangeSet){
			
			for(String mealType : mealTypeOtherSet){
				
				if(mealTypeSet.contains(mealType)){
					
					String amountTitle = "meal_" + timeRange + "_" + mealType + "_amount";
					String countTitle = "meal_" + timeRange + "_" + mealType + "_count";
					String averTitle = "meal_" + timeRange  + "_" + mealType + "_averAmount";
					
					stringBuffer.append(amountTitle);
					stringBuffer.append(",");
					stringBuffer.append(countTitle);
					stringBuffer.append(",");
					stringBuffer.append(averTitle);
					stringBuffer.append(",");
					
				}
				
				else {
					String countTitle = "meal_" + timeRange + "_" + mealType + "_count";
					stringBuffer.append(countTitle);
					stringBuffer.append(",");
				}				
			}
			
			//每段时间内的有效消费天数!!!
			String timeRangeDayTitle = "consume_" +  timeRange + "_days";
			stringBuffer.append(timeRangeDayTitle);
			stringBuffer.append(",");
		}
		
		String sumCountTitle = "meal_sumCount"; //在食堂内总的消费次数.
		stringBuffer.append(sumCountTitle);
		return stringBuffer.toString();
		
	}
	
	/**
	 * 根据每餐消费在各个时间段内的消费次数，消费金额，及每天是否按时吃饭等情况
	 * 综合分析每个学生的食堂消费水平及学生的行为规律!!!
	 * mealCountMap <timeRange,<type,count>>
	 * mealAmountMap <type,<timeRange,amount>>
	 * timeRangeDayMap <timeRange,count>每个时间段的有效消费天数.
	 * @param mealCountMap
	 * @param mealAmountMap
	 * @return
	 */
	public String getMealRecord(TreeMap<String, TreeMap<String, Integer>> mealCountMap,TreeMap<String, TreeMap<String, Double>> mealAmountMap,TreeMap<String, Integer> timeRangeDayMap){
		
		StringBuffer stringBuffer = new StringBuffer();
		Set<String> timeRangeSet = mealAmountMap.keySet();
		DecimalFormat df = new DecimalFormat("0.000");
		int mealSumCount = 0;
		for(String timeRange : timeRangeSet){
			
			TreeMap<String, Double> amountMap = mealAmountMap.get(timeRange);
			TreeMap<String, Integer> countMap = mealCountMap.get(timeRange);
			Set<String> mealType = countMap.keySet();
			for(String type : mealType){				
				int count = countMap.get(type);
				if(amountMap.containsKey(type)){ //早，中，晚三类消费.
					double amount = amountMap.get(type);					
					double averAmount = 0;
					if(count != 0)
						averAmount = amount/count;
					mealSumCount += count;					
					stringBuffer.append(df.format(amount));
					stringBuffer.append(",");
					stringBuffer.append(count);
					stringBuffer.append(",");
					stringBuffer.append(df.format(averAmount));
					stringBuffer.append(",");
				}
				else { //除了早，中，晚之外的其它消费!!! 这里只记录消费次数!!!
					
					stringBuffer.append(count);
					stringBuffer.append(",");
				}				
			}
			
			//每个时间段内的消费天数!!!
			int days = timeRangeDayMap.get(timeRange);
			stringBuffer.append(days);
			stringBuffer.append(",");
			
		}		
		stringBuffer.append(mealSumCount);
		return stringBuffer.toString();
		
	}
	
	/**
	 * 获得每个时间段内每种食堂消费类型的有效天数!!!
	 * 食堂消费类型的划分是动态的!!!
	 * 获得<timeRange,<早餐消费,消费天数>>
	 * @param mealBoolenMap
	 * @return
	 */
	public TreeMap<String, TreeMap<String, Integer>> getEveryMealCountMap(TreeMap<String, TreeMap<String, TreeMap<String, Boolean>>> mealBoolenMap){
		
		TreeMap<String, TreeMap<String, Integer>> mealTimeTypeCountMap = new TreeMap<>();
		
		Set<String> timeRangeSet = mealBoolenMap.keySet();
		for(String timeRange : timeRangeSet){
			
			TreeMap<String, Integer> mealTypeCountMap = new TreeMap<>();
			//早，中，晚，三类食堂消费，加上它们的任意组合，共记7种消费类型!!!
			int breakfast = 0;
			int lunch = 0;
			int dinner = 0;
			int breakFastAndLunch = 0;
			int breakFastAndDinner = 0;
			int lunchAndDinner = 0;
			int breakAndLunchAndDinner = 0;
			
			TreeMap<String, TreeMap<String, Boolean>> mealDateMap = mealBoolenMap.get(timeRange);
			Set<String> dateSet = mealDateMap.keySet();
			
			for(String date : dateSet){
				
				TreeMap<String, Boolean> mealTypeMap = mealDateMap.get(date);
				boolean breakFastFlag = mealTypeMap.get("breakfast");
				boolean lunchFlag = mealTypeMap.get("lunch");
				boolean dinnerFlag = mealTypeMap.get("dinner");
				
				if(breakFastFlag)
					breakfast += 1;
				if(lunchFlag)
					lunch += 1;
				if(dinnerFlag)
					dinner += 1;
				if(breakFastFlag && lunchFlag)
					breakFastAndLunch += 1;
				if(breakFastFlag && dinnerFlag)
					breakFastAndDinner += 1;
				if(lunchFlag && dinnerFlag)
					lunchAndDinner += 1;
				if(breakFastFlag && lunchFlag && dinnerFlag)
					breakAndLunchAndDinner += 1;
								
			}
			
			
			mealTypeCountMap.put("breakfast", breakfast);
			mealTypeCountMap.put("lunch", lunch);
			mealTypeCountMap.put("dinner", dinner);
			mealTypeCountMap.put("breakfast_lunch", breakFastAndLunch);
			mealTypeCountMap.put("breakfast_dinner", breakFastAndDinner);
			mealTypeCountMap.put("lunch_dinner", lunchAndDinner);
			mealTypeCountMap.put("breakfast_lunch_dinner", breakAndLunchAndDinner);
			
			mealTimeTypeCountMap.put(timeRange, mealTypeCountMap);
		
		}		
		return mealTimeTypeCountMap;
		
	}
	
	
	/**
	 * <timeRange,<date,<type,boolen>>>
	 * 初始化mealBoolenMap.
	 * @param mealBoolenMap
	 */
	public void initalMealBoolenMap(TreeMap<String, TreeMap<String, TreeMap<String, Boolean>>> mealBoolenMap){
		
		for(String timeRange : monthRangeSet){
			
			TreeMap<String, TreeMap<String, Boolean>> dateMap = new TreeMap<>();
			mealBoolenMap.put(timeRange, dateMap);
			
		}
		
	}

	/**
	 * <timeRange,<早餐,<消费金额>>> mealCountMap 早，中，晚三餐的消费天数，消费金额!!
	 * @param mealDateMap
	 * @param mealCountMap
	 */
	public void initalMealMap(TreeMap<String, TreeMap<String, Double>> mealCountMap){
		

		for(String timeRange : monthRangeSet){
					
			TreeMap<String, Double> mealValueMap = new TreeMap<>();			
			for(String mealType : mealTypeSet){	
				
				mealValueMap.put(mealType, 0.0);				
			}			
			mealCountMap.put(timeRange, mealValueMap);			
		}
		
	}
	
	/**
	 * 初始化所有地点的消费初始时间.
	 * <consumeType,beforeDate> <食堂,<1_5,食堂在1_5月份最近一次消费的日期>>
	 * <consumeType,<timeRange,count>>  <食堂,<1_5,总的消费天数>>
	 * @param consumeTypeDateMap
	 */
	public void initalConsumeDateMap(HashMap<String, HashMap<String, String>> consumeTypeDateMap,TreeMap<String, TreeMap<String, Integer>> consumeTypeDayMap){
		
		for(String type : consumePlaceSet){									
			HashMap<String, String> consumeMonthDateMap = new HashMap<>();			
			TreeMap<String, Integer> consumeMonthTypeCountMap = new TreeMap<>();
			for(String timeRange : monthRangeSet){
				consumeMonthTypeCountMap.put(timeRange, 0);
				consumeMonthDateMap.put(timeRange, "");
			}
			
			consumeTypeDayMap.put(type, consumeMonthTypeCountMap);
			consumeTypeDateMap.put(type, consumeMonthDateMap);
		}
		
		HashMap<String, String> consumeMonthDateMap = new HashMap<>();
		TreeMap<String, Integer> consumeMonthTypeCountMap = new TreeMap<>();
		for(String timeRange : monthRangeSet){
			consumeMonthTypeCountMap.put(timeRange, 0);
			consumeMonthDateMap.put(timeRange, "");
		}
		consumeTypeDayMap.put("all", consumeMonthTypeCountMap);
		consumeTypeDateMap.put("all", consumeMonthDateMap);
		
	}
	
	
	/**
	 * 得出每一个学生的记录.
	 * <食堂,<1-5月,{总的消费金额,总的消费次数}>>
	 * <食堂,<1-5月,消费天数>> 这里的type与上面的type不一样,这里有个额外的all字段!
	 * @param consumeTypeMap
	 * @return
	 * @throws Exception
	 */
	public String getRecordValue(TreeMap<String, TreeMap<String, List<Double>>> consumeTypeMap,String sid,TreeMap<String, TreeMap<String, Integer>> consumeTypeDayMap) throws Exception{
		
		StringBuffer stringBuffer = new StringBuffer();		
		Set<String> typeSet = consumeTypeMap.keySet();		
		DecimalFormat df = new DecimalFormat("0.00");		
		double sumAmount = 0;
		double sumCount = 0;
		
		stringBuffer.append(sid);
		stringBuffer.append(",");		
					
		for(String consumeType : typeSet){
			
			TreeMap<String, List<Double>> consumeTimeRangeMap = consumeTypeMap.get(consumeType);
			Set<String> timeRangeSet = consumeTimeRangeMap.keySet();						
			TreeMap<String, Integer> consumeTypeDayCountMap = consumeTypeDayMap.get(consumeType);
			
			for(String timeRange : timeRangeSet){				
				List<Double> amountAndCount = consumeTimeRangeMap.get(timeRange);
				double amount = amountAndCount.get(0);
				double count = amountAndCount.get(1);
				int consumeTypeDayCount = consumeTypeDayCountMap.get(timeRange); //某种消费类型总的消费天数.
				stringBuffer.append(df.format(amount));
				stringBuffer.append(",");
				stringBuffer.append(count);
				stringBuffer.append(",");
				stringBuffer.append(consumeTypeDayCount); //这个消费地点总的消费天数!
				stringBuffer.append(",");

				
				sumAmount += amount;
				sumCount += count;						
								
			}					
		}
		
		//这里与之前的那个地方不同的是，consumeTypeMap中没有all这个消费类型!
		int sumDays = 0; //总的天数!	
		
		for(String timeRange : monthRangeSet){			
			int count = consumeTypeDayMap.get("all").get(timeRange);
			sumDays += count;			
		}
		
		stringBuffer.append(df.format(sumAmount));
		stringBuffer.append(",");
		stringBuffer.append(sumCount);
		stringBuffer.append(",");
		stringBuffer.append(sumDays);
		
		String string = stringBuffer.toString();
		return string;		
	}
	
	public String getTitle(){
		
		StringBuffer stringBuffer = new StringBuffer();
		
		stringBuffer.append("sid");
		stringBuffer.append(",");
		
		for(String type : consumePlaceSet){
			
			for(String monthRange : monthRangeSet){
				
				String amountTitle = "consume_" + type + "_" + monthRange + "_amount";
				String countTitle = "consume_" + type + "_" + monthRange + "_count";
				String daysTitle = "consume_" + type + "_" + monthRange + "_days";
				
				stringBuffer.append(amountTitle);
				stringBuffer.append(",");
				stringBuffer.append(countTitle);
				stringBuffer.append(",");			
				stringBuffer.append(daysTitle);
				stringBuffer.append(",");
				
			}
		}
		
		String SumAmountTitle = "consume_sumAmount"; 
		String SumCountTitle = "consume_sumCount"; 
		String sumDaysTitle = "consume_sumDays";
		stringBuffer.append(SumAmountTitle);
		stringBuffer.append(",");
		
		stringBuffer.append(SumCountTitle);
		stringBuffer.append(",");
		
		stringBuffer.append(sumDaysTitle);
		
		return stringBuffer.toString();		
	}		
	/**
	 * <消费地点,<消费时间段，<消费地点，消费次数>>>
	 * @param consumeTypeMap
	 */
	public void initalMap(TreeMap<String, TreeMap<String, List<Double>>> consumeTypeMap){
			
		for(String type : consumePlaceSet){
			
			TreeMap<String, List<Double>> timeMap = new TreeMap<>();
			for(String timeRange : monthRangeSet){
				
				List<Double> consumeMoneyAndCount = new ArrayList<>();
				consumeMoneyAndCount.add(0.0);
				consumeMoneyAndCount.add(0.0);				
				timeMap.put(timeRange, consumeMoneyAndCount);
				
			}
			
			consumeTypeMap.put(type, timeMap);
			
		}
		
	}
	
	/**
	 * 作为测试时使用!
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		
		MonthAnalysize monthAnalysize = new MonthAnalysize();
		
		HashMap<Integer, String> monthMap = new HashMap<>();
		for(int i = 1; i <= 10; i++)
			monthMap.put(i, "1_10");
	
		for(int i = 11; i <= 12; i++)
			monthMap.put(i, "11_12");
		
		//改变月份的划分.
		monthAnalysize.setTimeRangeMap(monthMap);
		String consumeMonth1_10 = "E:/processTemp/consumeMonth1_10.csv";
		String consumeSortedFile = "E:/consumeMonthSorted.csv";		
		monthAnalysize.consumePlaceAnalysize(consumeSortedFile, consumeMonth1_10);
		
	}

}
