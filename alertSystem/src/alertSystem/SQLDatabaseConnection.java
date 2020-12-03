package alertSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import alertSystem.javaSqlString;
import alertSystem.MailSend;

public class SQLDatabaseConnection {

	// Connect to your database.
	// Replace server name, username, and password with your credentials
	public static void main(String[] args) {
		String fromEmail = args[0];
		String EmailUser= args[1];
		String EmailPwd= args[2];
		String EmailHost= args[3];
		String EmailPort= args[4];
		String managerMail=args[5];
		String type = args[6];// alert類型
		ArrayList<String> variableNumArr = new ArrayList();
		variableNumArr.add(args[7]);// sql變數
		if(args.length==13) {
			variableNumArr.add(args[8]);
			variableNumArr.add(args[9]);
			variableNumArr.add(args[10]);
			variableNumArr.add(args[11]);
			variableNumArr.add(args[12]);
		}
		// 參數
		FileWriter fw = null;
		checkfolder("log");
		try {
			Date now=new Date();
			SimpleDateFormat folderFmt=new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fileFmt=new SimpleDateFormat("yyyy-MM-dd-HHmm");
			checkfolder("log/"+folderFmt.format(now));
			fw = new FileWriter("log/"+folderFmt.format(now)+"/"+fileFmt.format(now)+"-alert.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 可以自動建立
		javaSqlString JavaSqlString = new javaSqlString();
		MailSend ms = new MailSend();
		MailContent mailContent = new MailContent();
		String connectionUrl = "jdbc:sqlserver://localhost;" + "database=VMINFO;" + "integratedSecurity=true;";
		// windows登入 需要先做設定

		ResultSet resultSet = null;

		try (Connection connection = DriverManager.getConnection(connectionUrl);
				Statement statement = connection.createStatement();) {

			// Create and execute a SELECT SQL statement.
			String selectSql = "";
			String SubjectVar = "";

			switch (type) {
			case "SnapshotOutdated":
				selectSql = JavaSqlString.getSnapshotOutdatedSql(variableNumArr.get(0));
				SubjectVar="VM";
				break;
			case "SnapshotOverSize":
				selectSql = JavaSqlString.getSnapshotOverSizeSql(variableNumArr.get(0));
				SubjectVar="VM";
				break;
			case "MemOverConsumed":
				selectSql = JavaSqlString.getMemOverConsumedSql(variableNumArr.get(0));
				SubjectVar="VM";
				break;
			case "DatastoreBelowSize":
				selectSql = JavaSqlString.getDatastoreBelowSizeSql(variableNumArr.get(0));
				SubjectVar="NAME";
				break;
			case "DatastoreOverProvision":
				selectSql = JavaSqlString.getDatastoreOverProvisionSql(variableNumArr.get(0));
				SubjectVar="NAME";
				break;
			case "HostCpuMemOverUsage":
				selectSql = JavaSqlString.getHostCpuMemOverUsageSql(variableNumArr.get(0));
				SubjectVar="Host";
				break;
			case "PartitionConsumed":
				selectSql = JavaSqlString.getPartitionConsumedSql(variableNumArr);
				SubjectVar="VM";
				break;
			}
			System.out.println(selectSql);
			resultSet = statement.executeQuery(selectSql);

			// Print results from select statement
			while (resultSet.next()) {
				String toEmail="";
				String Subject="";
				String SubjectinnerVar=resultSet.getString(SubjectVar);
				if(SubjectVar.equals("VM")) {
					Subject=mailContent.getMailSubject(type, SubjectinnerVar, resultSet.getString("email"),resultSet);
				}
				else {
					Subject=mailContent.getMailSubject(type, SubjectinnerVar, "",resultSet);
				}
				String body=mailContent.getMailBody(type,resultSet,variableNumArr);
				System.out.println(Subject+"|"+body);
				try {
					fw.write(Subject+"\r\n"+body);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(SubjectVar.equals("VM")&&resultSet.getString("email")!=null&&!resultSet.getString("email").equals("")) {
					toEmail=resultSet.getString("email");
					System.out.println("send:"+toEmail);
					try {
						fw.write("\r\n send:"+toEmail);
					} catch (IOException e) {
						e.printStackTrace();
					}
					ms.sendMail(toEmail,Subject,body,EmailUser,EmailPwd,fromEmail,EmailHost,EmailPort);
					
					/*
					String[] email_arr = managerMail.split(";");
					for(int i=1;i<email_arr.length;i++) {
						System.out.println("send:"+email_arr[i]);
						try {
							fw.write("send:"+email_arr[i]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						ms.sendMail(email_arr[i],Subject,body,EmailUser,EmailPwd,fromEmail,EmailHost,EmailPort);
					}
					*/
				}
				else {
					String[] email_arr = managerMail.split(";");
					for(int i=0;i<email_arr.length;i++) {
						System.out.println("send:"+email_arr[i]);
						try {
							fw.write("\r\n send:"+email_arr[i]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						ms.sendMail(email_arr[i],Subject,body,EmailUser,EmailPwd,fromEmail,EmailHost,EmailPort);
					}	
				}
				//System.out.println(resultSet.getString("VM") + " " + resultSet.getString("owner_mail_addr"));
			}
		} catch (SQLException e) {
			String Subject="";
			switch (type) {
			case "SnapshotOutdated":
				Subject="通知:alert_snapshot_outdated程式執行失敗";
				break;
			case "SnapshotOverSize":
				Subject="通知:alert_snapshot_over_size程式執行失敗";
				break;
			case "MemOverConsumed":
				Subject="通知:alert_mem_over_consumed程式執行失敗";
				break;
			case "DatastoreBelowSize":
				Subject="通知:alert_datastore_below_size程式執行失敗";
				break;
			case "DatastoreOverProvision":
				Subject="通知:alert_datastore_over_provision程式執行失敗";
				break;
			case "HostCpuMemOverUsage":
				Subject="通知:alert_host_cpu_mem_over_usage程式執行失敗";
				break;
			case "PartitionConsumed":
				Subject="通知:alert_partition_consumed程式執行失敗";
				break;
			}
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date current = new Date();
		    String exectime=sdFormat.format(current);
		    String body="=====================================================";
			body +="\r\n執行時間 :"+exectime+"\r\n";
			body+="=====================================================";
			String[] email_arr = managerMail.split(";");
			for(int i=0;i<email_arr.length;i++) {
				System.out.println("程式錯誤 send:"+email_arr[i]);
				try {
					fw.write("\r\n 程式錯誤 send:"+email_arr[i]);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				ms.sendMail(email_arr[i],Subject,body,EmailUser,EmailPwd,fromEmail,EmailHost,EmailPort);
			}
			//錯誤發信
			e.printStackTrace();
		}
		try {
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getFormateDate() {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddHH");
		Date current = new Date();
		return sdFormat.format(current);
	}
	
	public static void checkfolder(String folderPosition){
		File file =new File(folderPosition);    
		//如果資料夾不存在則建立    
		if  (!file .exists()  && !file .isDirectory())      
		{       
		    System.out.println("資料夾不存在");  
		    file .mkdir();    
		} else   
		{  
		    System.out.println("資料夾存在");  
		}  
	}
}
