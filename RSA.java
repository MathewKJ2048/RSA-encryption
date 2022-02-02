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
    //
    public static void test()
    {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter 2 primes:");
        BigInteger p[] = new BigInteger[2];
        p[0] = new BigInteger(sc.next());
        p[1] = new BigInteger(sc.next());
        BigInteger n = p[0].multiply(p[1]);
        System.out.println("n is: "+n.toString());
        System.out.println("totient is:" + totient(p).toString());
        //
        BigInteger private_key = new BigInteger("-1");
        BigInteger public_key = new BigInteger("1");
        do
        {
            System.out.println("Enter public key:");
            public_key = new BigInteger(sc.next());
            private_key = get_private_key(p,public_key);
            System.out.println("private key is: "+private_key.toString());
        }while(private_key.compareTo(BigInteger.valueOf(-1)) == 0);
        //
        while(true)
        {
            System.out.println("Enter message:");
            BigInteger message = new BigInteger(sc.next());
            if(message.compareTo(BigInteger.valueOf(-1)) == 0)break;
            BigInteger encrypted = encrypt(message,public_key,n);
            System.out.println("Encrypted message: "+encrypted.toString());
            System.out.println("Decrypted message: "+encrypt(encrypted,private_key,n));
        }
    }
}
