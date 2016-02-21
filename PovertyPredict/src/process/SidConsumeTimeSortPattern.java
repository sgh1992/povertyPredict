package process;

import java.util.Comparator;

/**
 * 对消费数据表进行排序，首先以学号为第一排序标准，如果学号相同，则以时间为第二排序标准.
 * 目的是使所有学生按他们自己从入学到离校时，在校的活动时间顺序为来排序.
 * @author Administrator
 *
 */
public class SidConsumeTimeSortPattern implements Comparator<SidAndConsumeTimeRecord>{
	
	
	public int compare(SidAndConsumeTimeRecord r1,SidAndConsumeTimeRecord r2){
		
		int s = r1.sid.compareTo(r2.sid);
		
		if(s == 0){ //如果学号相同的情况下，再按照时间排序.
			
			int t = r1.transTime.compareTo(r2.transTime);
			return t;
			
		}
		else 
			return s;
		
	}
	
	

}
