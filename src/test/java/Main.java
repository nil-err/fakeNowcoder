import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String str = br.readLine();
    int goodsNum = Integer.valueOf(str.trim());
    String[] price = br.readLine().trim().split(" ");
    String[] discountPrice = br.readLine().trim().split(" ");
    int ruleNum = Integer.valueOf(br.readLine().trim());
    String[] rule1 = br.readLine().trim().split(" ");
    String[] rule2 = br.readLine().trim().split(" ");

    int[] goodsPrice = new int[goodsNum];
    int[] goodsdis = new int[goodsNum];
    for (int i = 0; i < goodsNum; i++) {
      goodsPrice[i] = Integer.valueOf(price[i]);
      goodsdis[i] = Integer.valueOf(discountPrice[i]);
    }
    int[] rule11 = new int[ruleNum];
    int[] rule21 = new int[ruleNum];
    for (int i = 0; i < ruleNum; i++) {
      rule11[i] = Integer.valueOf(rule1[i]);
      rule21[i] = Integer.valueOf(rule2[i]);
    }

    StringBuilder sb = new StringBuilder();

    int priceNow = 0;
    int disPriceNow = 0;

    for (int i = 0; i < goodsNum; i++) {
      priceNow += goodsPrice[i];
      disPriceNow += goodsdis[i];
      int res = priceNow;
      for (int j = ruleNum - 1; j >= 0; j--) {
        if (priceNow > rule11[j]) {
          res = Math.min(res, priceNow - rule21[j]);
        }
      }
      if (res > disPriceNow) {
        sb.append('Z');
      } else if (res < disPriceNow) {
        sb.append('M');
      } else {
        sb.append('B');
      }
    }

    System.out.println(sb.toString());
  }

  //  public static void main(String[] args) throws IOException {
  //    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  //    String[] nums = br.readLine().trim().split(" ");
  //    String string = br.readLine().trim();
  //    if ("1".equals(nums[1])) {
  //      String encode = encode(Integer.valueOf(nums[0]), string);
  //      System.out.println(encode);
  //    } else if ("2".equals(nums[1])) {
  //      String decode = decode(Integer.valueOf(nums[0]), string);
  //      System.out.println(decode);
  //    } else {
  //      throw new IllegalArgumentException("参数错误");
  //    }
  //  }
  //
  //  private static String encode(int len, String str) {
  //    StringBuilder sb = new StringBuilder();
  //    for (int i = 0; ((((len + 1) / 2) - 1 - i) >= 0) || ((((len + 1) / 2) - 1 + i) < len); i++)
  // {
  //      if (i != 0 && ((((len + 1) / 2) - 1 + i) < len)) {
  //        sb.append(str.charAt(((len + 1) / 2) - 1 + i));
  //      }
  //      if (((((len + 1) / 2) - 1 - i) >= 0)) {
  //        sb.append(str.charAt(((len + 1) / 2) - 1 - i));
  //      }
  //    }
  //    return sb.toString();
  //  }
  //
  //  private static String decode(int len, String str) {
  //    char[] chars = new char[len];
  //
  //    for (int i = 0; ((((len) / 2) - 1 - i) >= 0) || ((((len) / 2) - 1 + i) < len); i += 2) {
  //      if (i != 0 && ((((len) / 2) - 1 + i / 2) < len)) {
  //        int index = ((len) / 2) - 1 + i / 2;
  //        chars[((len) / 2) - 1 + i / 2] = str.charAt(i);
  //      }
  //      if (((((len) / 2) - 1 - i / 2) >= 0)) {
  //        int index = ((len) / 2) - 1 - i / 2;
  //        chars[((len) / 2) - 1 - i / 2] = str.charAt(i + 1);
  //      }
  //    }
  //
  //    StringBuilder sb = new StringBuilder();
  //    for (char c : chars) {
  //      sb.append(c);
  //      System.out.println(c);
  //    }
  //    return sb.toString();
  //  }
}
