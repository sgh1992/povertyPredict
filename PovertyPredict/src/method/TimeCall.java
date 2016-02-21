package method;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class TimeCall {
	
	/**
	 * 返回当前日期与所要比较的日期之间相隔的月数.
	 * 其中这里的currentDate是早的日期，而date是晚的日期.
	 * 计算的是date - currentDate的相隔月数.
	 * @param date
	 * @param currentDate
	 * @return
	 * @throws Exception
	 */
	public static int distanceMonth(String date, String currentDate ) throws Exception{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(df.parse(date));
		
		Calendar currCalendar = Calendar.getInstance();
		currCalendar.setTime(df.parse(currentDate));
		
		int month = 0;
		
		while(!currCalendar.after(dateCalendar)){
			
			month++;			
			currCalendar.add(Calendar.MONTH, 1);
			
		}
		
		month = month - 1;
		
		return month;
		
	}
	
	/**
	 * 返回与指定日期相隔month月之后的具体日期.
	 * @param date
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public static String getMonth(String date,int month) throws Exception{
		
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(df.parse(date));
		
		calendar.add(Calendar.MONTH, month);
		
		Date curDate = calendar.getTime();
		
		return df.format(curDate);
		
		
	}
	
	
	 // 计算两个日期相差的时间数，以分钟为单位
  	public static long calDateSub(String date, String curDate) {
  		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");// 定义格式
  		long k = 0;
  		try {
  			// 转化为long型,并且进行算术运算
  			k = ((df.parse(curDate).getTime() - df.parse(date).getTime()) / (1000 * 60 * 60 * 24));   			
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
  		return k;
  	}
  	
  	//在某个日期后，再加一个整数，形成另一个日期,此处的日期精确到天
   public static String addDate(String d, long day) throws Exception {  
   	SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HHmmss");// 定义格式
   	Date date = null;
   	//将此种格式的字符串转化为日期
   	date = df.parse(d);
    	//getTime()表示该时间所转化的毫秒数据
	    long time = date.getTime();  
	    day = day * 24 * 60 * 60 * 1000;  
	    time += day;  
	    date = new Date(time);
	    String timeString = df.format(date);
	    //反过来，将毫秒数据转化为日期
	    return timeString;  
  } 
   
	/**
	 * 根据指定的日期格式，返回周几.
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static int dayofweek(String date) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmmss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(format.parse(date));
		int dayForWeek = 0;  
		 if(calendar.get(Calendar.DAY_OF_WEEK) == 1){  
		  dayForWeek = 7;  
		 }else{  
		  dayForWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;  
		 }  
		 return dayForWeek;
	}
	
	public static String dayofEnglish(String dateTime) throws Exception{
		int day = dayofweek(dateTime);
		String englishday = null;
		
		switch (day) {
		case 1:
			englishday = "Monday";
			break;
		case 2:
			englishday = "Tuesday";
			break;
		case 3:
			englishday = "Wednesday";
			break;
		case 4:
			englishday = "Thursday";
			break;
		case 5:
			englishday = "Friday";
			break;
		case 6:
			englishday = "Saturday";
			break;
		case 7:
			englishday = "Sunday";
		default:
			break;
		}
		
		return englishday;
		
	}
	
	

}
