package merkletree;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MerkleTree2 {
    public static void main(String[] args) {
        new MerkleTree2().solve();
    }

    byte[] zero = new byte[]{0};
    byte[] one = new byte[]{1};
    byte[] two = new byte[]{2};
    Scanner in = new Scanner(System.in);
    int[] idsToRoot;
    int heightGlobal;
    int[] vertexDown;
    Map<Integer, String> nodeMapAnswer;
    Map<Integer, String> nodesMap;
    Map<Integer, Map<Integer, String>> levelMap;

    public void createArrayVertexDown() {
        vertexDown[heightGlobal] = 0;
        for (int i = heightGlobal - 1; i >= 0; i--) {
            vertexDown[i] = vertexDown[i + 1] + (int) Math.pow(2, i + 1);
        }
    }

    public void preparation() {
        vertexDown = new int[heightGlobal + 1];
        createArrayVertexDown();
        int n = in.nextInt();
        nodesMap = new HashMap<>();
        nodeMapAnswer = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int id = in.nextInt();
            String value = in.next();
            nodesMap.put(id, new String(Base64.getEncoder().encode(sha256(concatenate(zero, Base64.getDecoder().decode(value))))));
            nodeMapAnswer.put(id, value);
        }
        levelMap = new HashMap<>();
        levelMap.put(heightGlobal, nodesMap);
        for (int i = heightGlobal; i > 0; i--) {
            buildTree(i);
        }
    }

    public void buildTree(int curHeight) {
        Map<Integer, String> curMap = levelMap.get(curHeight);
        Map<Integer, String> nextMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : curMap.entrySet()) {
            int curId = entry.getKey();
            int add = curId % 2 == 0 ? 1 : -1;
            int vertexInLevel = (int) Math.pow(2, curHeight);
            int add1 = (curId - vertexDown[curHeight]) / 2;
            int add2 = curId % 2 != 0 ? 1 : 0;
            int idNew = curId + vertexInLevel -add1 - add2;
            if (curMap.containsKey(curId + add)) {
                String value = rightOrLeft(entry.getValue(), curMap.get(curId + add), curId);
                nextMap.put(idNew, value);
            } else {
                nextMap.put(idNew, rightOrLeft(entry.getValue(), null, curId));
            }
        }
        levelMap.put(curHeight - 1, nextMap);
    }

    public void solve() {
        heightGlobal = in.nextInt();
        idsToRoot = new int[heightGlobal];
        preparation();
        int n = in.nextInt();
        for (int i = 0; i < n; i++) {
            int id = in.nextInt();
            countNums(heightGlobal, id, 0, 0);
            System.out.println(id + " " + nodeMapAnswer.get(id));
            for (int j = 0; j < heightGlobal; j++) {
                int curId = idsToRoot[j];
                int curLevel = heightGlobal - j;
                String value = levelMap.get(curLevel).get(curId % 2 == 0 ? curId + 1 : curId - 1);
                System.out.println(value);
            }
        }
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