package alertSystem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class javaSqlString {
	Date date= new Date();
	SimpleDateFormat Dateformat = new SimpleDateFormat("yyyy-MM-dd");
	String timeStr = Dateformat.format(date);
	public String getSnapshotOutdatedSql(String variableNum) {
		String sql="select A.VM,A.DATE_TIME,A.VI_SDK_Server,B.loginname,B.email ";
		sql+="FROM VMINFO.dbo.vSnapshot_tb A ";
		sql+="left join dblink_UHD01P.rackman.dbo.ServerView B ";
		sql+="ON A.VM=B.devname ";
		sql+="where datediff(day, A.date_time,'"+timeStr+"') > "+variableNum;
		return sql;
	}
	
	public String getSnapshotOverSizeSql(String variableNum) {
		String sql="select A.VM,A.DATE_TIME,CAST(A.SIZE_VMSN_MB as decimal(20,3))/1000 as SIZE_VMSN_MB,A.VI_SDK_Server,B.loginname,B.email ";
		sql+="FROM VMINFO.dbo.vSnapshot_tb A ";
		sql+="left join dblink_UHD01P.rackman.dbo.ServerView B ";
		sql+="ON A.VM = B.devname ";
		sql+="WHERE CAST(A.SIZE_VMSN_MB as decimal(20,3))/1000 > "+variableNum;
		return sql;
	}
	
	public String getMemOverConsumedSql(String variableNum) {
		String sql="select A.VM,A.VI_SDK_Server,A.Consumed,A.Size_MB,B.loginname,B.email ";
		sql+="FROM VMINFO.dbo.vMemory_tb A ";
		sql+="left join dblink_UHD01P.rackman.dbo.ServerView B ";
		sql+="ON A.VM=B.devname ";
		sql+="WHERE (convert(float,A.Consumed)/convert(float,A.Size_MB))*100 > "+variableNum;
		return sql;
	}
	
	public String getDatastoreBelowSizeSql(String variableNum) {
		String sql="select A.NAME,A.Free_MB,A.VI_SDK_Server ";
		sql+="FROM VMINFO.dbo.vDatastore_tb A ";
		sql+="WHERE (A.capacity_MB>=700000 and A.free_mb < "+variableNum+") or (A.capacity_MB<700000 and A.free_mb <30000)";
		return sql;
	}
	
	public String getDatastoreOverProvisionSql(String variableNum) {
		String sql="select A.NAME,A.provisioned_MB,A.capacity_MB ";
		sql+="FROM VMINFO.dbo.vDatastore_tb A ";
		sql+="WHERE CAST(A.provisioned_MB AS decimal(20,3))/CAST(capacity_MB AS decimal(20,3))*100 > "+variableNum;
		return sql;
	}
	
	public String getHostCpuMemOverUsageSql(String variableNum) {
		String sql="select A.Host,A.cpu_usage_percent,A.memory_usage_percent ";
		sql+="FROM VMINFO.dbo.vHost_tb A ";
		sql+="WHERE A.cpu_usage_percent > "+variableNum+" or A.memory_usage_percent > "+variableNum;
		return sql;
	}
	
	public String getPartitionConsumedSql(ArrayList<String> variableNumArr) {
		String sql="select A.VM,A.VI_SDK_Server,A.Disk,A.Capacity_MB,A.Consumed_MB,A.Free_MB,A.Free_percent,B.loginname,B.email ";
		sql+="FROM VMINFO.dbo.vPartition_tb A ";
		sql+="left join dblink_UHD01P.rackman.dbo.ServerView B ";
		sql+="ON A.VM=B.devname ";
		sql+="WHERE ((CAST(A.Capacity_MB AS decimal(20,3))/1000.0 > "+variableNumArr.get(0)+"AND CAST(Free_MB AS decimal(20,3)) <"+variableNumArr.get(1)+") OR ";
		sql+="(CAST(A.Capacity_MB AS decimal(20,3))/1000.0 > "+variableNumArr.get(2)+" AND A.Disk = 'C:\\' AND CAST(A.FREE_MB as decimal(20,3)) <"+variableNumArr.get(3)+") OR ";
		sql+="(CAST(A.Capacity_MB AS decimal(20,3))/1000.0 < "+variableNumArr.get(4)+" AND CAST(A.FREE_percent as decimal(20,3)) <"+variableNumArr.get(5)+")) AND  (substring(trim(disk),1,1)<>'/' AND powerstate<>'poweredOff')";
		return sql;
	}
}
