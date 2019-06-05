package com.shanjin.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shanjin.msg.MessageProcessor;
import com.sun.mail.smtp.SMTPTransport;

/**
 * 发送邮件工具类
 * 
 * @author Revoke 2016.1.7
 *
 */
public class MailTools {
	private static Logger logger = LoggerFactory.getLogger(MailTools.class);
	 
	private static Properties mailProperties = null;

	//需要首先调用此方法，初始化
	public static boolean init() {

		InputStream in = MailTools.class
				.getResourceAsStream("/mail.properties");

		mailProperties = new Properties();

		try {
			mailProperties.load(in);
			return true;
		} catch (IOException e) {
			System.out.println("邮件配置文件不存在");
			return false;
		}

	}

	public static void sendMail(String title, String content) throws Exception {

		Session session = Session.getInstance(mailProperties);

	//	session.setDebug(true);

		Transport transport = session.getTransport("smtp");

		transport.connect(mailProperties.getProperty("mail.smtp.host"),
				mailProperties.getProperty("mail.from"),
				mailProperties.getProperty("mail.password"));

		Message message = createImageMail(session,
				mailProperties.getProperty("mail.from"),
				mailProperties.getProperty("mail.receiveList"), title, content);
		// transport.send(message);
		transport.sendMessage(message, message.getAllRecipients());

		transport.close();

	}

	private static MimeMessage createImageMail(Session session,
			String mailFrom, String mailTo, String title, String content)
			throws AddressException, MessagingException {

		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(mailFrom));
	
		
		message.setRecipients(Message.RecipientType.TO, getAdddress(mailTo));
				

		message.setSentDate(new Date(System.currentTimeMillis()));
		message.setSubject(title);

		message.setText(content);

		message.saveChanges();

		return message;
	}
	
	private static Address[] getAdddress(String mailTo){
		 String[] mailsAddress = mailTo.split(",");
		 Address[] result = new Address[mailsAddress.length];
		 
		 short i=0;
		 for(String address:mailsAddress){
			 try {
				Address temp = new InternetAddress(address);
				result[i++] = temp;
			} catch (AddressException e) {
				logger.warn("不正确的邮件地址，忽略", address);
			}
		 }
		 
		 return result;
		
	}

	public static void main(String[] args) throws Exception {

		MailTools.sendMail("test", "abcdefgg");
	}

}
