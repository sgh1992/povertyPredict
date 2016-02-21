package process;

public class SidAndConsumeTimeRecord {
	
	public String sid = null;
	public String transName = null;
	public String deviceName = null;
	public String devphyid = null;
	public String transTime = null;
	public String cardbal = null;
	public String amount = null;
	public String month = null;
	public String type = null;
	
	public SidAndConsumeTimeRecord(String sid,String transName,String deviceName,String devphyid,String transTime,String amount,String cardbal,String type,String month){
		
		this.sid = sid;
		this.transName = transName;
		this.deviceName = deviceName;
		this.devphyid = devphyid;
		this.transTime = transTime;
		this.cardbal = cardbal;
		this.amount = amount;
		this.type = type;
		this.month = month;
		
	}

}
