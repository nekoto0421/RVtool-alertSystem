package alertSystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MailContent {
	public String getMailSubject(String type, String SubjectVar, String email ,ResultSet resultSet) {
		String Subject = "";
		String VMalert = "�q��:��������  ";
		if(type.equals("DatastoreBelowSize")||type.equals("DatastoreOverProvision")) {
			VMalert = "�q��:Datastore";
		}
		String noEmailStr = "\t\t,�t�d�H��mail���s�b\t\t";
		switch (type) {
		case "SnapshotOutdated":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "�ַӹL�[" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "�ַӹL�[�ХߧY�B�z";
			}
			break;

		case "SnapshotOverSize":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "�ַӹL�j" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "�ַӹL�j�ХߧY�B�z ";
			}
			break;
		case "MemOverConsumed":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "�O����ϥΤ�ҹL��" + noEmailStr;
			} else {
				Subject = VMalert + SubjectVar + "�O����ϥΤ�ҹL��";
			}
			break;
		case "DatastoreBelowSize":
			Subject = VMalert +"["+SubjectVar+"]"+"�Ѿl�Ŷ��L�C";
			break;
		case "DatastoreOverProvision":
			Subject = VMalert +"["+ SubjectVar+"]"+ "�w�t�m��ҹL��";
			break;
		case "HostCpuMemOverUsage":
		    Subject = VMalert +"["+SubjectVar+"]"+ "CPU�ΰO����ϥβv�L��";
			break;
		case "PartitionConsumed":
			if (email == null || email.equals("")) {
				Subject = VMalert + SubjectVar + "Partition�Ѿl�Ŷ��L�C" + noEmailStr;
			} else {
				try {
					Subject = VMalert + SubjectVar + "��\t" +resultSet.getString("Disk")+"\t�Ѿl�Ŷ�(\t"+resultSet.getString("Free_MB")+"\tMB)�ХߧY�B�z.";
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
		String excontent = "Ĳ�o�W�h : ";
		try {
			switch (type) {
			case "SnapshotOutdated":
				rowNumWithoutTime=4;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "���������W�� :";
				TitleArr[2] = "�t �d  �H �� :";
				TitleArr[3] = "�ַӫإ߮ɶ� :";
				TitleArr[4] = "����������m  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("DATE_TIME");
				StrArr[4] = resultSet.getString("VI_SDK_Server");
				excontent+="�ַӶW�L "+variableNumArr.get(0)+"��";
				break;
			case "SnapshotOverSize":
				rowNumWithoutTime=4;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "���������W�� :";
				TitleArr[2] = "�t �d  �H �� :";
				TitleArr[3] = "�ַӦ��ΪŶ� :";
				TitleArr[4] = "����������m  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("Size_vmsn_MB")+"GB";
				StrArr[4] = resultSet.getString("VI_SDK_Server");
				excontent+="�ַӦ��ΪŶ����� "+variableNumArr.get(0)+"GB";
				break;
			case "MemOverConsumed":
				rowNumWithoutTime=5;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "���������W�� :";
				TitleArr[2] = "����������m :";
				TitleArr[3] = "�t �d  �H �� :";
				TitleArr[4] = "�w�ϥΪ��O���� :";
				TitleArr[5] = "�t�m���O����  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("VI_SDK_Server");
				StrArr[3] = resultSet.getString("loginname");
				StrArr[4] = resultSet.getString("Consumed")+"MB";
				StrArr[5] = resultSet.getString("Size_MB")+"MB";
				excontent+="�O�����ڨϥλP�t�m��Ұ��� "+variableNumArr.get(0)+"%";
				break;
			case "DatastoreBelowSize":
				rowNumWithoutTime=3;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "Datastore�W��  :";
				TitleArr[2] = "Datastore�Ѿl�Ŷ� :";
				TitleArr[3] = "VC:";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("NAME");
				StrArr[2] = resultSet.getString("Free_MB")+"MB";
				StrArr[3] = resultSet.getString("VI_SDK_Server");
				excontent+="Datastore�Ѿl�Ŷ��C�� "+variableNumArr.get(0)+"MB";
				break;
			case "DatastoreOverProvision":
				rowNumWithoutTime=3;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "Datastore�W�� :";
				TitleArr[2] = "Datastore�w�t�m�Ŷ� :";
				TitleArr[3] = "Datastore�e�q  :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("NAME");
				StrArr[2] = resultSet.getString("provisioned_MB")+"MB";
				StrArr[3] = resultSet.getString("capacity_MB")+"MB";
				excontent+="Datastore�w�t�m�Ŷ��W�L�e�q�� "+variableNumArr.get(0)+"%";
				break;
			case "HostCpuMemOverUsage":
				rowNumWithoutTime=3;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "Host�W�� :";
				TitleArr[2] = "CPU�ϥβv :";
				TitleArr[3] = "�O����ϥβv :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("Host");
				StrArr[2] = resultSet.getString("cpu_usage_percent")+"%";
				StrArr[3] = resultSet.getString("memory_usage_percent")+"%";
				excontent+="CPU�ΰO����ϥβv�W�L "+variableNumArr.get(0)+"%";
				break;
			case "PartitionConsumed":
				rowNumWithoutTime=8;
				TitleArr[0] = "����ɶ� :";
				TitleArr[1] = "VM�W��  :";
				TitleArr[2] = "�t �d  �H ��  :";
				TitleArr[3] = "����������m  :";
				TitleArr[4] = "Disk :";
				TitleArr[5] = "���ΰϮe�q :";
				TitleArr[6] = "���ΰϤw�ϥΪŶ� :";
				TitleArr[7] = "���ΰϳѾl�Ŷ� :";
				TitleArr[8] = "���ΰϳѾl�Ŷ��e�� :";
				StrArr[0] = exectime;
				StrArr[1] = resultSet.getString("VM");
				StrArr[2] = resultSet.getString("loginname");
				StrArr[3] = resultSet.getString("VI_SDK_Server");
				StrArr[4] = resultSet.getString("Disk");
				StrArr[5] = resultSet.getString("Capacity_MB")+" MB";
				StrArr[6] = resultSet.getString("Consumed_MB")+" MB";
				StrArr[7] = resultSet.getString("Free_MB")+" MB";
				StrArr[8] = resultSet.getString("Free_percent")+ " %";
				excontent+="(���ΰϤj�� "+variableNumArr.get(0)+"GB,�Ѿl�Ŷ��C�� "+variableNumArr.get(1)+"MB)��(���ΰϤj��"+variableNumArr.get(2)+"GB�ɡA�B���ΰϥN�����yC:\\�z�ɡA�Ѿl�Ŷ��C�� "+variableNumArr.get(3)+"MB)��(���ΰϧC�� "+variableNumArr.get(4)+"GB �ɡA�B�Ѿl�Ŷ��C����ΰϮe�q��"+variableNumArr.get(5)+"�H)";
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
