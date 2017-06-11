package map.y;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class SHA512
{
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      System.out.println(SHA512(scanner.nextLine()));
    }
//    System.out.println(0xffff_ffff_ffff_ffffL);

  }

  public static String SHA512(final String strText)
  {  
    return SHA(strText, "SHA-512");
  }  

  private static String SHA(final String strText, final String strType)
  {
    String strResult = null;  

    if (strText != null && strText.length() > 0)  
    {  
      try  
      {
        MessageDigest messageDigest = MessageDigest.getInstance(strType);
        messageDigest.update(strText.getBytes());
        byte byteBuffer[] = messageDigest.digest();  

        StringBuilder strHexString = new StringBuilder();
        for (int i = 0; i < byteBuffer.length; i++)  
        {  
          String hex = Integer.toHexString(0xff & byteBuffer[i]);  
          if (hex.length() == 1)  
          {  
            strHexString.append('0');  
          }  
          strHexString.append(hex);  
        }
        strResult = strHexString.toString();  
      }  
      catch (NoSuchAlgorithmException e)  
      {  
        e.printStackTrace();  
      }  
    }  
  
    return strResult;  
  }  
}  