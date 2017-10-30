package rsapemdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/** RSA算法工具.
 * 
 * @author zyl910
 * @since 2017-10-27
 *
 */
public final class ZlRsaUtil {
	/** 用途文本. 如“BEGIN PUBLIC KEY”中的“PUBLIC KEY”. */
	final static String PURPOSE_TEXT = "PURPOSE_TEXT";
	/** 用途代码. R私钥， U公钥. */
	
	final static String PURPOSE_CODE = "PURPOSE_CODE";
	/**
	 * RSA .
	 */
	public final static String RSA = "RSA";
	
	/**
	 * 具体的 RSA 算法.
	 */
	public final static String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
	
	/** PEM解包.
	 * 
	 * <p>从PEM密钥数据中解包得到纯密钥数据. 即去掉BEGIN/END行，并作BASE64解码. 即去掉BEGIN/END行，并作BASE64解码. 若没有BEGIN/END, 则直接做BASE64解码.</p>
	 * 
	 * @param data	源数据.
	 * @param otherresult	其他返回值. 支持 PURPOSE_TEXT, PURPOSE_CODE。
	 * @return	返回解包后后纯密钥数据.
	 */
	public static byte[] PemUnpack(String data, Map<String, String> otherresult) {
		byte[] rt = null;
		final String SIGN_BEGIN = "-BEGIN";
		final String SIGN_END = "-END";
		int datelen = data.length();
		String purposetext = "";
		String purposecode = "";
		if (null!=otherresult) {
			purposetext = otherresult.get(PURPOSE_TEXT);
			purposecode = otherresult.get(PURPOSE_CODE);
			if (null==purposetext) purposetext= "";
			if (null==purposecode) purposecode= "";
		}
		// find begin.
		int bodyPos = 0;	// 主体内容开始的地方.
		int beginPos = data.indexOf(SIGN_BEGIN);
		if (beginPos>=0) {
			// 向后查找换行符后的首个字节.
			boolean isFound = false;
			boolean hadNewline = false;	// 已遇到过换行符号.
			boolean hyphenHad = false;	// 已遇到过“-”符号.
			boolean hyphenDone = false;	// 已成功获取了右侧“-”的范围.
			int p = beginPos + SIGN_BEGIN.length();
			int hyphenStart = p;	// 右侧“-”的开始位置.
			int hyphenEnd = hyphenStart;	// 右侧“-”的结束位置. 即最后一个“-”字符的位置+1.
			while(p<datelen) {
				char ch = data.charAt(p);
				// 查找右侧“-”的范围.
				if (!hyphenDone) {
					if (ch=='-') {
						if (!hyphenHad) {
							hyphenHad = true;
							hyphenStart = p;
							hyphenEnd = hyphenStart;
						}
					} else {
						if (hyphenHad) { // 无需“&& !hyphenDone”，因为外层判断了.
							hyphenDone = true;
							hyphenEnd = p;
						}
					}
				}
				// 向后查找换行符后的首个字节.
				if (ch=='\n' || ch=='\r') {
					hadNewline = true;
				} else {
					if (hadNewline) {
						// 找到了.
						bodyPos = p;
						isFound = true;
						break;
					}
				}
				// next.
				++p;
			}
			// purposetext
			if (hyphenDone && null!=otherresult) {
				purposetext = data.substring(beginPos + SIGN_BEGIN.length(), hyphenStart).trim();
				String purposetextUp = purposetext.toUpperCase();
				if (purposetextUp.indexOf("PRIVATE")>=0) {
					purposecode = "R";
				} else if (purposetextUp.indexOf("PUBLIC")>=0) {
					purposecode = "U";
				}
				otherresult.put(PURPOSE_TEXT, purposetext);
				otherresult.put(PURPOSE_CODE, purposecode);
			}
			// bodyPos.
			if (isFound) {
				//OK.
			} else if (hyphenDone) {
				// 以右侧右侧“-”的结束位置作为主体开始.
				bodyPos = hyphenEnd;
			} else {
				// 找不到结束位置，只能退出.
				return rt;
			}
		}
		// find end.
		int bodyEnd = datelen;	// 主体内容的结束位置. 即最后一个字符的位置+1.
		int endPos = data.indexOf(SIGN_END, bodyPos);
		if (endPos>=0) {
			// 向前查找换行符前的首个字节.
			boolean isFound = false;
			boolean hadNewline = false;
			int p = endPos-1;
			while(p >= bodyPos) {
				char ch = data.charAt(p);
				if (ch=='\n' || ch=='\r') {
					hadNewline = true;
				} else {
					if (hadNewline) {
						// 找到了.
						bodyEnd = p+1;
						break;
					}
				}
				// next.
				--p;
			}
			if (!isFound) {
				// 忽略.
			}
		}
		// get body.
		if (bodyPos>=bodyEnd) {
			return rt;
		}
		String body = data.substring(bodyPos, bodyEnd).trim();
		// Decode BASE64.
		rt = Base64.decode(body.getBytes());
		return rt;
	}

	// == File ==
	
	/** 加载文件中的所有字节.
	 * 
	 * @param filename	文件名.
	 * @return	返回文件内容的字节数组.
	 * @throws IOException IO异常.
	 */
	public static byte[] fileLoadBytes(String filename) throws IOException {
		byte[] rt = null;
        File file = new File(filename);  
        long fileSize = file.length();  
        if (fileSize > Integer.MAX_VALUE) {
        	throw new IOException(filename + " file too big...");
        }  
        FileInputStream fi = new FileInputStream(filename);
		try {
			rt = new byte[(int) fileSize];
			int offset = 0;  
			int numRead = 0;  
			while (offset < rt.length  
					&& (numRead = fi.read(rt, offset, rt.length - offset)) >= 0) {  
				offset += numRead;  
			}  
			// 确保所有数据均被读取  
			if (offset != rt.length) {  
				throw new IOException("Could not completely read file " + file.getName());  
			}  
		}finally{
			try {
				fi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return rt;
	}
	
	/** 将字节数组写入文件, 具有起始位置、长度参数.
	 * 
	 * @param filename	文件名.
	 * @param data	数据.
	 * @param dataOffset	数据起始.
	 * @param dataLen	数据长度.
	 * @throws IOException IO异常.
	 */
	public static void fileSaveBytes(String filename, byte[] data, int dataOffset, int dataLen) throws IOException {
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(filename);
			fos.write(data, dataOffset, dataLen);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
