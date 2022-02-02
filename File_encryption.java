import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;
public class File_encryption
{
    //String encryption/decryption
    public static String encrypt(String message, BigInteger key, BigInteger n, Tracker t)//gives message encrypted as chunks with each chunk separated by '\n'
    {
        String encrypted_message = "";
        String s = "";
        int length = message.length();
        int limit = get_char_limit(n);
        for(int i=0;i<length;i++)
        {
            s = s+message.charAt(i);
            if(s.length() == limit)
            {
                encrypted_message += RSA.encrypt(process(s),key,n).toString() + "\n";
                s = "";
            }
            if(t!=null)t.update(i);
        }
        encrypted_message += RSA.encrypt(process(s),key,n).toString() + "\n";
        return encrypted_message;
    }
    public static String decrypt(String encrypted_message, BigInteger key, BigInteger n, Tracker t)
    {
        String message = "";
        String s = "0";
        int length = encrypted_message.length();
        for(int i=0;i<length;i++)
        {
            if(encrypted_message.charAt(i) == '\n')
            {
                message += process(RSA.encrypt(new BigInteger(s),key,n));
                s = "0";
            }
            else
            {
                s = s+encrypted_message.charAt(i);
            }
            if(t!=null)t.update(i);
        }
        return message;
    }
    
    //String/BigInteger processing
    public static int get_char_limit(BigInteger n)//returns maximum string length for encryption, original message broken into chunks of this size
    {
        int limit = -1;
        BigInteger test = new BigInteger("1");
        while(test.compareTo(n)<0)
        {
            test = test.multiply(BigInteger.valueOf(256));
            limit+=1;
        }
        return limit;
    }
    public static BigInteger process(String s)//characters' ASCII values used as digits in a number with base 256
    {
        BigInteger b = new BigInteger("0");
        BigInteger p = new BigInteger("1");
        for(int i=0;i<s.length();i++)
        {
            b=b.add(BigInteger.valueOf(((int)s.charAt(i))%256 + 1).multiply(p));
            p=p.multiply(BigInteger.valueOf(256));
        }
        return b;
    }
    public static String process(BigInteger b)//recovers string from associated number
    {
        String s = "";
        while(b.compareTo(BigInteger.valueOf(0)) != 0)
        {
            int det = b.remainder(BigInteger.valueOf(256)).intValue();
            det-=1;
            det+=256;
            det%=256;
            b = b.subtract(BigInteger.valueOf(det));
            s = s+((char)det);
            b = b.divide(BigInteger.valueOf(256));
        }
        return s;
    }
}
