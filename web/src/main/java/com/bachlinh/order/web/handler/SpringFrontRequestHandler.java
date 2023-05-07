package com.bachlinh.order.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import com.bachlinh.order.core.http.NativeResponse;
import com.bachlinh.order.core.http.handler.RequestHandler;
import com.bachlinh.order.core.http.handler.SpringServletHandler;
import com.bachlinh.order.core.http.translator.spi.ExceptionTranslator;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.handler.controller.ControllerManager;
import com.bachlinh.order.handler.router.AbstractChildRouteContext;
import com.bachlinh.order.handler.router.ChildRoute;
import com.bachlinh.order.handler.router.ChildRouteContext;
import com.bachlinh.order.handler.router.ChildRouteDecorator;
import com.bachlinh.order.handler.router.SmartChildRouteContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringFrontRequestHandler {
    private static final String SEPARATOR = "/";

    private final ChildRouteContext childRouteContext;

    public SpringFrontRequestHandler(ControllerManager controllerManager, EntityFactory entityFactory, ExceptionTranslator<NativeResponse<String>> exceptionTranslator) {
        SimpleChildRouteContext simpleChildRouteContext = new SimpleChildRouteContext();
        simpleChildRouteContext.controllerManager(controllerManager);
        simpleChildRouteContext.entityFactory(entityFactory);
        simpleChildRouteContext.exceptionTranslator(exceptionTranslator);
        ChildRouteDecorator decorator = ChildRouteDecorator.wrap(simpleChildRouteContext);
        this.childRouteContext = decorator.decorate(controllerManager);
    }

    public <T> ResponseEntity<T> handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String[] urlPart = servletRequest.getRequestURI().split(SEPARATOR);
        return handleRequest(String.join(SEPARATOR, urlPart).replace(SEPARATOR + urlPart[0], ""), urlPart[0], servletRequest, servletResponse);
    }

    private <T> ResponseEntity<T> handleRequest(@NonNull String path, String prefix, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        SmartChildRouteContext smartChildRouteContext = (SmartChildRouteContext) childRouteContext;
        return smartChildRouteContext.handleRequest(path, prefix, servletRequest, servletResponse);
    }

    private static class SimpleChildRouteContext extends AbstractChildRouteContext implements SmartChildRouteContext {
        private final Map<String, String> childNameCache = new ConcurrentHashMap<>();

        @Override
        public RequestHandler getHandler(String name) {
            return getChild(name);
        }

        @Override
        public String parsePathToChildRouteName(String path, String prefix) {
            if (childNameCache.containsKey(path)) {
                return childNameCache.get(path);
            }
            String psudoPath = path;
            if (psudoPath.startsWith("/")) {
                psudoPath = psudoPath.substring(1);
            }
            if (psudoPath.startsWith(prefix) && !prefix.isEmpty()) {
                psudoPath = psudoPath.substring(prefix.length());
            }
            psudoPath = psudoPath.replace("-", "_");
            String[] partPath = psudoPath.split("/");
            String name = prefix + "-" + String.join("-", partPath);
            cachePath(path, name);
            return name;
        }

        @Override
        public void cachePath(String path, String childName) {
            this.childNameCache.put(path, childName);
        }

        @Override
        public void evictCache(String path) {
            childNameCache.remove(path);
        }

        @Override
        public <T> ResponseEntity<T> handleRequest(String path, String prefix, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
            ChildRouteWrapper childRouteWrapper = findRoute(path, prefix);
            SpringServletHandler servletHandler = childRouteWrapper.childRoute().getServletHandler();
            ResponseEntity<T> response = servletHandler.handleServletRequest("/".concat(childRouteWrapper.endpoint()), servletRequest, servletResponse);
            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                evictCache(path);
            }
            return response;
        }

        void controllerManager(ControllerManager controllerManager) {
            super.setControllerManager(controllerManager);
        }

        void exceptionTranslator(ExceptionTranslator<NativeResponse<String>> exceptionTranslator) {
            super.setExceptionTranslator(exceptionTranslator);
        }

        void entityFactory(EntityFactory entityFactory) {
            super.setEntityFactory(entityFactory);
        }

        private ChildRouteWrapper findRoute(String path, String prefix) {
            String name = parsePathToChildRouteName(path, prefix);
            String endpoint = path.substring(path.lastIndexOf("/") + 1);
            ChildRoute childRoute = getChild(name);
            return new ChildRouteWrapper(childRoute, endpoint);
        }

        private record ChildRouteWrapper(ChildRoute childRoute, String endpoint) {
        }
    }
}
