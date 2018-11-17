package cryptogame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class First {
    FastScanner in;
    PrintWriter out;

    public void solve() throws IOException {
        int k = 0;
        boolean flag = false;
        for (int i = 0; i < 2000; i++) {
            int c = in.nextInt();
            System.out.println(c);
            String answer = in.next();
            if (answer.equals("YES")) {
                k++;
            }
        }
        if (k > 1000) {
            flag = true;
        }
        for (int i = 2000; i < 10000; i++) {
            int c = in.nextInt();
            if (flag) {
                System.out.println(c);
            } else {
                System.out.println((c + 1) % 2);
            }
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
        new First().run();
    }
}