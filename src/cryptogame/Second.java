package cryptogame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Second {
    FastScanner in;
    PrintWriter out;

    public void solve() throws IOException {
        int[] array = new int[10000];
        boolean flag;
        for (int i = 0; i < 2000; i++) {
            int c = in.nextInt();
            System.out.println(c);
            String answer = in.next();
            if (answer.equals("YES")) {
                array[i] = 0;
            } else {
                array[i] = 1;
            }
        }
        int period = 1;
        for (; period < 1000; period++) {
            flag = false;
            for (int j = period; j < 2000; j++) {
                if (array[j - period] != array[j]) {
                    flag = true;
                    break;
                }
            }
            if (!flag) break;
        }

        for (int i = 2000; i < 10000; i++) {
            int c = in.nextInt();
            int b = c ^ array[i % period];
            System.out.println(b);
            in.next();
        }

    }

    public void run() {
        try {
            in = new FastScanner();
            solve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }

    public static void main(String[] arg) {
        new Second().run();
    }
}