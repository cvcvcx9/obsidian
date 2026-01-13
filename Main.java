import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int M = sc.nextInt();

        int[] cost = {500, 700, 900};
        String[] name = {"연필", "볼펜", "샤프"};

        boolean[] dp = new boolean[M + 1];

        int[] c0 = new int[M + 1]; // 연필 개수
        int[] c1 = new int[M + 1]; // 볼펜 개수
        int[] c2 = new int[M + 1]; // 샤프 개수

        dp[0] = true;

        for (int x = 0; x <= M; x++) {
            if (!dp[x]) continue;

            for (int i = 0; i < 3; i++) {
                int nx = x + cost[i];
                if (nx > M) continue;

                // nx를 처음 만들거나, (같은 nx라도) 갱신 규칙을 넣고 싶으면 여기서 비교 가능
                if (!dp[nx]) {
                    dp[nx] = true;

                    // x의 개수를 그대로 복사
                    c0[nx] = c0[x];
                    c1[nx] = c1[x];
                    c2[nx] = c2[x];

                    // i번째 물건 하나 추가
                    if (i == 0) c0[nx]++;
                    if (i == 1) c1[nx]++;
                    if (i == 2) c2[nx]++;
                }
            }
        }

        // 잔돈 최소 = 만들 수 있는 가장 큰 금액 찾기
        int best = 0;
        for (int x = M; x >= 0; x--) {
            if (dp[x]) { best = x; break; }
        }

        int change = M - best;

        List<String> out = new ArrayList<>();
        if (c0[best] > 0) out.add("연필 " + c0[best] + "개");
        if (c1[best] > 0) out.add("볼펜 " + c1[best] + "개");
        if (c2[best] > 0) out.add("샤프 " + c2[best] + "개");

        System.out.println(String.join(", ", out));
        System.out.println("잔돈: " + change);
    }
}
