package merklesignature;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class MerkleSignature {

    private String rootHash;
    private boolean[] wasInThisNode = new boolean[256];
    private boolean[] wasInThisNodeTwice = new boolean[256];
    int globalHeight = 8;

    byte[][][][] X = new byte[256][2][256][32];
    byte[][][][] Y = new byte[256][2][256][32];

    Scanner in;

    {
        try {
            in = new Scanner(new File("in.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new MerkleSignature().solve();
    }

    public void solve() {
        rootHash = in.next();

        MerkleTree merkleTree = new MerkleTree(globalHeight);

        for (int round = 0; round < 1000; round++) {
            int id = in.nextInt();
            printDocHash(id);
            String baseSignature = in.next();
            String basePublicKey = in.next();

            byte[] signature = merkleTree.decodeBase64(baseSignature);
            byte[] publicKey = merkleTree.decodeBase64(basePublicKey);

            boolean isCorrectSignature = isCorrectSignature(signature, publicKey, merkleTree, wasInThisNode[id]);

            String[] parentsHash = new String[globalHeight];
            for (int i = 0; i < globalHeight; i++) {
                parentsHash[i] = in.next();
            }

            boolean isCorrectProof = merkleTree.verify(id, basePublicKey, parentsHash);
            String Qhash = in.next();
            System.out.println(isCorrectProof);
            System.out.println(isCorrectSignature);
            if (isCorrectProof && isCorrectSignature) {
                if (wasInThisNode[id]) {
                    wasInThisNodeTwice[id] = true;
                }
                for (int i = 0; i < 256; i++) {
                    for (int j = 0; j < 32; j++) {
                        X[id][wasInThisNode[id] ? 1 : 0][i][j] = signature[32 * i + j];
                    }
                }
                wasInThisNode[id] = true;
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
            if (wasInThisNodeTwice[id]) {
                byte[] answer = new byte[256 * 32];
                for (int i = 0; i < 256; i++) {
                    int k = 0;
                    if (Qhash.charAt(i) == '1') {
                        k = 1;
                    }
                    for (int j = 0; j < 32; j++) {
                        answer[32 * i + j] = X[id][k][i][j];
                    }
                }
                System.out.println("YES");
                System.out.println(merkleTree.encodeBase64(answer));
                break;
            } else {
                System.out.println("NO");
            }
        }
    }

    public boolean isCorrectSignature(byte[] signature, byte[] publicKey, MerkleTree merkleTree, boolean isSecond) {
        byte[] signatureSHA256 = new byte[256 * 32];
        for (int i = 0; i < 256; i++) {
            byte[] piece = new byte[32];
            System.arraycopy(signature, 32 * i, piece, 0, 32);
            byte[] shaPiece = merkleTree.sha256(piece);
            System.arraycopy(shaPiece, 0, signatureSHA256, i * 32, 32);
        }

        for (int i = 0; i < signatureSHA256.length; i++) {
            if (signatureSHA256[i] != publicKey[isSecond ? 32 * 256 + i : i]) {
                return false;
            }
        }
        return true;
    }

    public void printDocHash(int id) {
        if (wasInThisNode[id]) {
            for (int i = 0; i < 256; i++) {
                System.out.print("1");
            }
            System.out.println();
        } else {
            for (int i = 0; i < 256; i++) {
                System.out.print("0");
            }
            System.out.println();
        }
    }

    class MerkleTree {
        int height;
        int idsToRoot[];
        byte[] zero = new byte[]{0};
        byte[] one = new byte[]{1};
        byte[] two = new byte[]{2};

        MerkleTree(int height) {
            this.height = height;
            idsToRoot = new int[height];
        }

        public boolean verify(int id, String sonHash, String[] parentsHash) {
            countNums(height, id, 0, 0);
            String nodeHash = sonHash;
            if (nodeHash.equals("null")) nodeHash = null;
            nodeHash = countList(nodeHash);
            for (int i = 0; i < height; i++) {
                String curHash = parentsHash[i];
                if (curHash.equals("null")) curHash = null;
                nodeHash = rightOrLeft(nodeHash, curHash, idsToRoot[i]);
            }
            return nodeHash.equals(rootHash);
        }

        public byte[] concatenate(byte[] left, byte[] right) {
            byte[] result = new byte[left.length + right.length];
            System.arraycopy(left, 0, result, 0, left.length);
            for (int i = left.length; i < right.length + left.length; i++) {
                result[i] = right[i - left.length];
            }
            return result;
        }

        public String rightOrLeft(String nodeHash, String neighbourHash, int id) {
            if (id % 2 == 0) {
                return count(nodeHash, neighbourHash);
            } else {
                return count(neighbourHash, nodeHash);
            }
        }

        public byte[] decodeBase64(String string) {
            return Base64.getDecoder().decode(string);
        }

        public String encodeBase64(byte[] bytes) {
            return new String(Base64.getEncoder().encode(bytes));
        }

        public String count(String left, String right) {
            if (left == null && right == null) return null;
            if (left == null) left = "";
            if (right == null) right = "";
            byte[] leftByte = decodeBase64(left);
            byte[] rightByte = decodeBase64(right);
            byte[] concatenatedSHA256ByteArray = sha256(concatenate(concatenate(concatenate(one, leftByte), two), rightByte));
            return new String(Base64.getEncoder().encode(concatenatedSHA256ByteArray));
        }

        public String countList(String nodeHash) {
            if (nodeHash == null) return null;
            else
                return new String(Base64.getEncoder().encode(sha256(concatenate(zero, Base64.getDecoder().decode(nodeHash)))));
        }

        public byte[] sha256(byte[] input) {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
            return digest.digest(input);
        }

        public void countNums(int height, int id, int vertexDown, int i) {
            if (height == 0) return;
            idsToRoot[i] = id;
            int vertexInLevel = (int) Math.pow(2, height);
            int add1 = (id - vertexDown) / 2;
            int add2 = id % 2 != 0 ? 1 : 0;
            int idNew = id + vertexInLevel - add1 - add2;
            countNums(height - 1, idNew, vertexDown + vertexInLevel, i + 1);
        }
    }
}
