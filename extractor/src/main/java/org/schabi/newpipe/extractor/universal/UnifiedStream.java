package org.schabi.newpipe.extractor.universal;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * A single playable stream of a {@link UnifiedMedia}, in a service-agnostic form.
 *
 * <p>
 * Instances are created from either a video stream (progressive or video-only) or an
 * audio stream of the underlying service. Fields which do not apply to a kind of
 * stream, or which the service did not provide, are {@code null} (for objects) or
 * {@code -1} (for bitrates).
 * </p>
 */
public final class UnifiedStream implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private final String url;
    @Nullable
    private final String format;
    @Nullable
    private final String resolution;
    @Nullable
    private final String quality;
    private final int bitrate;
    @Nullable
    private final String codec;
    private final boolean videoOnly;
    private final int averageBitrate;

    private UnifiedStream(final Builder builder) {
        this.url = builder.url;
        this.format = builder.format;
        this.resolution = builder.resolution;
        this.quality = builder.quality;
        this.bitrate = builder.bitrate;
        this.codec = builder.codec;
        this.videoOnly = builder.videoOnly;
        this.averageBitrate = builder.averageBitrate;
    }

    /**
     * Gets the direct content of the stream, usually a media URL.
     *
     * @return the stream content, or {@code null} if not available
     */
    @Nullable
    public String getUrl() {
        return url;
    }

    /**
     * Gets the name of the media format of the stream, e.g. {@code "MPEG-4"}.
     *
     * @return the format name, or {@code null} if unknown
     */
    @Nullable
    public String getFormat() {
        return format;
    }

    /**
     * Gets the resolution of a video stream, e.g. {@code "720p"}.
     *
     * @return the resolution, or {@code null} for audio streams and if unknown
     */
    @Nullable
    public String getResolution() {
        return resolution;
    }

    /**
     * Gets the quality label of the stream, as reported by the service.
     *
     * @return the quality label, or {@code null} if unknown
     */
    @Nullable
    public String getQuality() {
        return quality;
    }

    /**
     * Gets the bitrate of the stream in bits per second.
     *
     * @return the bitrate, or {@code -1} if unknown
     */
    public int getBitrate() {
        return bitrate;
    }

    /**
     * Gets the codec of the stream.
     *
     * @return the codec, or {@code null} if unknown
     */
    @Nullable
    public String getCodec() {
        return codec;
    }

    /**
     * Checks whether this stream carries video without audio.
     *
     * @return {@code true} if this is a video-only stream, {@code false} otherwise
     */
    public boolean isVideoOnly() {
        return videoOnly;
    }

    /**
     * Gets the average bitrate of an audio stream in bits per second.
     *
     * @return the average bitrate, or {@code -1} for video streams and if unknown
     */
    public int getAverageBitrate() {
        return averageBitrate;
    }

    /**
     * Builder for {@link UnifiedStream}.
     */
    public static final class Builder {
        @Nullable
        private String url;
        @Nullable
        private String format;
        @Nullable
        private String resolution;
        @Nullable
        private String quality;
        private int bitrate = -1;
        @Nullable
        private String codec;
        private boolean videoOnly;
        private int averageBitrate = -1;

        /**
         * Sets the direct content of the stream.
         *
         * @param streamUrl the stream content, usually a media URL
         * @return this builder
         */
        public Builder setUrl(@Nullable final String streamUrl) {
            this.url = streamUrl;
            return this;
        }

        /**
         * Sets the name of the media format of the stream.
         *
         * @param formatName the format name
         * @return this builder
         */
        public Builder setFormat(@Nullable final String formatName) {
            this.format = formatName;
            return this;
        }

        /**
         * Sets the resolution of the stream.
         *
         * @param streamResolution the resolution
         * @return this builder
         */
        public Builder setResolution(@Nullable final String streamResolution) {
            this.resolution = streamResolution;
            return this;
        }

        /**
         * Sets the quality label of the stream.
         *
         * @param qualityLabel the quality label
         * @return this builder
         */
        public Builder setQuality(@Nullable final String qualityLabel) {
            this.quality = qualityLabel;
            return this;
        }

        /**
         * Sets the bitrate of the stream in bits per second.
         *
         * @param streamBitrate the bitrate
         * @return this builder
         */
        public Builder setBitrate(final int streamBitrate) {
            this.bitrate = streamBitrate;
            return this;
        }

        /**
         * Sets the codec of the stream.
         *
         * @param codecName the codec
         * @return this builder
         */
        public Builder setCodec(@Nullable final String codecName) {
            this.codec = codecName;
            return this;
        }

        /**
         * Sets whether the stream carries video without audio.
         *
         * @param isVideoOnly whether the stream is video-only
         * @return this builder
         */
        public Builder setVideoOnly(final boolean isVideoOnly) {
            this.videoOnly = isVideoOnly;
            return this;
        }

        /**
         * Sets the average bitrate of an audio stream in bits per second.
         *
         * @param streamAverageBitrate the average bitrate
         * @return this builder
         */
        public Builder setAverageBitrate(final int streamAverageBitrate) {
            this.averageBitrate = streamAverageBitrate;
            return this;
        }

        /**
         * Builds the {@link UnifiedStream}.
         *
         * @return the built stream
         */
        public UnifiedStream build() {
            return new UnifiedStream(this);
        }
    }
}
