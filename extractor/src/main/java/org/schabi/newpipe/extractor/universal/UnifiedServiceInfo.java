package org.schabi.newpipe.extractor.universal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basic information about a service supported by the universal facade.
 */
public final class UnifiedServiceInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int serviceId;
    @Nonnull
    private final String name;
    @Nullable
    private final String baseUrl;
    @Nonnull
    private final List<String> mediaCapabilities;

    /**
     * Creates a new service information.
     *
     * @param serviceId the id of the service
     * @param name the name of the service
     * @param baseUrl the base URL of the service
     * @param mediaCapabilities the names of the media capabilities of the service
     */
    public UnifiedServiceInfo(final int serviceId,
                              @Nonnull final String name,
                              @Nullable final String baseUrl,
                              @Nonnull final List<String> mediaCapabilities) {
        this.serviceId = serviceId;
        this.name = name;
        this.baseUrl = baseUrl;
        this.mediaCapabilities =
                Collections.unmodifiableList(new ArrayList<>(mediaCapabilities));
    }

    /**
     * Gets the id of the service.
     *
     * @return the service id
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Gets the name of the service.
     *
     * @return the service name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the base URL of the service.
     *
     * @return the base URL, or {@code null} if not available
     */
    @Nullable
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Gets the names of the media capabilities of the service,
     * e.g. {@code AUDIO}, {@code VIDEO}, {@code LIVE} and {@code COMMENTS}.
     *
     * @return the media capability names, never {@code null}
     */
    @Nonnull
    public List<String> getMediaCapabilities() {
        return mediaCapabilities;
    }
}
