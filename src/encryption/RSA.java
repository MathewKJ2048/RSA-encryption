package encryption;

import java.math.BigInteger;
import java.util.Scanner;
public class RSA
{
    //mathematics
    public static BigInteger gcd(BigInteger a,BigInteger b)
    {
        if(a.compareTo(BigInteger.valueOf(1)) == 0 || b.compareTo(BigInteger.valueOf(1)) == 0)
        {
            return BigInteger.valueOf(1);
        }
        else
        {
            BigInteger max = a.compareTo(b)>0 ? a : b;
            BigInteger min = a.compareTo(b)>0 ? b : a;
            if(max.remainder(min).compareTo(BigInteger.valueOf(0)) == 0)return min;
            return gcd(min,max.remainder(min));
        }
    }    
    public static BigInteger totient(BigInteger p[])//p = array of distinct primes
    {
        BigInteger t = new BigInteger("1");
        for(int i=0;i<p.length;i++)
        {
            t=t.multiply(p[i].subtract(BigInteger.valueOf(1)));
        }     
        return t;
    }
    
    //key generation and usage
    public static BigInteger encrypt(BigInteger data, BigInteger key, BigInteger n)
    {
        return data.modPow(key,n);
    }
    public static BigInteger get_private_key(BigInteger p[], BigInteger public_key)
    {
        BigInteger private_key = new BigInteger("-1");
        BigInteger phi = totient(p);
        BigInteger product = new BigInteger("1");
        if(gcd(public_key,phi).compareTo(BigInteger.valueOf(1)) != 0)
        {
            return private_key;
        }
        while(private_key.compareTo(BigInteger.valueOf(-1)) == 0)
        {
            product = product.add(phi);
            if(product.remainder(public_key).compareTo(BigInteger.valueOf(0)) == 0)
            {
                private_key = product.divide(public_key);
            }
        }
        return private_key;
    }
}
