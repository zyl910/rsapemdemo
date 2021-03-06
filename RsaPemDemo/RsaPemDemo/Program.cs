﻿using System;
using System.Collections.Generic;
using System.IO;
using System.Text;
using System.Collections;
using System.Security.Cryptography.X509Certificates;
using System.Security.Cryptography;

namespace RsaPemDemo {
	/// <summary>
	/// Java/.NET RSA demo, use pem key file (Java/.NET的RSA加解密演示项目，使用pem格式的密钥文件).
	/// </summary>
	class Program {
		/// <summary>
		/// 帮助文本.
		/// </summary>
		private const string helpText = "Usage: RsaPemDemo [options] srcfile\n\nFor example:\n\n    # encode by public key\n    rsapemdemo -e -l publickey.pem -o dstfile srcfile\n\n    # decode by private key\n    rsapemdemo -d -l privatekey.pem -o dstfile srcfile\n\nThe options:\n\n    -e        RSA encryption and BASE64 encode.\n    -d        BASE64 decode and RSA decryption.\n    -l [keyfile]  Load key file.\n    -o [outfile]  out file.\n";

		/// <summary>
		/// 运行.
		/// </summary>
		/// <param name="export">文本打印流.</param>
		/// <param name="args">参数.</param>
		public void run(TextWriter export, string[] args) {
			bool showhelp = true;
			// args
			string state = null;	// 状态.
			bool isEncode = false;
			bool isDecode = false;
			string fileKey = null;
			string fileOut = null;
			string fileSrc = null;
			int keysize = 0;	// RSA密钥位数. 0表示自动获取.
			foreach(string s in args) {
				if ("-e".Equals(s, StringComparison.OrdinalIgnoreCase)) {
					isEncode = true;
				} else if ("-d".Equals(s, StringComparison.OrdinalIgnoreCase)) {
					isDecode = true;
				} else if ("-l".Equals(s, StringComparison.OrdinalIgnoreCase)) {
					state = "l";
				} else if ("-o".Equals(s, StringComparison.OrdinalIgnoreCase)) {
					state = "o";
				} else {
					if ("l".Equals(state, StringComparison.OrdinalIgnoreCase)) {
						fileKey = s;
						state = null;
					} else if ("o".Equals(state, StringComparison.OrdinalIgnoreCase)) {
						fileOut = s;
						state = null;
					} else {
						fileSrc = s;
					}
				}
			}
			try{
				if (string.IsNullOrEmpty(fileKey)) {
					export.WriteLine("No key file! Command need add `-l [keyfile]`.");
				} else if (string.IsNullOrEmpty(fileOut)) {
					export.WriteLine("No out file! Command need add `-o [outfile]`.");
				} else if (string.IsNullOrEmpty(fileSrc)) {
					export.WriteLine("No src file! Command need add `[srcfile]`.");
				} else if (isEncode!=false && isDecode!=false) {
					export.WriteLine("No set Encode/Encode! Command need add `-e`/`-d`.");
				} else if (isEncode) {
					showhelp = false;
					doEncode(export, keysize, fileKey, fileOut, fileSrc, null);
				} else if (isDecode) {
					showhelp = false;
					doDecode(export, keysize, fileKey, fileOut, fileSrc, null);
				}
			} catch (Exception ex) {
				export.WriteLine(ex.ToString());
			}
			// do.
			if (showhelp) {
				export.WriteLine(helpText);
			}
		}

		/// <summary>
		/// 进行加密.
		/// </summary>
		/// <param name="export">文本打印流.</param>
		/// <param name="keysize">密钥位数. 为0表示自动获取.</param>
		/// <param name="fileKey">密钥文件.</param>
		/// <param name="fileOut">输出文件.</param>
		/// <param name="fileSrc">源文件.</param>
		/// <param name="exargs">扩展参数.</param>
		private void doEncode(TextWriter export, int keysize, string fileKey, string fileOut,
				string fileSrc, IDictionary exargs) {
			byte[] bytesSrc = File.ReadAllBytes(fileSrc);
			string strDataKey = File.ReadAllText(fileKey);
			string purposetext = null;
			char purposecode = '\0';
			byte[] bytesKey = ZlRsaUtil.PemUnpack(strDataKey, ref purposetext, ref purposecode);
			//export.WriteLine(bytesKey);
			// key.
			RSACryptoServiceProvider rsa;
			if ('R' == purposecode) {
				rsa = ZlRsaUtil.PemDecodePkcs8PrivateKey(bytesKey);	// try 
				if (null == rsa) {
					rsa = ZlRsaUtil.PemDecodeX509PrivateKey(bytesKey);
				}
			} else {	// 公钥或无法判断时, 均当成公钥处理.
				rsa = ZlRsaUtil.PemDecodePublicKey(bytesKey);
			}
			if (null == rsa) {
				export.WriteLine("Key decode fail!");
				return;
			}
			export.WriteLine(string.Format("KeyExchangeAlgorithm: {0}", rsa.KeyExchangeAlgorithm));
			export.WriteLine(string.Format("KeySize: {0}", rsa.KeySize));
			// encrypt.
			byte[] cipherBytes = ZlRsaUtil.Encrypt(rsa, bytesSrc);
			string cipherBase64 = Convert.ToBase64String(cipherBytes);
			File.WriteAllText(fileOut, cipherBase64);
			export.WriteLine(string.Format("{0} save done.", fileOut));
		}

		/// <summary>
		/// 进行解密.
		/// </summary>
		/// <param name="export">文本打印流.</param>
		/// <param name="keysize">密钥位数. 为0表示自动获取.</param>
		/// <param name="fileKey">密钥文件.</param>
		/// <param name="fileOut">输出文件.</param>
		/// <param name="fileSrc">源文件.</param>
		/// <param name="exargs">扩展参数.</param>
		private void doDecode(TextWriter export, int keysize, string fileKey, string fileOut,
				string fileSrc, IDictionary exargs) {
			String bytesSrcB64Src = File.ReadAllText(fileSrc);
			byte[] bytesSrc = Convert.FromBase64String(bytesSrcB64Src);
			string strDataKey = File.ReadAllText(fileKey);
			string purposetext = null;
			char purposecode = '\0';
			byte[] bytesKey = ZlRsaUtil.PemUnpack(strDataKey, ref purposetext, ref purposecode);
			//export.WriteLine(bytesKey);
			// key.
			RSACryptoServiceProvider rsa;
			if ('R' == purposecode) {
				rsa = ZlRsaUtil.PemDecodePkcs8PrivateKey(bytesKey);	// try 
				if (null == rsa) {
					rsa = ZlRsaUtil.PemDecodeX509PrivateKey(bytesKey);
				}
			} else {	// 公钥或无法判断时, 均当成公钥处理.
				rsa = ZlRsaUtil.PemDecodePublicKey(bytesKey);
			}
			if (null == rsa) {
				export.WriteLine("Key decode fail!");
				return;
			}
			export.WriteLine(string.Format("KeyExchangeAlgorithm: {0}", rsa.KeyExchangeAlgorithm));
			export.WriteLine(string.Format("KeySize: {0}", rsa.KeySize));
			// encryption.
			byte[] cipherBytes = ZlRsaUtil.Decrypt(rsa, bytesSrc);
			File.WriteAllBytes(fileOut, cipherBytes);
			export.WriteLine(string.Format("{0} save done.", fileOut));
		}

		static void Main(string[] args) {
			Program demo = new Program();
			demo.run(Console.Out, args);
		}
	}
}
