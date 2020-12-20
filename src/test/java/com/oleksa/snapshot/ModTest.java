package com.oleksa.snapshot;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;

public class ModTest {

    @Test
    void perfEncryption() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        BigInteger prime = BigInteger.probablePrime(256, current);
        System.out.println(prime);
        BigInteger nextPrime = prime.nextProbablePrime();
        System.out.println(nextPrime);
        System.out.println(nextPrime.subtract(prime));

        BigInteger a = new BigInteger(255, 0, current);
        System.out.println(a);
        BigInteger b = a.modInverse(prime);
        System.out.println(b);
        BigInteger prod = a.multiply(b);
        System.out.println(prod);
        BigInteger rem = prod.mod(prime);
        System.out.println(rem);

        BigInteger gcd = a.gcd(prime);
        System.out.println(gcd);
        BigInteger[] quotAndRem = prod.subtract(gcd).divideAndRemainder(prime);
        System.out.println(quotAndRem[0]);
        System.out.println(quotAndRem[1]);
        BigInteger exEuclid = prod.add(quotAndRem[0].negate().multiply(prime));
        System.out.println(exEuclid);

        BigInteger n = new BigInteger(127, 0, current);
        System.out.println(n);
        BigInteger z = n.modPow(prime.subtract(BigInteger.ONE), prime);
        System.out.println(z);

        BigInteger m = new BigInteger(255, 0, current);
        System.out.println(m);
        BigInteger ma = m.multiply(a);
        System.out.println(ma);
        BigInteger res = ma.multiply(b).mod(prime);
        System.out.println(res);

        BigInteger[] sqrtAndRemainder = prime.sqrtAndRemainder();
        System.out.println(sqrtAndRemainder[0]);
        System.out.println(sqrtAndRemainder[1]);
        System.out.println(prime);
        System.out.println(sqrtAndRemainder[0].pow(2).add(sqrtAndRemainder[1]));
    }
}
