package alertSystem;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
public class MailSend {
    /*
     * 需要
     * javamail.jar
     * session.jar
     * 如果import是使用Ctrl shift+O，↓這兩個import要修改成這樣，不然它會import到別的
     * import javax.mail.Authenticator;
     * import javax.mail.PasswordAuthentication;
     */
    public int sendMail(String to,String subject,String body,final String user,final String pwd,String from,String host,String port){

        /*
         * host
         * yahoo:"smtp.mail.yahoo.com"
         * outlook:"smtp-mail.outlook.com"
         * google:"smtp.gmail.com"
         */

        // 建立一個Properties來設定Properties
        Properties properties = System.getProperties();
        //設定傳輸協定為smtp
        properties.setProperty("mail.transport.protocol", "smtp");
        //設定mail Server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);//outlook587
        
        properties.put("mail.smtp.auth", "false");//需要驗證帳號密碼
        //SSL authentication
        properties.put("mail.smtp.ssl.enable", "false");
        //properties.put("mail.smtp.ssl.enable", "false");
        properties.put("mail.smtp.starttls.enable", "false");
        //System.setProperty("java.net.preferIPv4Stack" , "true");
       // 建立一個Session物件，並把properties傳進去
        /*
        Session mailSession = Session.getInstance(properties, new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                 return new PasswordAuthentication(user,pwd);
            }
        });  
        */
        Session mailSession = Session.getInstance(properties,null);
        try {
            //建立一個 MimeMessage object.
            MimeMessage message = new MimeMessage(mailSession);
            //設定郵件
            message.setFrom(new InternetAddress(from)); // 設定寄件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); // 設定收件人
            message.setSubject(MimeUtility.encodeWord(subject, "UTF-8", "B")); // 設定主旨

            // 宣告一個multipart , 它可以使內文有不同的段落，
            //使其可以用用來夾帶內文及檔案
            Multipart multipart = new MimeMultipart();
            //第一個段落
            BodyPart messageBodyPart = new MimeBodyPart(); //宣告一個BodyPart用以夾帶內文     
            messageBodyPart.setText(body);//設定內文
            multipart.addBodyPart(messageBodyPart); //把BodyPart加入Multipart  
            
            message.setContent(multipart); //設定eMultipart為messag的Content           
            Transport transport = mailSession.getTransport("smtp");
            //transport.connect(host ,user, pwd);
            transport.connect(host ,user, null);
            //傳送信件         
            transport.sendMessage(message,message.getAllRecipients());
            
            System.out.println("發送成功");
            //關閉連線
            transport.close();
            return 1;
            
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return 0;
        } catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
    }
}