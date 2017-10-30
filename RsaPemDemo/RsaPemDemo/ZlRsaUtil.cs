using System;
using System.Collections.Generic;
using System.Text;

namespace RsaPemDemo {
	/// <summary>
	/// RSA算法工具.
	/// </summary>
	public class ZlRsaUtil {

		/// <summary>
		/// PEM密钥文件解码.
		/// </summary>
		/// <param name="data">源数据.</param>
		/// <param name="purposetext">用途文本. 如返回“BEGIN PUBLIC KEY”中的“PUBLIC KEY”.</param>
		/// <param name="purposecode">用途代码. R私钥， U公钥. 若无法识别，便保持原值.</param>
		/// <returns>返回解码后后纯密钥数据.</returns>
		/// <exception cref="System.ArgumentNullException">data is empty, or data body is empty.</exception>
		/// <exception cref="System.FormatException">data body is not BASE64.</exception>
		public static byte[] PemDecode(String data, ref string purposetext, ref char purposecode) {
			byte[] rt = null;
			const string SIGN_BEGIN = "-BEGIN";
			const string SIGN_END = "-END";
			if (String.IsNullOrEmpty(data)) throw new ArgumentNullException("data", "data is empty!");
			int datelen = data.Length;
			// find begin.
			int bodyPos = 0;	// 主体内容开始的地方.
			int beginPos = data.IndexOf(SIGN_BEGIN, StringComparison.OrdinalIgnoreCase);
			if (beginPos>=0) {
				// 向后查找换行符后的首个字节.
				bool isFound = false;
				bool hadNewline = false;	// 已遇到过换行符号.
				bool hyphenHad = false;	// 已遇到过“-”符号.
				bool hyphenDone = false;	// 已成功获取了右侧“-”的范围.
				int p = beginPos + SIGN_BEGIN.Length;
				int hyphenStart = p;	// 右侧“-”的开始位置.
				int hyphenEnd = hyphenStart;	// 右侧“-”的结束位置. 即最后一个“-”字符的位置+1.
				while(p<datelen) {
					char ch = data[p];
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
				if (hyphenDone) {
					int start = beginPos + SIGN_BEGIN.Length;
					purposetext = data.Substring(start, hyphenStart - start).Trim();
					string purposetextUp = purposetext.ToUpperInvariant();
					if (purposetextUp.IndexOf("PRIVATE")>=0) {
						purposecode = 'R';
					} else if (purposetextUp.IndexOf("PUBLIC") >= 0) {
						purposecode = 'U';
					}
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
			int endPos = data.IndexOf(SIGN_END, bodyPos);
			if (endPos>=0) {
				// 向前查找换行符前的首个字节.
				bool isFound = false;
				bool hadNewline = false;
				int p = endPos-1;
				while(p >= bodyPos) {
					char ch = data[p];
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
			string body = data.Substring(bodyPos, bodyEnd - bodyPos).Trim();
			// Decode BASE64.
			if (String.IsNullOrEmpty(body)) throw new ArgumentNullException("data", "data body is empty!");
			rt = Convert.FromBase64String(body);
			return rt;
		}

	}
}
