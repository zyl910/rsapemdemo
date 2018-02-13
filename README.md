# rsapemdemo
Java/.NET RSA demo, use pem key file (Java/.NET的RSA加解密演示项目，使用pem格式的密钥文件)

path:

- `data`: key file, test data.
- `src`: Java project source.
- `RsaPemDemo`: .NET project source.

RSA info:
```
Private key encoded: PKCS#8 pem	// PKCS8EncodedKeySpec
Public key encoded: X.509 pem	// X509EncodedKeySpec
RSA/ECB/PKCS1Padding
bits: 2024
```

## Command

rsapemdemo command:

```
Usage: rsapemdemo [options] srcfile

For example:

    # encode by public key
    rsapemdemo -e -l publickey.pem -o dstfile srcfile

    # decode by private key
    rsapemdemo -d -l privatekey.pem -o dstfile srcfile

The options:

    -e        RSA encryption and BASE64 encode.
    -d        BASE64 decode and RSA decryption.
    -l [keyfile]  Load key file.
    -o [outfile]  out file.

```

Sample:

```
rsapemdemo -e -l "E:\rsapemdemo\data\public1.pem" -o "E:\rsapemdemo\data\src1_pub.log" "E:\rsapemdemo\data\src1.txt"
rsapemdemo -e -l "E:\rsapemdemo\data\private1.pem" -o "E:\rsapemdemo\data\src1_pri.log" "E:\rsapemdemo\data\src1.txt"

rsapemdemo -d -l "E:\rsapemdemo\data\public1.pem" -o "E:\rsapemdemo\data\src1_pri_d.log" "E:\rsapemdemo\data\src1_pri.log"
rsapemdemo -d -l "E:\rsapemdemo\data\private1.pem" -o "E:\rsapemdemo\data\src1_pub_d.log" "E:\rsapemdemo\data\src1_pub.log"
```

## Tip

Tip:

* .NET 的RSA，仅支持公钥加密、私钥解密。若用私钥加密，则仍是返回公钥加密结果。若用公钥解密，会出现 `System.Security.Cryptography.CryptographicException: 不正确的项。` 异常.


## Change history (变更日志)

### [2018-02-13] v1.0: http://www.cnblogs.com/zyl910/p/rsapemdemo_cs_java.html

* Release v1.0 (发布1.0版).

## Reference documentation

* 《在线生成生成RSA密钥对》. http://web.chacuo.net/netrsakeypair
* Michel I. Gallant Ph.D.《RSA Public, Private, and PKCS #8 key parser》（OpenSSLKey.cs）. http://www.jensign.com/opensslkey/
* 写代码的二妹《PHP，C# 和JAVARSA签名及验签》. http://www.cnblogs.com/frankyou/p/5993756.html
* FrankYou《C# RSA 分段加解密》. http://www.cnblogs.com/frankyou/p/5993756.html
* FrankYou《Java RSA 分段加解密》. http://www.cnblogs.com/frankyou/p/5993685.html

