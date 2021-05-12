import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
public class Main {
public static BigInteger RSA_titkos(BigInteger p, BigInteger q,BigInteger m){
    Scanner myObj1 = new Scanner(System.in);
    BigInteger n=p.multiply(q);
    BigInteger fi_n=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    BigInteger e;
    System.out.println("Adj meg egy e értéket, hogy 1<e<"+fi_n+" és e és "+fi_n+" relatív prímek!");
    e=myObj1.nextBigInteger();
    while(!e.gcd(fi_n).equals(BigInteger.ONE) ||e.compareTo(BigInteger.ONE)==0||e.compareTo(fi_n)==1){
        System.out.println("Rossz e érték!");
        System.out.println("Adj meg egy e értéket, hogy 1<e<" + fi_n + " és e és " + fi_n + " relatív prímek!");
        e = myObj1.nextBigInteger();
        if(e.gcd(fi_n).equals(BigInteger.ONE) && e.compareTo(BigInteger.ONE)== 1&& e.compareTo(fi_n)==-1) break;
    }
    BigInteger d;
    d=modInverse2(e,fi_n);
    System.out.println("Publikus kulcs:" +n+" "+e);
    System.out.println("Privát kulcs: "+d);
    BigInteger c;
    c=modPow2(m,e,n);
    System.out.println("Titkosított üzenet: "+c);
    return BigInteger.ZERO;
}
public static BigInteger RSA_visszafejt(BigInteger p, BigInteger q, BigInteger c, BigInteger d){
    BigInteger d_p;
    d_p=d.mod(p.subtract(BigInteger.valueOf(1)));
    BigInteger d_q;
    d_q=d.mod(q.subtract(BigInteger.valueOf(1)));
    BigInteger m_p;
    m_p=modPow2(c,d_p,p);
    BigInteger m_q;
    m_q=modPow2(c,d_q,q);
    BigInteger[] m_ar = {p,q};
    BigInteger[] c_ar = {m_p,m_q};
    CRT(c_ar,m_ar);
    BigInteger y_p,y_q;
    y_p=Euklid(p,q)[2];
    y_q=Euklid(p,q)[3];
    BigInteger m;
    m= m_p.multiply(y_q).multiply(q).add(m_q.multiply(y_p).multiply(p)).mod(p.multiply(q));
    System.out.println("Nyílvános üzenet Euklidesszel: "+m);
    System.out.println("Nyílvános üzenet modPow2-vel: "+modPow2(c,d,p.multiply(q)));
    return BigInteger.ZERO;
}
public static BigInteger[] Euklid (BigInteger a, BigInteger b){
    BigInteger x0= BigInteger.ONE;
    BigInteger x1=BigInteger.ZERO;
    BigInteger y0= BigInteger.ZERO;
    BigInteger y1= BigInteger.ONE;
    BigInteger n= BigInteger.ONE;
    BigInteger r=BigInteger.ZERO, q=BigInteger.ZERO;
    BigInteger x=BigInteger.ZERO,y=BigInteger.ZERO;
    while(!b.equals(BigInteger.ZERO)){
        r=a.mod(b);
        q=a.divide(b);
        a=b;
        b=r;
        x=x1;
        y=y1;
        x1=q.multiply(x1).add(x0);
        y1=q.multiply(y1).add(y0);
        x0=x;
        y0=y;
        n=n.multiply(BigInteger.valueOf(-1));
    }
    x=n.multiply(x0);
    y=n.multiply(BigInteger.valueOf(-1)).multiply(y0);
    BigInteger[] result={a,b,x,y};
    return result;
}
public static BigInteger CRT(BigInteger[] c, BigInteger[] m){
    BigInteger M,M_i,y_i;
    M=m[0].multiply(m[1]);
    BigInteger x=BigInteger.ZERO;
    for(int i=0;i<c.length;i++){
        M_i=M.divide(m[i]);
        y_i=modInverse2(M_i,m[i]);
        x=x.add(c[i].multiply(M_i).multiply(y_i));
    }
    x=x.mod(M);
    System.out.println("Nyílványos üzenet CRT-vel: "+x);
    return BigInteger.ZERO;
}
public static BigInteger modPow2(BigInteger a, BigInteger e, BigInteger m){
        BigInteger result = BigInteger.ONE;
        BigInteger apow=a;
        for (int idx = 0; idx < e.bitLength(); ++idx) {
            if (e.testBit(idx)) {
                result = result.multiply(apow).mod(m);
            }
            apow = apow.multiply(apow).mod(m);
        }
        return result;
    }
public static BigInteger modInverse2(BigInteger a, BigInteger m){
        BigInteger m0,x,y,q,b;
        m0=m;
        x=BigInteger.ONE;
        y=BigInteger.ZERO;
        if(m.equals(BigInteger.ONE))return BigInteger.ZERO;
        while(a.compareTo(BigInteger.ONE)==1){
            q=a.divide(m);
            b=m;
            m=a.mod(m);
            a=b;
            b=y;
            y=x.subtract(q.multiply(y));
            x=b;
        }
        if(x.compareTo(BigInteger.ZERO)==-1)x=x.add(m0);
        return x;
    }
public static boolean MyMillerRabin(BigInteger n, Random r) {
        BigInteger temp = BigInteger.ZERO;
        do {
            temp = new BigInteger(n.bitLength()-1, r);
        } while (temp.compareTo(BigInteger.ONE) <= 0);
        if (!n.gcd(temp).equals(BigInteger.ONE)) return false;
        BigInteger base = n.subtract(BigInteger.ONE);
        BigInteger TWO = new BigInteger("2");
        int k=0;
        while ( (base.mod(TWO)).equals(BigInteger.ZERO)) {
            base = base.divide(TWO);
            k++;
        }
        BigInteger curValue = modPow2(temp,base,n);
        if (curValue.equals(BigInteger.ONE) || curValue.equals(n.subtract(BigInteger.ONE)))
            return true;
        for (int i=0; i<k; i++) {
            if (curValue.equals(n.subtract(BigInteger.ONE)))
                return true;
            else
                curValue = modPow2(curValue,TWO,n);
        }
        return false;
    }
public static boolean MillerRabin(BigInteger n, int numTimes) {
        Random r = new Random();
        for (int i=0; i<numTimes; i++)
            if (!MyMillerRabin(n,r)) return false;
        return true;
    }
public static void main(String[] args) {
        Scanner myObj = new Scanner(System.in);
        BigInteger p;
        System.out.println("Adj meg egy p prímet!");
        p = myObj.nextBigInteger();
        while(!MillerRabin(p,50)){
            System.out.println("P nem prím, adj meg új számot!");
            p = myObj.nextBigInteger();
            if(MillerRabin(p,50)) break;
        }
        BigInteger q;
        System.out.println("Adj meg egy q prímet!");
        q = myObj.nextBigInteger();
        while(!MillerRabin(q,50)){
            System.out.println("Q nem prím, adj meg új számot!");
            q = myObj.nextBigInteger();
            if(MillerRabin(q,50)) break;
        }
        BigInteger m;
        System.out.println("Adj meg egy m értéket, amit titkosítani kell!");
        m = myObj.nextBigInteger();
        RSA_titkos(p,q,m);
        BigInteger c;
        System.out.println("Adj meg egy c értéket, amit visszafejteni kell!");
        c = myObj.nextBigInteger();
        BigInteger d;
        System.out.println("Adj meg egy d értéket!");
        d = myObj.nextBigInteger();
        RSA_visszafejt(p,q,c,d);
    }
}
