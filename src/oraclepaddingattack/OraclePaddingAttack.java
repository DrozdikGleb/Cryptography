package oraclepaddingattack;

import java.util.Base64;
import java.util.Scanner;

public class OraclePaddingAttack {
    Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        new OraclePaddingAttack().solve();
    }

    public void solve() {
        new MessageDecoder().solveDecoder();
    }

    class MessageDecoder {
        String IV;
        String message;

        public void solveDecoder() {
            System.out.println(Integer.MIN_VALUE);
            message = in.nextLine();
            IV = in.nextLine();
            byte[] messageByte = decodeBase64(message);
            byte[] IVByte = decodeBase64(IV);
            IVByte[2] = (byte) (IVByte[2] + 1);
            System.out.println("NO");
            System.out.println(message);
            System.out.println(encodeBase64(IVByte));

            if (in.nextLine().equals("Wrong padding")) {
                System.out.println("YES");
                System.out.println("No");
                return;
            }

            System.out.println("NO");
            IVByte = decodeBase64(IV);
            IVByte[2] = (byte) ("s".getBytes()[0] ^ IVByte[2] ^ 14);
            for (int i = 3; i < 16; i++) {
                IVByte[i] = (byte) (0x0D ^ IVByte[i] ^ 0x0E);
            }

            System.out.println(message);
            System.out.println(encodeBase64(IVByte));
            String lastAnswer = in.nextLine();
            if (lastAnswer.equals("Ok")) {
                System.out.println("YES");
                System.out.println("Yes");
            } else {
                System.out.println("YES");
                System.out.println("N/A");
            }

        }

        public byte[] decodeBase64(String string) {
            return Base64.getDecoder().decode(string);
        }

        public String encodeBase64(byte[] bytes) {
            return Base64.getEncoder().encodeToString(bytes);
        }

    }

}
