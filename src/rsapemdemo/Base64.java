package rsapemdemo;

/** Base64编解码.
 */
public final class Base64
{

    protected static final int BASELENGTH = 255;
    protected static final int LOOKUPLENGTH = 64;
    protected static final int TWENTYFOURBITGROUP = 24;
    protected static final int EIGHTBIT = 8;
    protected static final int SIXTEENBIT = 16;
    protected static final int SIXBIT = 6;
    protected static final int FOURBYTE = 4;
    protected static final int SIGN = -128;
    protected static final byte PAD = 61;
    protected static final boolean fDebug = false;
    private static byte base64Alphabet[];
    private static byte lookUpBase64Alphabet[];

    /** 是不是Base64数组.
     * 
     * @param binsrc	源二进制数据.
     * @return	若是便返回true，否则返回false.
     */
    public static synchronized boolean isArrayByteBase64(byte binsrc[])
    {
        return getDecodedDataLength(binsrc) >= 0;
    }

    /** 取得解码数据长度.
     * 
     * @param binsrc	源二进制数据.
     * @return	返回解码数据长度.
     */
    public static synchronized int getDecodedDataLength(byte binsrc[])
    {
        if(binsrc == null)
            return -1;
        if(binsrc.length == 0)
            return 0;
        byte binsrc2[] = null;
        if((binsrc2 = decode(binsrc)) == null)
            return -1;
        else
            return binsrc2.length;
    }

    public Base64()
    {
    }

    /** 编码.
     * 
     * @param binsrc	源二进制数据.
     * @return	返回Base64字节数组.
     */
    public static synchronized byte[] encode(byte binsrc[])
    {
        if(binsrc == null)
            return null;
        int i = binsrc.length * 8;
        int j = i % 24;
        int k = i / 24;
        byte binsrc2[] = null;
        if(j != 0)
            binsrc2 = new byte[(k + 1) * 4];
        else
            binsrc2 = new byte[k * 4];
        int l = 0;
        int i1 = 0;
        int j1 = 0;
        for(j1 = 0; j1 < k; j1++)
        {
            i1 = j1 * 3;
            byte byte5 = binsrc[i1];
            byte b1 = binsrc[i1 + 1];
            byte b3 = binsrc[i1 + 2];
            byte byte3 = (byte)(b1 & 0xf);
            byte byte0 = (byte)(byte5 & 0x3);
            l = j1 * 4;
            byte byte11 = (byte5 & 0xffffff80) == 0 ? (byte)(byte5 >> 2) : (byte)(byte5 >> 2 ^ 0xc0);
            byte byte14 = (b1 & 0xffffff80) == 0 ? (byte)(b1 >> 4) : (byte)(b1 >> 4 ^ 0xf0);
            byte byte16 = (b3 & 0xffffff80) == 0 ? (byte)(b3 >> 6) : (byte)(b3 >> 6 ^ 0xfc);
            binsrc2[l] = lookUpBase64Alphabet[byte11];
            binsrc2[l + 1] = lookUpBase64Alphabet[byte14 | byte0 << 4];
            binsrc2[l + 2] = lookUpBase64Alphabet[byte3 << 2 | byte16];
            binsrc2[l + 3] = lookUpBase64Alphabet[b3 & 0x3f];
        }

        i1 = j1 * 3;
        l = j1 * 4;
        if(j == 8)
        {
            byte byte6 = binsrc[i1];
            byte byte1 = (byte)(byte6 & 0x3);
            byte byte12 = (byte6 & 0xffffff80) == 0 ? (byte)(byte6 >> 2) : (byte)(byte6 >> 2 ^ 0xc0);
            binsrc2[l] = lookUpBase64Alphabet[byte12];
            binsrc2[l + 1] = lookUpBase64Alphabet[byte1 << 4];
            binsrc2[l + 2] = 61;
            binsrc2[l + 3] = 61;
        } else
        if(j == 16)
        {
            byte b0 = binsrc[i1];
            byte b2 = binsrc[i1 + 1];
            byte byte4 = (byte)(b2 & 0xf);
            byte byte2 = (byte)(b0 & 0x3);
            byte byte13 = (b0 & 0xffffff80) == 0 ? (byte)(b0 >> 2) : (byte)(b0 >> 2 ^ 0xc0);
            byte byte15 = (b2 & 0xffffff80) == 0 ? (byte)(b2 >> 4) : (byte)(b2 >> 4 ^ 0xf0);
            binsrc2[l] = lookUpBase64Alphabet[byte13];
            binsrc2[l + 1] = lookUpBase64Alphabet[byte15 | byte2 << 4];
            binsrc2[l + 2] = lookUpBase64Alphabet[byte4 << 2];
            binsrc2[l + 3] = 61;
        }
        return binsrc2;
    }

    protected static boolean isWhiteSpace(byte byte0)
    {
        return byte0 == 32 || byte0 == 13 || byte0 == 10 || byte0 == 9;
    }

    /** 移除Base64里的空白字符.
     * 
     * @param binsrc	Base64字节数组.
     * @return	返回移除空白字符后的Base64字节数组.
     */
    public static synchronized byte[] removeWhiteSpace(byte binsrc[])
    {
        if(binsrc == null)
            return null;
        int i = 0;
        int j = binsrc.length;
        for(int k = 0; k < j; k++)
            if(!isWhiteSpace(binsrc[k]))
                i++;

        if(i == j)
            return binsrc;
        byte binsrc2[] = new byte[i];
        int i1 = 0;
        for(int l = 0; l < j; l++)
            if(!isWhiteSpace(binsrc[l]))
                binsrc2[i1++] = binsrc[l];

        return binsrc2;
    }

    /** 是不是Base64字节.
     * 
     * @param byte0	源字节.
     * @return	返回是不是Base64.
     */
    public static boolean isBase64(byte byte0)
    {
        return isWhiteSpace(byte0) || isPad(byte0) || isData(byte0);
    }

    /** 是不是Base64字符串.
     * 
     * @param s	源字符串.
     * @return	返回是不是Base64.
     */
    public static boolean isBase64(String s)
    {
        if(s == null)
            return false;
        else
            return isArrayByteBase64(s.getBytes());
    }

    /** 解码.
     * 
     * @param binsrc	源Base64字节数组.
     * @return	返回解码后的二进制数据.
     */
    public static synchronized byte[] decode(byte binsrc[])
    {
        if(binsrc == null)
            return null;
        if(binsrc.length < 4)
            return null;
        byte binsrc2[] = removeWhiteSpace(binsrc);
        if(binsrc2.length % 4 != 0)
            return null;
        int i = binsrc2.length / 4;
        if(i == 0)
            return new byte[0];
        byte bindest[] = null;
        byte byte0 = 0;
        byte byte1 = 0;
        byte b0 = 0;
        byte b1 = 0;
        byte b2 = 0;
        byte b3 = 0;
        int j = 0;
        int k = 0;
        int l = 0;
        bindest = new byte[i * 3];
        for(; j < i - 1; j++)
        {
            if(!isData(b0 = binsrc2[l++]) || !isData(b1 = binsrc2[l++]) || !isData(b2 = binsrc2[l++]) || !isData(b3 = binsrc2[l++]))
                return null;
            byte0 = base64Alphabet[b0];
            byte1 = base64Alphabet[b1];
            byte byte2 = base64Alphabet[b2];
            byte byte5 = base64Alphabet[b3];
            bindest[k++] = (byte)(byte0 << 2 | byte1 >> 4);
            bindest[k++] = (byte)((byte1 & 0xf) << 4 | byte2 >> 2 & 0xf);
            bindest[k++] = (byte)(byte2 << 6 | byte5);
        }

        if(!isData(b0 = binsrc2[l++]) || !isData(b1 = binsrc2[l++]))
            return null;
        byte0 = base64Alphabet[b0];
        byte1 = base64Alphabet[b1];
        b2 = binsrc2[l++];
        b3 = binsrc2[l++];
        if(!isData(b2) || !isData(b3))
        {
            if(isPad(b2) && isPad(b3))
                if((byte1 & 0xf) != 0)
                {
                    return null;
                } else
                {
                    byte abyte3[] = new byte[j * 3 + 1];
                    System.arraycopy(bindest, 0, abyte3, 0, j * 3);
                    abyte3[k] = (byte)(byte0 << 2 | byte1 >> 4);
                    return abyte3;
                }
            if(!isPad(b2) && isPad(b3))
            {
                byte byte3 = base64Alphabet[b2];
                if((byte3 & 0x3) != 0)
                {
                    return null;
                } else
                {
                    byte abyte4[] = new byte[j * 3 + 2];
                    System.arraycopy(bindest, 0, abyte4, 0, j * 3);
                    abyte4[k++] = (byte)(byte0 << 2 | byte1 >> 4);
                    abyte4[k] = (byte)((byte1 & 0xf) << 4 | byte3 >> 2 & 0xf);
                    return abyte4;
                }
            } else
            {
                return null;
            }
        } else
        {
            byte byte4 = base64Alphabet[b2];
            byte byte6 = base64Alphabet[b3];
            bindest[k++] = (byte)(byte0 << 2 | byte1 >> 4);
            bindest[k++] = (byte)((byte1 & 0xf) << 4 | byte4 >> 2 & 0xf);
            bindest[k++] = (byte)(byte4 << 6 | byte6);
            return bindest;
        }
    }

    protected static boolean isData(byte byte0)
    {
        return base64Alphabet[byte0] != -1;
    }

    protected static boolean isPad(byte byte0)
    {
        return byte0 == 61;
    }

    static 
    {
        base64Alphabet = new byte[255];
        lookUpBase64Alphabet = new byte[64];
        for(int i = 0; i < 255; i++)
            base64Alphabet[i] = -1;

        for(int j = 90; j >= 65; j--)
            base64Alphabet[j] = (byte)(j - 65);

        for(int k = 122; k >= 97; k--)
            base64Alphabet[k] = (byte)((k - 97) + 26);

        for(int l = 57; l >= 48; l--)
            base64Alphabet[l] = (byte)((l - 48) + 52);

        base64Alphabet[43] = 62;
        base64Alphabet[47] = 63;
        for(int i1 = 0; i1 <= 25; i1++)
            lookUpBase64Alphabet[i1] = (byte)(65 + i1);

        int j1 = 26;
        for(int k1 = 0; j1 <= 51; k1++)
        {
            lookUpBase64Alphabet[j1] = (byte)(97 + k1);
            j1++;
        }

        int l1 = 52;
        for(int i2 = 0; l1 <= 61; i2++)
        {
            lookUpBase64Alphabet[l1] = (byte)(48 + i2);
            l1++;
        }

        lookUpBase64Alphabet[62] = 43;
        lookUpBase64Alphabet[63] = 47;
    }
}

