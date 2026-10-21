package org.schabi.newpipe.extractor.universal;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A single list entry (a search result, a related item, a playlist entry, ...),
 * in a service-agnostic form.
 *
 * <p>
 * Every item knows which service it came from, so a client merging results of
 * several services can still tell them apart. Fields which only apply to stream
 * items ({@code uploaderName}, {@code durationSeconds} and {@code viewCount}) are
 * {@code null} or {@code -1} for other item types.
 * </p>
 */
public final class UnifiedItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int serviceId;
    @Nullable
    private final String serviceName;
    @Nullable
    private final String infoType;
    @Nullable
    private final String name;
    @Nullable
    private final String url;
    @Nullable
    private final String thumbnailUrl;
    @Nullable
    private final String uploaderName;
    private final long durationSeconds;
    private final long viewCount;

    private UnifiedItem(final Builder builder) {
        this.serviceId = builder.serviceId;
        this.serviceName = builder.serviceName;
        this.infoType = builder.infoType;
        this.name = builder.name;
        this.url = builder.url;
        this.thumbnailUrl = builder.thumbnailUrl;
        this.uploaderName = builder.uploaderName;
        this.durationSeconds = builder.durationSeconds;
        this.viewCount = builder.viewCount;
    }

    /**
     * Gets the id of the service this item belongs to.
     *
     * @return the service id
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Gets the name of the service this item belongs to.
     *
     * @return the service name, or {@code null} if it could not be determined
     */
    @Nullable
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the type of this item: {@code STREAM}, {@code CHANNEL}, {@code PLAYLIST}
     * or {@code COMMENT}.
     *
     * @return the item type name, or {@code null} if unknown
     */
    @Nullable
    public String getInfoType() {
        return infoType;
    }

    /**
     * Gets the name (title) of this item.
     *
     * @return the name, or {@code null} if not available
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Gets the URL of this item on its service.
     *
     * @return the URL, or {@code null} if not available
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * Gets the URL of the first thumbnail of this item.
     *
     * @return the thumbnail URL, or {@code null} if the item has no thumbnail
     */
    @Nullable
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Gets the name of the uploader of this item, for stream items.
     *
     * @return the uploader name, or {@code null} for other item types
     */
    @Nullable
    public String getUploaderName() {
        return uploaderName;
    }

    /**
     * Gets the duration of this item in seconds, for stream items.
     *
     * @return the duration in seconds, or {@code -1} for other item types
     */
    public long getDurationSeconds() {
        return durationSeconds;
    }

    /**
     * Gets the view count of this item, for stream items.
     *
     * @return the view count, or {@code -1} for other item types and if unknown
     */
    public long getViewCount() {
        return viewCount;
    }

    /**
     * Builder for {@link UnifiedItem}.
     */
    public static final class Builder {
        private int serviceId = -1;
        @Nullable
        private String serviceName;
        @Nullable
        private String infoType;
        @Nullable
        private String name;
        @Nullable
        private String url;
        @Nullable
        private String thumbnailUrl;
        @Nullable
        private String uploaderName;
        private long durationSeconds = -1;
        private long viewCount = -1;

        /**
         * Sets the id of the service the item belongs to.
         *
         * @param itemServiceId the service id
         * @return this builder
         */
        public Builder setServiceId(final int itemServiceId) {
            this.serviceId = itemServiceId;
            return this;
        }

        /**
         * Sets the name of the service the item belongs to.
         *
         * @param itemServiceName the service name
         * @return this builder
         */
        public Builder setServiceName(@Nullable final String itemServiceName) {
            this.serviceName = itemServiceName;
            return this;
        }

        /**
         * Sets the type of the item.
         *
         * @param itemInfoType the item type name
         * @return this builder
         */
        public Builder setInfoType(@Nullable final String itemInfoType) {
            this.infoType = itemInfoType;
            return this;
        }

        /**
         * Sets the name of the item.
         *
         * @param itemName the name
         * @return this builder
         */
        public Builder setName(@Nullable final String itemName) {
            this.name = itemName;
            return this;
        }

        /**
         * Sets the URL of the item.
         *
         * @param itemUrl the URL
         * @return this builder
         */
        public Builder setUrl(@Nullable final String itemUrl) {
            this.url = itemUrl;
            return this;
        }

        /**
         * Sets the URL of the first thumbnail of the item.
         *
         * @param itemThumbnailUrl the thumbnail URL
         * @return this builder
         */
        public Builder setThumbnailUrl(@Nullable final String itemThumbnailUrl) {
            this.thumbnailUrl = itemThumbnailUrl;
            return this;
        }

        /**
         * Sets the name of the uploader of the item.
         *
         * @param itemUploaderName the uploader name
         * @return this builder
         */
        public Builder setUploaderName(@Nullable final String itemUploaderName) {
            this.uploaderName = itemUploaderName;
            return this;
        }

        /**
         * Sets the duration of the item in seconds.
         *
         * @param itemDurationSeconds the duration in seconds
         * @return this builder
         */
        public Builder setDurationSeconds(final long itemDurationSeconds) {
            this.durationSeconds = itemDurationSeconds;
            return this;
        }

        /**
         * Sets the view count of the item.
         *
         * @param itemViewCount the view count
         * @return this builder
         */
        public Builder setViewCount(final long itemViewCount) {
            this.viewCount = itemViewCount;
            return this;
        }

        /**
         * Builds the {@link UnifiedItem}.
         *
         * @return the built item
         */
        public UnifiedItem build() {
            return new UnifiedItem(this);
        }
    }
}
