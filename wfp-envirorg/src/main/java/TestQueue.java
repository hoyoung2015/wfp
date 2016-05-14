import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Administrator on 2015/11/10.
 */
public class TestQueue {
    public static void main(String[] args) {
        BlockingDeque<String> queue = new LinkedBlockingDeque<String>();
                queue.push("a");
                queue.push("b");
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }
}
