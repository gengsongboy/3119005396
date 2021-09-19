package test_1;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PaperPassCount {
    public static void main(String[] args) {

        String porig;
        String padd;
        String answer;
        String[] original = new String[500];
        String[] add = new String[500];

        Scanner in = new Scanner(System.in);
        System.out.println("请输入原文路径:");   
        porig = in.nextLine();
        original = TxtToArray(porig);
        System.out.println("请输入查重论文路径:");
        padd = in.nextLine();
        add = TxtToArray(padd);
        System.out.println("请输入答案储存路径:");
        answer = in.nextLine();
        PaperPass(original, add, answer);

    }

    private static int JudgeType(int temp) {
        if ((char) temp == ' ' || (char) temp == '，' || (char) temp == '\r' || (char) temp == '\t' ||
                (char) temp == '、' || (char) temp == '《' || (char) temp == '.' || (char) temp == '-'
                || (char) temp == '”' || (char) temp == '“' || (char) temp == '》' || (char) temp == '：'
                || (char) temp == '—' || (char) temp == '；')
            return 0;   //忽略
        else if ((char) temp == '。' || (char) temp == '!' || (char) temp == '？' || (char) temp == '\n'
                || (char) temp == ';' || (char) temp == '>')
            return 1;   //判定为句子
        else return 2;
    }

    private static String[] TxtToArray(String paperPath) {
        String[] senArr = new String[2000];
        try {
            Reader reader = null;
            reader = new InputStreamReader(new FileInputStream(new File(paperPath)));
            int temp;
            int n = 0;
            String sen = "";
            while ((temp = reader.read()) != -1) {
                switch (JudgeType(temp)) {
                    case 1:
                        if (sen.equals("")) break;
                        if (sen.length() > 5) senArr[n++] = sen;
                        sen = "";
                        break;
                    case 2:
                        sen = sen + (char) (temp);
                    default:
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return senArr;
    }

    private static void PaperPass(String[] original, String[] add, String answer) {
        double simBi = 0;
        double senBi;
        double Wnum = 0;
        for (String doc1 : original
        ) {
            senBi = 0;
            if (doc1 == null) break;
            Wnum += doc1.length();
            for (String doc2 : add
            ) {
                if (doc2 == null) break;
                Map<Character, int[]> algMap = new HashMap<Character, int[]>();
                for (int i = 0; i < doc1.length(); i++) {
                    char d1 = doc1.charAt(i);
                    int[] fq = algMap.get(d1);
                    if (fq != null && fq.length == 2) {
                        fq[0]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 1;
                        fq[1] = 0;
                        algMap.put(d1, fq);
                    }
                }
                for (int i = 0; i < doc2.length(); i++) {
                    char d2 = doc2.charAt(i);
                    int[] fq = algMap.get(d2);
                    if (fq != null && fq.length == 2) {
                        fq[1]++;
                    } else {
                        fq = new int[2];
                        fq[0] = 0;
                        fq[1] = 1;
                        algMap.put(d2, fq);
                    }
                }
                double s1 = 0;
                double s2 = 0;
                double dt = 0;
                for (Map.Entry entry : algMap.entrySet()) {
                    int[] c = (int[]) entry.getValue();
                    dt += c[0] * c[1];
                    s1 += c[0] * c[0];
                    s2 += c[1] * c[1];
                }
                double similiar = dt / Math.sqrt(s1 * s2);
                if (similiar > senBi)
                    senBi = similiar;
            }
            simBi += (senBi * doc1.length());
        }
        simBi = simBi / Wnum * 100;
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        df.format(simBi);
        System.out.println("论文重复率为" + simBi + "%");
        File file = new File(answer);
        try {
            Writer writer = new FileWriter(file,false);
            writer.write("论文重复率为" + simBi + "%");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}