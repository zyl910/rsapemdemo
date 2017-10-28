# rsapemdemo
Java/.NET RSA demo, use pem key file (Java/.NET的RSA加解密演示项目，使用pem格式的密钥文件)

path:

- `data`: key file, test data.
- `src`: Java project source.
- `RsaPemDemo`: .NET project source.

RSA info:
```
RSA/ECB/PKCS1Padding
bits: 2024
key encode: PKCS#8 pem
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

    -e        AES encryption and BASE64 encode.
    -d        BASE64 decode and AES decryption.
    -l [keyfile]  Load key file.
    -o [outfile]  out file.

```

Sample:

```
rsapemdemo -e -l "E:\svn\20150630_dxt\workspace_dxt_my2\rsapemdemo\data\public1.pem" -o "E:\svn\20150630_dxt\workspace_dxt_my2\rsapemdemo\data\src1_out.log" "E:\svn\20150630_dxt\workspace_dxt_my2\rsapemdemo\data\src1.txt"
```

## Reference documentation

http://web.chacuo.net/netrsakeypair

