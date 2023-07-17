import com.bachlinh.order.handler.router.Node;
import com.bachlinh.order.handler.router.NodeFactory;

import java.util.Arrays;

public class TestMain {
    public static void main(String[] args) {
        String[] testPaths = new String[]{
                "/admin/home",
                "/admin/test/test1",
                "/test",
                "/customer/home",
                "/customer/test/test1",
                "/admin/email/common/sending"
        };
//        String[] testPaths = new String[]{
//                "/admin/home",
//                "/admin/test/test1",
//                "/customer/home"
//        };
        NodeFactory nodeFactory = new NodeFactory(null);
        Node node = nodeFactory.createNode(Arrays.asList(testPaths));
        node.getName();
    }
}
