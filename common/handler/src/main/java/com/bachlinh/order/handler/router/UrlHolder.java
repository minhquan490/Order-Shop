package com.bachlinh.order.handler.router;

import java.util.Queue;

record UrlHolder(Queue<String> paths, String actualUrl) {
}
