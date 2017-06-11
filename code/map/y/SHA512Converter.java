package map.y;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Created by T on 2017/4/21.
 */
public class SHA512Converter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("域的大小为：" + maxValue);
        while (scanner.hasNext())
            System.out.println("\n= " + convert(SHA512.SHA512(scanner.nextLine())));
        scanner.close();
    }

    private static BigDecimal maxValue;
    static {
        StringBuilder builder = new StringBuilder();
        builder.append('1');
        for (int i = 0; i < 128; i++) builder.append('0');
        maxValue = new BigDecimal(new BigInteger(builder.toString(), 16));
    }
    public static double convert(String shaText) {
        BigDecimal sha = new BigDecimal(new BigInteger(shaText, 16));
        return sha.divide(maxValue, 1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }
}
