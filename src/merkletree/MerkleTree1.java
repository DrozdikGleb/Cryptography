package merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

public class MerkleTree1 {
    public static void main(String[] args) {
        new MerkleTree1().solve();
    }
    String rootHash;
    Scanner in = new Scanner(System.in);

    public void solve() {
        int height = in.nextInt();
        MerkleTree merkleTree = new MerkleTree(height);
        rootHash = in.next();
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            int id = in.nextInt();
            String sonHash = in.next();
            String []parentsHash = new String[height];
            for (int j = 0; j < height; j++) {
                parentsHash[j] = in.next();
            }
            merkleTree.verify(id, sonHash, parentsHash);
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
            if (nodeHash == null) {
                System.out.println("NO");
            } else {
                System.out.println(nodeHash.equals(rootHash) ? "YES" : "NO");
            }
            return true;
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

        public String count(String left, String right) {
            if (left == null && right == null) return null;
            if (left == null) left = "";
            if (right == null) right = "";
            byte[] leftByte = Base64.getDecoder().decode(left);
            byte[] rightByte = Base64.getDecoder().decode(right);
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