package com.bachlinh.order.core.server.netty.shaded;

import io.netty.util.internal.UnstableApi;

@UnstableApi
public final class Http2ConnectionPrefaceAndSettingsFrameWrittenEvent {
    static final Http2ConnectionPrefaceAndSettingsFrameWrittenEvent INSTANCE =
            new Http2ConnectionPrefaceAndSettingsFrameWrittenEvent();

    private Http2ConnectionPrefaceAndSettingsFrameWrittenEvent() {
    }
}
