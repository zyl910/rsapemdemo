package rsapemdemo;

import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/** Java/.NET RSA demo, use pem key file (Java/.NET的RSA加解密演示项目，使用pem格式的密钥文件).
 * 
 * @author zyl910
 * @since 2017-10-27
 *
 */
public class RsaPemDemo {
	/** 帮助文本. */
	private String helpText = "Usage: rsapemdemo [options] srcfile\n\nFor example:\n\n    # encode by public key\n    rsapemdemo -e -l publickey.pem -o dstfile srcfile\n\n    # decode by private key\n    rsapemdemo -d -l privatekey.pem -o dstfile srcfile\n\nThe options:\n\n    -e        AES encryption and BASE64 encode.\n    -d        BASE64 decode and AES decryption.\n    -l [keyfile]  Load key file.\n    -o [outfile]  out file.\n";
	
	/** 是否为空.
	 * 
	 * @param str	字符串.
	 * @return	如果字符串为null或空串，则返回true，否则返回false.
	 */
	private static boolean isEmpty(String str) {
		return null==str || str.length()<=0;
	}

	/** 运行.
	 * 
	 * @param out	文本打印流.
	 * @param args	参数.
	 * @return	程序退出码.
	 */
	public void run(PrintStream out, String[] args) {
		boolean showhelp = true;
		// args
		String state = null;	// 状态.
		boolean isEncode = false;
		boolean isDecode = false;
		String fileKey = null;
		String fileOut = null;
		String fileSrc = null;
		int keybits = 2048;	// RSA密钥位数.
		for(String s: args) {
			if ("-e".equalsIgnoreCase(s)) {
				isEncode = true;
			} else if ("-e".equalsIgnoreCase(s)) {
				isDecode = true;
			} else if ("-l".equalsIgnoreCase(s)) {
				state = "l";
			} else if ("-o".equalsIgnoreCase(s)) {
				state = "o";
			} else {
				if ("l".equalsIgnoreCase(state)) {
					fileKey = s;
					state = null;
				} else if ("o".equalsIgnoreCase(state)) {
					fileOut = s;
					state = null;
				} else {
					fileSrc = s;
				}
			}
		}
		try{
			if (isEmpty(fileKey)) {
				out.println("No key file! Command need add `-l [keyfile]`.");
			} else if (isEmpty(fileOut)) {
				out.println("No out file! Command need add `-o [outfile]`.");
			} else if (isEmpty(fileSrc)) {
				out.println("No src file! Command need add `[srcfile]`.");
			} else if (isEncode!=false && isDecode!=false) {
				out.println("No set Encode/Encode! Command need add `-e`/`-d`.");
			} else if (isEncode) {
				showhelp = false;
				doEncode(out, keybits, fileKey, fileOut, fileSrc, null);
			} else if (isDecode) {
				showhelp = false;
				doDecode(out, keybits, fileKey, fileOut, fileSrc, null);
			}
		} catch (Exception e) {
			e.printStackTrace(out);
		}
		// do.
		if (showhelp) {
			out.println(helpText);
		}
	}

	/** 进行加密.
	 * 
	 * @param out	文本打印流.
	 * @param keybits	密钥位数.
	 * @param fileKey	密钥文件.
	 * @param fileOut	输出文件.
	 * @param fileSrc	源文件.
	 * @param exargs	扩展参数.
	 * @throws IOException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	private void doEncode(PrintStream out, int keybits, String fileKey, String fileOut,
			String fileSrc, Map<String, ?> exargs) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] bytesSrc = ZlRsaUtil.fileLoadBytes(fileSrc);
		String strDataKey = new String(ZlRsaUtil.fileLoadBytes(fileKey));
		Map<String, String> map = new HashMap<String, String>();
		byte[] bytesKey = ZlRsaUtil.pemDecode(strDataKey, map);
		String purposecode = map.get(ZlRsaUtil.PURPOSE_CODE);
		//out.println(bytesKey);
		// do.
		KeyFactory kf = KeyFactory.getInstance(ZlRsaUtil.RSA);
		Key key= null;
		//boolean isPrivate = false;
		if ("R".equals(purposecode)) {
			//isPrivate = true;
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytesKey);
			key = kf.generatePrivate(spec);
		} else {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(bytesKey);
			key = kf.generatePublic(spec);
		}
		out.println(String.format("key.getAlgorithm: %s", key.getAlgorithm()));
		out.println(String.format("key.getFormat: %s", key.getFormat()));
		Cipher cipher = Cipher.getInstance(ZlRsaUtil.RSA_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherBytes = cipher.doFinal(bytesSrc);
		byte[] cipherBase64 = Base64.encode(cipherBytes);
		ZlRsaUtil.fileSaveBytes(fileOut, cipherBase64, 0, cipherBase64.length);
		out.println(String.format("%s save done.", fileOut));
	}

	/** 进行解密.
	 * 
	 * @param out	文本打印流.
	 * @param keybits	密钥位数.
	 * @param fileKey	密钥文件.
	 * @param fileOut	输出文件.
	 * @param fileSrc	源文件.
	 * @param exargs	扩展参数.
	 */
	private void doDecode(PrintStream out, int keybits, String fileKey, String fileOut,
			String fileSrc, Object exargs) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		RsaPemDemo demo = new RsaPemDemo();
		demo.run(System.out, args);
	}
}
