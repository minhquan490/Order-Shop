package com.bachlinh.order.handler.router;

import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NodeFactory {
    private final DependenciesResolver resolver;

    public NodeFactory(DependenciesResolver resolver) {
        this.resolver = resolver;
    }

    public Node createNode(List<String> paths) {
        paths = paths.stream().map(s -> s.substring(1)).collect(Collectors.toList());
        Collections.sort(paths);
        Node root = Node.getInstance(resolver, "", null);
        for (String path : paths) {
            createInnerNode(root, path);
        }
        return root;
    }

    private void createInnerNode(Node root, String path) {
        String[] parts = path.split("/");
        String processed = null;
        for (String part : parts) {

            if (part.isEmpty()) {
                continue;
            }

            Node node = ((NodeHolder) root).getChild(Objects.requireNonNullElse(processed, part));

            if (node == null) {
                ((NodeRegister) root).registerNode(Node.getInstance(resolver, part, root));
            } else {
                String a = Objects.requireNonNullElse(processed, part);
                String joined = String.join("/", parts);
                String p;
                if (joined.startsWith("/")) {
                    p = String.join("/", parts).replace("/" + a, "");
                } else {
                    p = String.join("/", parts).replace(a, "");
                }
                createInnerNode(node, p);
                return;
            }
            processed = part;
        }
    }
}
