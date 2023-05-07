package com.bachlinh.order.handler.router;

import com.bachlinh.order.handler.controller.AbstractControllerManager;
import com.bachlinh.order.handler.controller.Controller;
import com.bachlinh.order.handler.controller.ControllerManager;

import java.util.Arrays;
import java.util.Collection;

public class ChildRouteDecorator {
    private final ChildRouteContext childRouteContext;

    private ChildRouteDecorator(ChildRouteContext childRouteContext) {
        this.childRouteContext = childRouteContext;
    }

    public static ChildRouteDecorator wrap(ChildRouteContext childRouteContext) {
        return new ChildRouteDecorator(childRouteContext);
    }

    public ChildRouteContext decorate(ControllerManager controllerManager) {
        AbstractControllerManager abstractControllerManager = (AbstractControllerManager) controllerManager;
        Collection<Controller<?, ?>> controllers = abstractControllerManager.getAllController();
        controllers.forEach(controller -> {
            String fullPath = controller.getPath();
            String[] children = Arrays.stream(fullPath.split("/"))
                    .filter(child -> !child.isBlank())
                    .map(child -> child.replace("-", "_"))
                    .toList()
                    .toArray(new String[0]);
            String childName;
            if (children.length == 1) {
                childName = "-" + children[0];
            } else {
                childName = String.join("-", children);
            }
            childRouteContext.addChild(childName, "/".concat(children[0]), "/".concat(String.join("/", children)));
        });
        return childRouteContext;
    }
}
