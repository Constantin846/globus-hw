package tk.project.globus.hw.utility;

import java.math.BigInteger;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PasswordEncoder {

  public static String encode(String password) {

    IntStream intStream = password.chars();
    BigInteger sum =
        intStream.mapToObj(BigInteger::valueOf).reduce(BigInteger.ZERO, BigInteger::add);

    sum = BigInteger.valueOf(8_232_323_433_324_765_857L).multiply(sum);
    return String.valueOf(sum);
  }
}
