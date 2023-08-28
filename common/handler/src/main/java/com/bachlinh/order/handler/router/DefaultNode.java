package com.bachlinh.order.handler.router;

import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.NativeRequest;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.translator.internal.JsonExceptionTranslator;
import com.bachlinh.order.exception.http.HttpRequestMethodNotSupportedException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.Controller;
import com.bachlinh.order.handler.controller.ControllerManager;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

class DefaultNode extends AbstractNode implements NodeRegister {
    private final List<Node> nodes = new LinkedList<>();
    private final List<String> nodeNames = new LinkedList<>();
    private final Comparator<Node> nodeComparator = Comparator.comparing(Node::getName);
    private boolean isControllerNotfound = false;

    private Controller<Object, Object> controller;

    public DefaultNode(ControllerManager controllerManager, JsonExceptionTranslator exceptionTranslator, String name, Node parent) {
        super(controllerManager, exceptionTranslator, name, parent);
    }

    @Override
    public <T, U> NativeResponse<T> handleRequest(NativeRequest<U> request, String controllerPath, RequestMethod method) throws HttpRequestMethodNotSupportedException {
        String[] p = controllerPath.split("/");
        Queue<String> paths = new ArrayDeque<>(p.length);
        paths.addAll(Arrays.asList(p));
        return handleRequest(new UrlHolder(paths, controllerPath), method, request);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, U> NativeResponse<T> handleRequest(UrlHolder urlHolder, RequestMethod method, NativeRequest<U> request) {
        if (isControllerNotfound) {
            throw new ResourceNotFoundException("Not found", urlHolder.actualUrl());
        }
        String path = urlHolder.paths().poll();

        // Case tail node
        if (urlHolder.paths().isEmpty()) {
            if (getChildren().length != 0) {
                return search(path).handleRequest(urlHolder, method, request);
            }
            if (controller == null) {
                var paths = new ArrayList<String>();
                paths.add(path);
                controller = getController(paths, method);
                if (controller == null) {
                    isControllerNotfound = true;
                    throw new ResourceNotFoundException("Not found", urlHolder.actualUrl());
                }
            }
            if (!controller.getRequestMethod().equals(method)) {
                throw new HttpRequestMethodNotSupportedException(String.format("Method [%s] not allowed", method.name()), urlHolder.actualUrl());
            } else {
                controller.setNativeRequest(getControllerManager().getNativeRequest());
                controller.setNativeResponse(getControllerManager().getNativeResponse());
                NativeRequest<Object> casted = (NativeRequest<Object>) request;
                return (NativeResponse<T>) controller.handle(casted);
            }
        }

        // Case root node
        if (!StringUtils.hasText(path)) {
            path = urlHolder.paths().poll();
        }

        return search(path).handleRequest(urlHolder, method, request);
    }

    @NonNull
    @Override
    public Node[] getChildren() {
        return nodes.toArray(new Node[0]);
    }

    @Nullable
    @Override
    public Node getChild(String name) {
        int position = Collections.binarySearch(nodeNames, name);
        if (position < 0) {
            return null;
        } else {
            return nodes.get(position);
        }
    }

    @Override
    public void registerNode(Node node) {
        nodes.add(node);
        nodes.sort(nodeComparator);
        nodeNames.clear();
        nodeNames.addAll(nodes.stream().map(Node::getName).toList());
    }


    @Override
    public NativeResponse<byte[]> translateException(Exception exception) {
        return exceptionTranslator().translateException(exception);
    }

    @Override
    public NativeResponse<byte[]> translateError(Error error) {
        return exceptionTranslator().translateError(error);
    }

    Controller<Object, Object> getController(Collection<String> paths, RequestMethod method) {
        if (paths == null) {
            paths = new ArrayList<>();
        }
        paths.add(getName());
        if (getParent() != null) {
            return ((DefaultNode) getParent()).getController(paths, method);
        } else {
            paths.removeIf(Objects::isNull);
            Collections.reverse((List<?>) paths);
            String actualPath = String.join("/", paths.toArray(new String[0]));
            return getContext().getController(actualPath, method);
        }
    }

    private DefaultNode search(String path) {
        int position = Collections.binarySearch(nodeNames, path);
        return (DefaultNode) nodes.get(position);
    }
}
