package rsapemdemo;

import java.io.PrintStream;

public class RsaPemDemo {
	/** 帮助文本. */
	private String helpText = "Usage: rsapemdemo [options] srcfile\n\nFor example:\n\n    # encode by public key\n    rsapemdemo -e -l publickey.pem -o dstfile srcfile\n\n    # decode by private key\n    rsapemdemo -d -l privatekey.pem -o dstfile srcfile\n\nThe options:\n\n    -e        AES encryption and BASE64 encode.\n    -d        BASE64 decode and AES decryption.\n    -l [keyfile]  Load key file.\n    -o [outfile]  out file.\n";

	/** 运行.
	 * 
	 * @param out	文本打印流.
	 * @param args	参数.
	 * @return	程序退出码.
	 */
	public void run(PrintStream out, String[] args) {
		boolean showhelp = true;
		if (showhelp) {
			out.println(helpText);
		}
	}

	public static void main(String[] args) {
		RsaPemDemo demo = new RsaPemDemo();
		demo.run(System.out, args);
	}
}
