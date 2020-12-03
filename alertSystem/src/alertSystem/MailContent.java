package alertSystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MailContent {
	public String getMailSubject(String type, String SubjectVar, String email ,ResultSet resultSet) {
		String Subject = "";
		String VMalert = "通知:虛擬機器  ";
		if(type.equals("DatastoreBelowSize")||type.equals("DatastoreOverProvision")) {
			VMalert = "通知:Datastore";
		}
		String noEmailStr = "\t\t,負責人員mail不存在\t\t";
		switch (type) {
		case "SnapshotOutdated":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "快照過久" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "快照過久請立即處理";
			}
			break;

		case "SnapshotOverSize":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "快照過大" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "快照過大請立即處理 ";
			}
			break;
		case "MemOverConsumed":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "記憶體使用比例過高" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "記憶體使用比例過高";
			}
			break;
		case "DatastoreBelowSize":
			Subject = VMalert +"["+SubjectVar+"]"+"剩餘空間過低";
			break;
		case "DatastoreOverProvision":
			Subject = VMalert +"["+ SubjectVar+"]"+ "已配置比例過高";
			break;
		case "HostCpuMemOverUsage":
		    Subject = VMalert +"["+SubjectVar+"]"+ "CPU或記憶體使用率過高";
			break;
		case "PartitionConsumed":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "Partition剩餘空間過低" + noEmailStr;
			} else {
				try {
					Subject = VMalert + SubjectVar + "的\t" +resultSet.getString("Disk")+"\t剩餘空間(\t"+resultSet.getString("Free_MB")+"\tMB)請立即處理.";
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
		return Subject;
	}

	public String getMailBody(String type, ResultSet resultSet,ArrayList<String> variableNumArr) {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date current = new Date();
		int rowNumWithoutTime=0;
		String exectime = sdFormat.format(current);
		String[] TitleArr= new String[10];
		String[] StrArr= new String[10];
		String excontent = "觸發規則 : ";
		try {
			switch (type) {
			case "SnapshotOutdated":
				rowNumWithoutTime=4;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "虛擬機器名稱 :";
				TitleArr[2] = "負 責  人 員 :";
				TitleArr[3] = "快照建立時間 :";
				TitleArr[4] = "虛擬機器位置  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("DATE_TIME");
				StrArr[4] = resultSet.getString("VI_SDK_Server");
				excontent+="快照超過 "+variableNumArr.get(0)+"日";
				break;
			case "SnapshotOverSize":
				rowNumWithoutTime=4;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "虛擬機器名稱 :";
				TitleArr[2] = "負 責  人 員 :";
				TitleArr[3] = "快照佔用空間 :";
				TitleArr[4] = "虛擬機器位置  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("Size_vmsn_MB")+"GB";
				StrArr[4] = resultSet.getString("VI_SDK_Server");
				excontent+="快照佔用空間高於 "+variableNumArr.get(0)+"GB";
				break;
			case "MemOverConsumed":
				rowNumWithoutTime=5;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "虛擬機器名稱 :";
				TitleArr[2] = "虛擬機器位置 :";
				TitleArr[3] = "負 責  人 員 :";
				TitleArr[4] = "已使用的記憶體 :";
				TitleArr[5] = "配置的記憶體  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("VI_SDK_Server");
				StrArr[3] = resultSet.getString("loginname");
				StrArr[4] = resultSet.getString("Consumed")+"MB";
				StrArr[5] = resultSet.getString("Size_MB")+"MB";
				excontent+="記憶體實際使用與配置比例高於 "+variableNumArr.get(0)+"%";
				break;
			case "DatastoreBelowSize":
				rowNumWithoutTime=3;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "Datastore名稱  :";
				TitleArr[2] = "Datastore剩餘空間 :";
				TitleArr[3] = "VC:";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("NAME");
				StrArr[2] = resultSet.getString("Free_MB")+"MB";
				StrArr[3] = resultSet.getString("VI_SDK_Server");
				excontent+="Datastore剩餘空間低於 "+variableNumArr.get(0)+"MB";
				break;
			case "DatastoreOverProvision":
				rowNumWithoutTime=3;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "Datastore名稱 :";
				TitleArr[2] = "Datastore已配置空間 :";
				TitleArr[3] = "Datastore容量  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("NAME");
				StrArr[2] = resultSet.getString("provisioned_MB")+"MB";
				StrArr[3] = resultSet.getString("capacity_MB")+"MB";
				excontent+="Datastore已配置空間超過容量的 "+variableNumArr.get(0)+"%";
				break;
			case "HostCpuMemOverUsage":
				rowNumWithoutTime=3;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "Host名稱 :";
				TitleArr[2] = "CPU使用率 :";
				TitleArr[3] = "記憶體使用率 :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("Host");
				StrArr[2] = resultSet.getString("cpu_usage_percent")+"%";
				StrArr[3] = resultSet.getString("memory_usage_percent")+"%";
				excontent+="CPU或記憶體使用率超過 "+variableNumArr.get(0)+"%";
				break;
			case "PartitionConsumed":
				rowNumWithoutTime=8;
				TitleArr[0] = "執行時間 :";
				TitleArr[1] = "VM名稱  :";
				TitleArr[2] = "負 責  人 員  :";
				TitleArr[3] = "虛擬機器位置  :";
				TitleArr[4] = "Disk :";
				TitleArr[5] = "分割區容量 :";
				TitleArr[6] = "分割區已使用空間 :";
				TitleArr[7] = "分割區剩餘空間 :";
				TitleArr[8] = "分割區剩餘空間占比 :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("VI_SDK_Server");
				StrArr[4] = resultSet.getString("Disk");
				StrArr[5] = resultSet.getString("Capacity_MB")+" MB";
				StrArr[6] = resultSet.getString("Consumed_MB")+" MB";
				StrArr[7] = resultSet.getString("Free_MB")+" MB";
				StrArr[8] = resultSet.getString("Free_percent")+ " %";
				excontent+="(分割區大於 "+variableNumArr.get(0)+"GB,剩餘空間低於 "+variableNumArr.get(1)+"MB)或(分割區大於"+variableNumArr.get(2)+"GB時，且分割區代號為『C:\\』時，剩餘空間低於 "+variableNumArr.get(3)+"MB)或(分割區低於 "+variableNumArr.get(4)+"GB 時，且剩餘空間低於分割區容量之"+variableNumArr.get(5)+"％)";
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String body = "=====================================================\r\n";
		for(int i=0;i<=rowNumWithoutTime;i++) {
			body += TitleArr[i] + StrArr[i] + "\r\n";
		}
		body += "=====================================================";
		if (!excontent.equals("")) {
			body += "\r\n" + excontent;
		}
		return body;
	}
}
