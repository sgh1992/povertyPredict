package process;

import java.util.Comparator;

public class ConsumeSortSidPattern implements Comparator<ConsumeRecord>{
	
	public int compare(ConsumeRecord r1,ConsumeRecord r2){
		
		//以学号为第一排序标准，当学号相同的情况下，以消费时间为第二排序标准.
		int s = r1.sid.compareTo(r2.sid);
		if(s == 0){
			
			int t = r1.transTime.compareTo(r2.transTime);
			return t;			
		}
		else 
			return s;
		
	}

}
