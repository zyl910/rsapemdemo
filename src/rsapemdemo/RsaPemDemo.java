package rsapemdemo;

import java.io.PrintStream;
import java.util.Map;

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
			doEncode(keybits, fileKey, fileOut, fileSrc, null);
		} else if (isDecode) {
			showhelp = false;
			doDecode(keybits, fileKey, fileOut, fileSrc, null);
		}
		// do.
		if (showhelp) {
			out.println(helpText);
		}
	}

	/** 进行加密.
	 * 
	 * @param keybits	密钥位数.
	 * @param fileKey	密钥文件.
	 * @param fileOut	输出文件.
	 * @param fileSrc	源文件.
	 * @param exargs	扩展参数.
	 */
	private void doEncode(int keybits, String fileKey, String fileOut,
			String fileSrc, Map<String, ?> exargs) {
		// TODO Auto-generated method stub
		
	}

	/** 进行解密.
	 * 
	 * @param keybits	密钥位数.
	 * @param fileKey	密钥文件.
	 * @param fileOut	输出文件.
	 * @param fileSrc	源文件.
	 * @param exargs	扩展参数.
	 */
	private void doDecode(int keybits, String fileKey, String fileOut,
			String fileSrc, Object exargs) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		RsaPemDemo demo = new RsaPemDemo();
		demo.run(System.out, args);
	}
}
