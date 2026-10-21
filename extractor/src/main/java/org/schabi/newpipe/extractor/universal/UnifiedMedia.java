package org.schabi.newpipe.extractor.universal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The result of extracting a single URL, in a service-agnostic form.
 *
 * <p>
 * The {@link Kind} tells whether the URL pointed to a stream, a channel or a
 * playlist. Stream-specific fields (stream lists, subtitles, DASH/HLS manifest
 * URLs, view and like counts) are only filled for streams; channels and playlists
 * carry their first page of items in {@link #getRelatedItems()}. Fields a service
 * does not provide are {@code null}, {@code -1} or an empty list.
 * </p>
 */
public final class UnifiedMedia implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The kind of content a {@link UnifiedMedia} describes.
     */
    public enum Kind {
        STREAM,
        CHANNEL,
        PLAYLIST
    }

    private final int serviceId;
    @Nullable
    private final String serviceName;
    @Nullable
    private final String originalUrl;
    @Nonnull
    private final Kind kind;
    @Nullable
    private final String name;
    @Nullable
    private final String uploaderName;
    @Nullable
    private final String uploaderUrl;
    @Nullable
    private final String description;
    private final long lengthSeconds;
    private final long viewCount;
    private final long likeCount;
    @Nonnull
    private final List<String> thumbnailUrls;
    @Nullable
    private final String dashMpdUrl;
    @Nullable
    private final String hlsUrl;
    @Nonnull
    private final List<UnifiedStream> videoStreams;
    @Nonnull
    private final List<UnifiedStream> videoOnlyStreams;
    @Nonnull
    private final List<UnifiedStream> audioStreams;
    @Nonnull
    private final List<UnifiedSubtitle> subtitles;
    @Nonnull
    private final List<UnifiedItem> relatedItems;
    @Nullable
    private final String streamType;
    @Nonnull
    private final List<String> tags;

    private UnifiedMedia(final Builder builder) {
        this.serviceId = builder.serviceId;
        this.serviceName = builder.serviceName;
        this.originalUrl = builder.originalUrl;
        this.kind = builder.kind;
        this.name = builder.name;
        this.uploaderName = builder.uploaderName;
        this.uploaderUrl = builder.uploaderUrl;
        this.description = builder.description;
        this.lengthSeconds = builder.lengthSeconds;
        this.viewCount = builder.viewCount;
        this.likeCount = builder.likeCount;
        this.thumbnailUrls = immutableCopy(builder.thumbnailUrls);
        this.dashMpdUrl = builder.dashMpdUrl;
        this.hlsUrl = builder.hlsUrl;
        this.videoStreams = immutableCopy(builder.videoStreams);
        this.videoOnlyStreams = immutableCopy(builder.videoOnlyStreams);
        this.audioStreams = immutableCopy(builder.audioStreams);
        this.subtitles = immutableCopy(builder.subtitles);
        this.relatedItems = immutableCopy(builder.relatedItems);
        this.streamType = builder.streamType;
        this.tags = immutableCopy(builder.tags);
    }

    private static <T> List<T> immutableCopy(@Nonnull final List<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    /**
     * Gets the id of the service this media belongs to.
     *
     * @return the service id
     */
    public int getServiceId() {
        return serviceId;
    }

    /**
     * Gets the name of the service this media belongs to.
     *
     * @return the service name, or {@code null} if it could not be determined
     */
    @Nullable
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the URL this media was extracted from.
     *
     * @return the original URL
     */
    @Nullable
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * Gets the kind of this media.
     *
     * @return the kind of this media
     */
    @Nonnull
    public Kind getKind() {
        return kind;
    }

    /**
     * Gets the name (title) of this media.
     *
     * @return the name, or {@code null} if it could not be extracted
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Gets the name of the uploader of this media.
     *
     * @return the uploader name, or {@code null} if not available
     */
    @Nullable
    public String getUploaderName() {
        return uploaderName;
    }

    /**
     * Gets the URL of the uploader of this media.
     *
     * @return the uploader URL, or {@code null} if not available
     */
    @Nullable
    public String getUploaderUrl() {
        return uploaderUrl;
    }

    /**
     * Gets the description of this media.
     *
     * @return the description, or {@code null} if not available
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Gets the length of this media in seconds, for streams.
     *
     * @return the length in seconds, or {@code -1} if not applicable or unknown
     */
    public long getLengthSeconds() {
        return lengthSeconds;
    }

    /**
     * Gets the view count of this media, for streams.
     *
     * @return the view count, or {@code -1} if not applicable or unknown
     */
    public long getViewCount() {
        return viewCount;
    }

    /**
     * Gets the like count of this media, for streams.
     *
     * @return the like count, or {@code -1} if not applicable or unknown
     */
    public long getLikeCount() {
        return likeCount;
    }

    /**
     * Gets the URLs of all thumbnails of this media.
     *
     * @return the thumbnail URLs, never {@code null}
     */
    @Nonnull
    public List<String> getThumbnailUrls() {
        return thumbnailUrls;
    }

    /**
     * Gets the URL of the DASH manifest of this media, for streams.
     *
     * @return the DASH manifest URL, or {@code null} if not available
     */
    @Nullable
    public String getDashMpdUrl() {
        return dashMpdUrl;
    }

    /**
     * Gets the URL of the HLS manifest of this media, for streams.
     *
     * @return the HLS manifest URL, or {@code null} if not available
     */
    @Nullable
    public String getHlsUrl() {
        return hlsUrl;
    }

    /**
     * Gets the progressive video streams of this media (audio and video combined).
     *
     * @return the video streams, never {@code null}
     */
    @Nonnull
    public List<UnifiedStream> getVideoStreams() {
        return videoStreams;
    }

    /**
     * Gets the video-only streams of this media (video without audio).
     *
     * @return the video-only streams, never {@code null}
     */
    @Nonnull
    public List<UnifiedStream> getVideoOnlyStreams() {
        return videoOnlyStreams;
    }

    /**
     * Gets the audio streams of this media.
     *
     * @return the audio streams, never {@code null}
     */
    @Nonnull
    public List<UnifiedStream> getAudioStreams() {
        return audioStreams;
    }

    /**
     * Gets the subtitle tracks of this media.
     *
     * @return the subtitle tracks, never {@code null}
     */
    @Nonnull
    public List<UnifiedSubtitle> getSubtitles() {
        return subtitles;
    }

    /**
     * Gets the items related to this media.
     *
     * <p>
     * For streams these are the related streams, for playlists the entries of the
     * first page and for channels the items of the first channel tab, if any.
     * </p>
     *
     * @return the related items, never {@code null}
     */
    @Nonnull
    public List<UnifiedItem> getRelatedItems() {
        return relatedItems;
    }

    /**
     * Gets the stream type of this media, e.g. {@code VIDEO_STREAM} or
     * {@code LIVE_STREAM}, for streams.
     *
     * @return the stream type name, or {@code null} if not applicable
     */
    @Nullable
    public String getStreamType() {
        return streamType;
    }

    /**
     * Gets the tags of this media.
     *
     * @return the tags, never {@code null}
     */
    @Nonnull
    public List<String> getTags() {
        return tags;
    }

    /**
     * Builder for {@link UnifiedMedia}.
     */
    public static final class Builder {
        private int serviceId = -1;
        @Nullable
        private String serviceName;
        @Nullable
        private String originalUrl;
        @Nonnull
        private Kind kind = Kind.STREAM;
        @Nullable
        private String name;
        @Nullable
        private String uploaderName;
        @Nullable
        private String uploaderUrl;
        @Nullable
        private String description;
        private long lengthSeconds = -1;
        private long viewCount = -1;
        private long likeCount = -1;
        @Nonnull
        private List<String> thumbnailUrls = Collections.emptyList();
        @Nullable
        private String dashMpdUrl;
        @Nullable
        private String hlsUrl;
        @Nonnull
        private List<UnifiedStream> videoStreams = Collections.emptyList();
        @Nonnull
        private List<UnifiedStream> videoOnlyStreams = Collections.emptyList();
        @Nonnull
        private List<UnifiedStream> audioStreams = Collections.emptyList();
        @Nonnull
        private List<UnifiedSubtitle> subtitles = Collections.emptyList();
        @Nonnull
        private List<UnifiedItem> relatedItems = Collections.emptyList();
        @Nullable
        private String streamType;
        @Nonnull
        private List<String> tags = Collections.emptyList();

        /**
         * Sets the id of the service the media belongs to.
         *
         * @param mediaServiceId the service id
         * @return this builder
         */
        public Builder setServiceId(final int mediaServiceId) {
            this.serviceId = mediaServiceId;
            return this;
        }

        /**
         * Sets the name of the service the media belongs to.
         *
         * @param mediaServiceName the service name
         * @return this builder
         */
        public Builder setServiceName(@Nullable final String mediaServiceName) {
            this.serviceName = mediaServiceName;
            return this;
        }

        /**
         * Sets the URL the media was extracted from.
         *
         * @param mediaOriginalUrl the original URL
         * @return this builder
         */
        public Builder setOriginalUrl(@Nullable final String mediaOriginalUrl) {
            this.originalUrl = mediaOriginalUrl;
            return this;
        }

        /**
         * Sets the kind of the media.
         *
         * @param mediaKind the kind
         * @return this builder
         */
        public Builder setKind(@Nonnull final Kind mediaKind) {
            this.kind = mediaKind;
            return this;
        }

        /**
         * Sets the name of the media.
         *
         * @param mediaName the name
         * @return this builder
         */
        public Builder setName(@Nullable final String mediaName) {
            this.name = mediaName;
            return this;
        }

        /**
         * Sets the name of the uploader of the media.
         *
         * @param mediaUploaderName the uploader name
         * @return this builder
         */
        public Builder setUploaderName(@Nullable final String mediaUploaderName) {
            this.uploaderName = mediaUploaderName;
            return this;
        }

        /**
         * Sets the URL of the uploader of the media.
         *
         * @param mediaUploaderUrl the uploader URL
         * @return this builder
         */
        public Builder setUploaderUrl(@Nullable final String mediaUploaderUrl) {
            this.uploaderUrl = mediaUploaderUrl;
            return this;
        }

        /**
         * Sets the description of the media.
         *
         * @param mediaDescription the description
         * @return this builder
         */
        public Builder setDescription(@Nullable final String mediaDescription) {
            this.description = mediaDescription;
            return this;
        }

        /**
         * Sets the length of the media in seconds.
         *
         * @param mediaLengthSeconds the length in seconds
         * @return this builder
         */
        public Builder setLengthSeconds(final long mediaLengthSeconds) {
            this.lengthSeconds = mediaLengthSeconds;
            return this;
        }

        /**
         * Sets the view count of the media.
         *
         * @param mediaViewCount the view count
         * @return this builder
         */
        public Builder setViewCount(final long mediaViewCount) {
            this.viewCount = mediaViewCount;
            return this;
        }

        /**
         * Sets the like count of the media.
         *
         * @param mediaLikeCount the like count
         * @return this builder
         */
        public Builder setLikeCount(final long mediaLikeCount) {
            this.likeCount = mediaLikeCount;
            return this;
        }

        /**
         * Sets the URLs of the thumbnails of the media.
         *
         * @param mediaThumbnailUrls the thumbnail URLs
         * @return this builder
         */
        public Builder setThumbnailUrls(@Nonnull final List<String> mediaThumbnailUrls) {
            this.thumbnailUrls = mediaThumbnailUrls;
            return this;
        }

        /**
         * Sets the URL of the DASH manifest of the media.
         *
         * @param mediaDashMpdUrl the DASH manifest URL
         * @return this builder
         */
        public Builder setDashMpdUrl(@Nullable final String mediaDashMpdUrl) {
            this.dashMpdUrl = mediaDashMpdUrl;
            return this;
        }

        /**
         * Sets the URL of the HLS manifest of the media.
         *
         * @param mediaHlsUrl the HLS manifest URL
         * @return this builder
         */
        public Builder setHlsUrl(@Nullable final String mediaHlsUrl) {
            this.hlsUrl = mediaHlsUrl;
            return this;
        }

        /**
         * Sets the progressive video streams of the media.
         *
         * @param mediaVideoStreams the video streams
         * @return this builder
         */
        public Builder setVideoStreams(@Nonnull final List<UnifiedStream> mediaVideoStreams) {
            this.videoStreams = mediaVideoStreams;
            return this;
        }

        /**
         * Sets the video-only streams of the media.
         *
         * @param mediaVideoOnlyStreams the video-only streams
         * @return this builder
         */
        public Builder setVideoOnlyStreams(
                @Nonnull final List<UnifiedStream> mediaVideoOnlyStreams) {
            this.videoOnlyStreams = mediaVideoOnlyStreams;
            return this;
        }

        /**
         * Sets the audio streams of the media.
         *
         * @param mediaAudioStreams the audio streams
         * @return this builder
         */
        public Builder setAudioStreams(@Nonnull final List<UnifiedStream> mediaAudioStreams) {
            this.audioStreams = mediaAudioStreams;
            return this;
        }

        /**
         * Sets the subtitle tracks of the media.
         *
         * @param mediaSubtitles the subtitle tracks
         * @return this builder
         */
        public Builder setSubtitles(@Nonnull final List<UnifiedSubtitle> mediaSubtitles) {
            this.subtitles = mediaSubtitles;
            return this;
        }

        /**
         * Sets the items related to the media.
         *
         * @param mediaRelatedItems the related items
         * @return this builder
         */
        public Builder setRelatedItems(@Nonnull final List<UnifiedItem> mediaRelatedItems) {
            this.relatedItems = mediaRelatedItems;
            return this;
        }

        /**
         * Sets the stream type of the media.
         *
         * @param mediaStreamType the stream type name
         * @return this builder
         */
        public Builder setStreamType(@Nullable final String mediaStreamType) {
            this.streamType = mediaStreamType;
            return this;
        }

        /**
         * Sets the tags of the media.
         *
         * @param mediaTags the tags
         * @return this builder
         */
        public Builder setTags(@Nonnull final List<String> mediaTags) {
            this.tags = mediaTags;
            return this;
        }

        /**
         * Builds the {@link UnifiedMedia}.
         *
         * @return the built media
         */
        public UnifiedMedia build() {
            return new UnifiedMedia(this);
        }
    }
}
