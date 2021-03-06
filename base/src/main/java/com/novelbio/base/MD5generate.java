package com.novelbio.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.validator.Field;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.base64.Base64Encoder;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobCreatorUtils;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.HibernateAccessor;

import com.novelbio.base.fileOperate.FileHadoop;
import com.novelbio.base.fileOperate.FileOperate;

/**
 * MD5的算法在RFC1321 中定义 在RFC 1321中，给出了Test suite用来检验你的实现是否正确： MD5 ("") =
 * d41d8cd98f00b204e9800998ecf8427e MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72 MD5 ("message digest") =
 * f96b697d7cb7938d525a2f31aaf161d0 MD5 ("abcdefghijklmnopqrstuvwxyz") =
 * c3fcd3d76192e4007dfb496cca67e13b
 * 
 * @author haogj
 * 
 *         传入参数：一个字节数组 传出参数：字节数组的 MD5 结果字符串
 */
public class MD5generate {
	private static Logger logger = Logger.getLogger(MD5generate.class);

	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6','7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
     
	protected static MessageDigest messageDigest = null;  
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");  
		} catch (NoSuchAlgorithmException nsaex) {  
			logger.error("初始化失败，MessageDigest不支持MD5!");  
			nsaex.printStackTrace();  
		}
	}
	
	public static void main(String[] args) throws Exception {
		String filePath = "/hdfs:/nbCloud/public/publicFile/deflate.js";
		if(FileHadoop.isHdfs(filePath))
			System.out.println(FileHadoop.convertToLocalPath(filePath));
	}
	/**
	 * 返回""表示出错
	 * @param fileName
	 * @return
	 */
	public static String getFileMD5String(String fileName) {
		String result = "";
		try {
			result = getFileMD5StringExp(fileName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	
	private static String getFileMD5StringExp(String fileName) throws IOException {
		File file = new File(fileName);
		FileInputStream in = new FileInputStream(file);  
		FileChannel ch = in.getChannel();  
		
		//700000000 bytes are about 670M  
		int maxSize=700000000;  
		
		long startPosition=0L;  
		long step=file.length()/maxSize;  
		
		if(step == 0) {  
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,file.length());
			messageDigest.update(byteBuffer);  
			return bufferToHex(messageDigest.digest());  
		}
         
		for(int i=0;i<step;i++) {
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,maxSize);  
			messageDigest.update(byteBuffer);  
			startPosition+=maxSize;  
		}
         
		if(startPosition==file.length()) {  
			return bufferToHex(messageDigest.digest());  
		}
   
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,file.length()-startPosition);  
		messageDigest.update(byteBuffer);  
		in.close();
		return bufferToHex(messageDigest.digest());  
	}
	
	/**
	 * 根据文件全路径获得全文件的md5值
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String getNBCFileRealMd5(String fileName) throws IOException {
		if(FileHadoop.isHdfs(fileName))
			fileName = FileHadoop.convertToLocalPath(fileName);
		File file = new File(fileName);
		FileInputStream in = new FileInputStream(file);  
		FileChannel ch = in.getChannel();  
		
		//700000000 bytes are about 670M  
		int maxSize=700000000;  
		
		long startPosition=0L;  
		long step=file.length()/maxSize;  
		
		if(step == 0) {  
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,file.length());
			messageDigest.update(byteBuffer);  
			return bufferToHex(messageDigest.digest());  
		}
         
		for(int i=0;i<step;i++) {
			MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,maxSize);  
			messageDigest.update(byteBuffer);  
			startPosition+=maxSize;  
		}
         
		if(startPosition==file.length()) {  
			return bufferToHex(messageDigest.digest());  
		}
   
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, startPosition,file.length()-startPosition);  
		messageDigest.update(byteBuffer);  
		in.close();
		return bufferToHex(messageDigest.digest()); 
	}
	/**
	 * 根据文件全路径得到指定最大流的1024*1024*5即为5M 值
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String getNBCFileMD5(String fileName) throws IOException {
		InputStream in = FileOperate.getInputStream(fileName);
		int maxSize = 1024*1024*5;//100;
		int fileLength = 0;
		if(in instanceof FSDataInputStream){
			fileLength = (int) ((FSDataInputStream) in).getFileLength();
		}else{
			fileLength = in.available();
		}
		maxSize = (maxSize > fileLength ? fileLength : maxSize);
		System.out.println(maxSize);
		byte[] b = new byte[maxSize];
		in.read(b);
		in.close();
		String str = Base64.encodeBase64String(b);
		return getMD5String("data:;base64," + str);  
	}
	
	public static String getMD5String(String s) {
		try {
			return getMD5String(s.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			return getMD5(s.getBytes());
		}
	}  
   
	public static String getMD5String(byte[] bytes) {  
		messageDigest.update(bytes);  
		return bufferToHex(messageDigest.digest());  
	}  
   
	private static String bufferToHex(byte bytes[]) {  
		return bufferToHex(bytes, 0, bytes.length);  
	}  
   
	private static String bufferToHex(byte bytes[], int m, int n) {  
		StringBuffer stringbuffer = new StringBuffer(2 * n);  
		int k = m + n;  
		for (int l = m; l < k; l++) {  
			appendHexPair(bytes[l], stringbuffer);  
		}  
		return stringbuffer.toString();  
	}  
   
	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
		char c0 = hexDigits[(bt & 0xf0) >> 4];  
		char c1 = hexDigits[bt & 0xf];  
		stringbuffer.append(c0);  
		stringbuffer.append(c1);  
	}  
    
	/**
	 * 只是看看的，这里面有一些注释写的比较清楚，对于如何解析MD5的结果
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
										// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
											// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
											// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
															// >>>
															// 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 提供一个MD5多次加密方法
	 */
	public static String getMD5String(String origString, int times) {
		String md5 = getMD5String(origString);
		for (int i = 0; i < times - 1; i++) {
			md5 = getMD5String(md5);
		}
		return getMD5String(md5);
	}
	/**
	 * 密码验证方法
	 */
	public static boolean verifyPassword(String inputStr, String MD5Code) {
		return getMD5String(inputStr).equals(MD5Code);
	}
	/**
	 * 多次加密时的密码验证方法
	 */
	public static boolean verifyPassword(String inputStr, String MD5Code, int times) {
		return getMD5String(inputStr, times).equals(MD5Code);
	}
}
