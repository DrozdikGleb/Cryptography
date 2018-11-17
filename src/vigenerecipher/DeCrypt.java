package vigenerecipher;

import java.io.*;
import java.util.StringTokenizer;

public class DeCrypt {
    FastScanner in;
    PrintWriter out;
    //индекс совпадения для английского языка
    double matcherIndex = 0.0667;
    //частота встречаемости букв английского алфавита (взято с википедии)
    double[] englishLettersFrequency = {0.08167, 0.01492, 0.02782, 0.04253, 0.127, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153, 0.00772,
            0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758,
            0.00978, 0.02360, 0.00150, 0.01974, 0.00074};
    int[] letterCount = new int[26];
    double[] letterFrequency = new double[26];
    long lengthOfText;
    String cipherText;
    String[] textsWithShift = new String[5];
    String answerText;
    double[][] freqWithShift = new double[26][26];

    public void init(String text) {
        lengthOfText = text.length();
        answerText = new String(new char[(int) lengthOfText]);
        for (int i = 6; i <= 10; i++) {
            int index = 0;
            StringBuilder curString = new StringBuilder();
            while (index < text.length()) {
                curString.append(text.charAt(index));
                index += i;
            }
            textsWithShift[i - 6] = curString.toString();
        }
        createFreqDoubleArray();
    }

    public void createFreqDoubleArray() {
        System.arraycopy(englishLettersFrequency, 0, freqWithShift[0], 0, 25);
        for (int i = 1; i < 26; i++) {
            int k = 26 - i;
            for (int j = 0; j < 26; j++) {
                if (k >= 26) {
                    k = 0;
                }
                freqWithShift[i][j] = englishLettersFrequency[k];
                k++;
            }
        }
    }

    public void solve() {
        cipherText = in.next();
        init(cipherText);
        int keyWordLength = countKeywordLength();
        decodeCipherText(keyWordLength);
    }

    public int countKeywordLength() {
        double min = 100;
        int length = 0;
        for (int i = 0; i < 5; i++) {
            countFreq(textsWithShift[i]);
            double curIndex = countMatcherIndexOfText(textsWithShift[i]);
            if (Math.abs(curIndex - matcherIndex) < min) {
                min = Math.abs(curIndex - matcherIndex);
                length = i + 6;
            }
        }
        lengthOfText = cipherText.length();
        return length;
    }

    //разбиваем наш текст на keywordLength групп и в каждой группе находим сдвиг, затем, зная частоту встречамости символов
    //в английском алфавите востанновим буквы в каждой группе.
    public void decodeCipherText(int keywordLength) {
        for (int i = 0; i < keywordLength; i++) {
            letterCount = new int[26];
            StringBuilder textWithShifts = new StringBuilder();
            for (int j = 0; j <= lengthOfText / keywordLength; j++) {
                if (j * keywordLength + i < lengthOfText) {
                    textWithShifts.append(cipherText.charAt(j * keywordLength + i));
                }
            }
            countFreq(textWithShifts.toString());
            int shift = findShift();
            StringBuilder stringBuilder = new StringBuilder(answerText);
            for (int j = 0; j <= lengthOfText / keywordLength; j++) {
                if (j * keywordLength + i < lengthOfText) {
                    int num = cipherText.charAt(j * keywordLength + i) - 97 - shift;
                    if (num < 0) {
                        num = 26 + num;
                    }
                    stringBuilder.setCharAt(j * keywordLength + i, (char) ((num) + 97));
                }
            }
            answerText = stringBuilder.toString();
        }
        out.println(answerText);
    }

    public int findShift() {
        double max = 0.0;
        int shift = 0;
        for (int i = 0; i < 26; i++) {
            double value = 0.0;
            for (int j = 0; j < 26; j++) {
                value += (letterFrequency[j] * freqWithShift[i][j]);
            }
            if (max < value) {
                max = value;
                shift = i;
            }
        }
        return shift;
    }

    public void countFreq(String text) {
        letterCount = new int[26];
        for (int i = 0; i < text.length(); i++) {
            letterCount[(int) text.charAt(i) - 97]++;
        }
        for (int i = 0; i < 26; i++) {
            letterFrequency[i] = (letterCount[i] / (double) text.length());
        }
    }

    public double countMatcherIndexOfText(String text) {
        double index = 0;
        lengthOfText = text.length();
        for (int i = 0; i < 26; i++) {
            index += ((double) (letterCount[i] * letterCount[i])) / ((double) (lengthOfText * lengthOfText));
        }
        return index;
    }

    public void run() {
        try {
            in = new FastScanner(new File("./src/vigenerecipher/input.cipher"));
            out = new PrintWriter(new File("./src/vigenerecipher/decrypt.cipher"));
            solve();

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
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
        new DeCrypt().run();
    }
}