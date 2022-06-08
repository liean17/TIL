import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

class Main{

    static int[][] arr;
    static int N;
    static int M;
    static int V;
    static boolean[] visited;

    static Queue<Integer> queue;

    static String dfsAnswer;
    static String bfsAnswer;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        V = Integer.parseInt(st.nextToken());

        //필요한 초기화들
        arr = new int[M+1][M+1];
        visited = new boolean[M+1];
        queue = new LinkedList<>();

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());
            arr[x][y] = 1;
            arr[y][x] = 1;
        }
        System.out.println(Arrays.deepToString(arr));

        dfsAnswer = V+"";
        bfsAnswer = "";

        dfs(V);
        visited = new boolean[M+1];

        bfs(V);

        System.out.println(dfsAnswer);
        System.out.println(bfsAnswer);
    }


    public static void dfs(int start){
        visited[start]=true;
        for (int i = 1; i <= N ; i++) {
            if (arr[start][i]==1&&visited[i]==false){
                dfsAnswer+=" "+ i;
                visited[i]=true;
                dfs(i);
            }
        }
        return;
    }

    public static void bfs(int start){
        visited[start]=true;
        queue.add(start);

        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            bfsAnswer+=poll+" ";
            for (int i = 1; i <= M; i++) {
                if (arr[poll][i] == 1 && visited[i] == false) {
                    queue.add(i);
                    visited[i] = true;
                }
            }
        }
    }
}