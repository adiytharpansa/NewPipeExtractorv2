package org.schabi.newpipe.extractor.universal;

import org.schabi.newpipe.extractor.Image;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.MediaFormat;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.channel.tabs.ChannelTabExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.kiosk.KioskList;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.stream.AudioStream;
import org.schabi.newpipe.extractor.stream.Description;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.SubtitlesStream;
import org.schabi.newpipe.extractor.stream.VideoStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A service-agnostic ("universal") facade over the extractor.
 *
 * <p>
 * Clients of this class never need to know which streaming service a URL belongs
 * to: {@link #extract(String)} detects the service from the URL and always returns
 * the same {@link UnifiedMedia} model, {@link #searchAll(String)} and
 * {@link #trendingAll()} collect items across all supported services at once and
 * {@link #supportedServices()} lists what is available.
 * </p>
 *
 * <p>
 * Before using this class, the extractor has to be initialized once with a
 * downloader, as usual:
 * </p>
 *
 * <pre>{@code
 * NewPipe.init(myDownloader, new Localization("en", "GB"), new ContentCountry("GB"));
 * UnifiedMedia media = UniversalExtractor.extract("https://www.youtube.com/watch?v=...");
 * }</pre>
 *
 * <p>
 * Only sites for which a service implementation exists can be handled (see
 * {@link #supportedServices()}); URLs of any other site are rejected with an
 * {@link ExtractionException}.
 * </p>
 */
public final class UniversalExtractor {

    private UniversalExtractor() {
    }

    /**
     * Detects which supported service a URL belongs to.
     *
     * <p>
     * This is a pure URL inspection and does not perform any network request.
     * </p>
     *
     * @param url the URL to inspect
     * @return the service handling the URL
     * @throws ExtractionException if no supported service can handle the URL
     */
    @Nonnull
    public static StreamingService detectService(@Nonnull final String url)
            throws ExtractionException {
        return NewPipe.getServiceByUrl(url);
    }

    /**
     * Lists the services supported by this facade.
     *
     * @return information about every supported service
     */
    @Nonnull
    public static List<UnifiedServiceInfo> supportedServices() {
        final List<UnifiedServiceInfo> result = new ArrayList<>();
        for (final StreamingService service : NewPipe.getServices()) {
            final List<String> capabilities = new ArrayList<>();
            for (final StreamingService.ServiceInfo.MediaCapability capability
                    : service.getServiceInfo().getMediaCapabilities()) {
                capabilities.add(capability.name());
            }
            result.add(new UnifiedServiceInfo(service.getServiceId(),
                    service.getServiceInfo().getName(), service.getBaseUrl(), capabilities));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Extracts a URL of any supported service into a {@link UnifiedMedia}.
     *
     * <p>
     * The service and the kind of content (stream, channel or playlist) are
     * detected from the URL. A failure to fetch the page is reported by throwing,
     * while individual fields which a service cannot provide are mapped to
     * {@code null}, {@code -1} or an empty list instead of failing the whole
     * extraction.
     * </p>
     *
     * @param url the URL to extract
     * @return the extracted media
     * @throws IOException if a network error occurs while fetching the page
     * @throws ExtractionException if the URL is not supported or cannot be parsed
     */
    @Nonnull
    public static UnifiedMedia extract(@Nonnull final String url)
            throws IOException, ExtractionException {
        final StreamingService service = detectService(url);
        final StreamingService.LinkType linkType = service.getLinkTypeByUrl(url);
        switch (linkType) {
            case STREAM:
                return extractStream(service, url);
            case CHANNEL:
                return extractChannel(service, url);
            case PLAYLIST:
                return extractPlaylist(service, url);
            default:
                throw new ExtractionException(
                        "No service can handle the url = \"" + url + "\"");
        }
    }

    /**
     * Searches all supported services for a query and merges the results.
     *
     * <p>
     * A service which fails does not fail the whole search: its failure is
     * recorded in {@link UnifiedSearchResult#getErrorsByService()} and the items
     * of the other services are returned as usual.
     * </p>
     *
     * @param query the search query
     * @return the merged search result
     */
    @Nonnull
    public static UnifiedSearchResult searchAll(@Nonnull final String query) {
        final List<UnifiedItem> items = new ArrayList<>();
        final Map<String, String> errors = new LinkedHashMap<>();
        for (final StreamingService service : NewPipe.getServices()) {
            try {
                items.addAll(searchService(service, query));
            } catch (final Exception e) {
                errors.put(service.getServiceInfo().getName(), describeError(e));
            }
        }
        return new UnifiedSearchResult(items, errors);
    }

    /**
     * Searches a single service for a query.
     *
     * @param serviceId the id of the service to search, see {@link #supportedServices()}
     * @param query the search query
     * @return the search result of that service
     * @throws IOException if a network error occurs while fetching the page
     * @throws ExtractionException if the service id is unknown or the search fails
     */
    @Nonnull
    public static UnifiedSearchResult search(final int serviceId,
                                             @Nonnull final String query)
            throws IOException, ExtractionException {
        final StreamingService service = NewPipe.getService(serviceId);
        final List<UnifiedItem> items = searchService(service, query);
        return new UnifiedSearchResult(items, Collections.emptyMap());
    }

    /**
     * Collects the default trending (kiosk) items of all supported services.
     *
     * <p>
     * Like {@link #searchAll(String)}, failures of single services are recorded
     * in {@link UnifiedSearchResult#getErrorsByService()} instead of being thrown.
     * </p>
     *
     * @return the merged trending items
     */
    @Nonnull
    public static UnifiedSearchResult trendingAll() {
        final List<UnifiedItem> items = new ArrayList<>();
        final Map<String, String> errors = new LinkedHashMap<>();
        for (final StreamingService service : NewPipe.getServices()) {
            try {
                items.addAll(trendingService(service));
            } catch (final Exception e) {
                errors.put(service.getServiceInfo().getName(), describeError(e));
            }
        }
        return new UnifiedSearchResult(items, errors);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Extraction of the single link types
    //////////////////////////////////////////////////////////////////////////*/

    @Nonnull
    private static UnifiedMedia extractStream(@Nonnull final StreamingService service,
                                              @Nonnull final String url)
            throws IOException, ExtractionException {
        final StreamExtractor extractor = service.getStreamExtractor(url);
        extractor.fetchPage();
        return baseBuilder(service, url, UnifiedMedia.Kind.STREAM)
                .setName(safe(extractor::getName, null))
                .setUploaderName(safe(extractor::getUploaderName, null))
                .setUploaderUrl(safe(extractor::getUploaderUrl, null))
                .setDescription(safe(() -> descriptionContent(extractor.getDescription()),
                        null))
                .setLengthSeconds(safe(extractor::getLength, -1L))
                .setViewCount(safe(extractor::getViewCount, -1L))
                .setLikeCount(safe(extractor::getLikeCount, -1L))
                .setThumbnailUrls(imageUrls(safe(extractor::getThumbnails,
                        Collections.emptyList())))
                .setDashMpdUrl(safe(extractor::getDashMpdUrl, null))
                .setHlsUrl(safe(extractor::getHlsUrl, null))
                .setVideoStreams(toVideoStreams(safe(extractor::getVideoStreams,
                        Collections.emptyList())))
                .setVideoOnlyStreams(toVideoStreams(safe(extractor::getVideoOnlyStreams,
                        Collections.emptyList())))
                .setAudioStreams(toAudioStreams(safe(extractor::getAudioStreams,
                        Collections.emptyList())))
                .setSubtitles(toSubtitles(safe(extractor::getSubtitlesDefault,
                        Collections.emptyList())))
                .setRelatedItems(toItems(safe(
                        () -> extractor.getRelatedStreams().getItems(),
                        Collections.emptyList())))
                .setStreamType(safe(() -> extractor.getStreamType().name(), null))
                .setTags(safe(extractor::getTags, Collections.emptyList()))
                .build();
    }

    @Nonnull
    private static UnifiedMedia extractChannel(@Nonnull final StreamingService service,
                                               @Nonnull final String url)
            throws IOException, ExtractionException {
        final ChannelExtractor extractor = service.getChannelExtractor(url);
        extractor.fetchPage();
        return baseBuilder(service, url, UnifiedMedia.Kind.CHANNEL)
                .setName(safe(extractor::getName, null))
                .setDescription(safe(extractor::getDescription, null))
                .setThumbnailUrls(imageUrls(safe(extractor::getAvatars,
                        Collections.emptyList())))
                .setTags(safe(extractor::getTags, Collections.emptyList()))
                .setRelatedItems(channelItems(service, extractor))
                .build();
    }

    @Nonnull
    private static UnifiedMedia extractPlaylist(@Nonnull final StreamingService service,
                                                @Nonnull final String url)
            throws IOException, ExtractionException {
        final PlaylistExtractor extractor = service.getPlaylistExtractor(url);
        extractor.fetchPage();
        return baseBuilder(service, url, UnifiedMedia.Kind.PLAYLIST)
                .setName(safe(extractor::getName, null))
                .setUploaderName(safe(extractor::getUploaderName, null))
                .setUploaderUrl(safe(extractor::getUploaderUrl, null))
                .setDescription(safe(() -> descriptionContent(extractor.getDescription()),
                        null))
                .setThumbnailUrls(imageUrls(safe(extractor::getThumbnails,
                        Collections.emptyList())))
                .setRelatedItems(toItems(safe(
                        () -> extractor.getInitialPage().getItems(),
                        Collections.emptyList())))
                .build();
    }

    @Nonnull
    private static List<UnifiedItem> channelItems(@Nonnull final StreamingService service,
                                                  @Nonnull final ChannelExtractor extractor) {
        return safe(() -> {
            final List<ListLinkHandler> tabs = extractor.getTabs();
            if (tabs == null || tabs.isEmpty()) {
                return Collections.emptyList();
            }
            final ChannelTabExtractor tabExtractor =
                    service.getChannelTabExtractor(tabs.get(0));
            if (tabExtractor == null) {
                return Collections.emptyList();
            }
            tabExtractor.fetchPage();
            return toItems(tabExtractor.getInitialPage().getItems());
        }, Collections.emptyList());
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Search and trending
    //////////////////////////////////////////////////////////////////////////*/

    @Nonnull
    private static List<UnifiedItem> searchService(@Nonnull final StreamingService service,
                                                   @Nonnull final String query)
            throws IOException, ExtractionException {
        final SearchExtractor extractor = service.getSearchExtractor(query);
        extractor.fetchPage();
        return toItems(extractor.getInitialPage().getItems());
    }

    @Nonnull
    private static List<UnifiedItem> trendingService(@Nonnull final StreamingService service)
            throws IOException, ExtractionException {
        final KioskList kioskList = service.getKioskList();
        if (kioskList == null) {
            return Collections.emptyList();
        }
        final KioskExtractor<?> kioskExtractor = kioskList.getDefaultKioskExtractor();
        if (kioskExtractor == null) {
            return Collections.emptyList();
        }
        kioskExtractor.fetchPage();
        return toItems(kioskExtractor.getInitialPage().getItems());
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Mapping helpers
    //////////////////////////////////////////////////////////////////////////*/

    @Nonnull
    private static UnifiedMedia.Builder baseBuilder(@Nonnull final StreamingService service,
                                                    @Nonnull final String url,
                                                    @Nonnull final UnifiedMedia.Kind kind) {
        return new UnifiedMedia.Builder()
                .setServiceId(service.getServiceId())
                .setServiceName(service.getServiceInfo().getName())
                .setOriginalUrl(url)
                .setKind(kind);
    }

    @Nonnull
    private static List<UnifiedItem> toItems(@Nonnull final List<? extends InfoItem> items) {
        final List<UnifiedItem> result = new ArrayList<>();
        for (final InfoItem item : items) {
            final UnifiedItem unified = safe(() -> toItem(item), null);
            if (unified != null) {
                result.add(unified);
            }
        }
        return result;
    }

    @Nonnull
    private static UnifiedItem toItem(@Nonnull final InfoItem item) {
        final UnifiedItem.Builder builder = new UnifiedItem.Builder()
                .setServiceId(item.getServiceId())
                .setServiceName(serviceName(item.getServiceId()))
                .setInfoType(item.getInfoType() == null ? null : item.getInfoType().name())
                .setName(item.getName())
                .setUrl(item.getUrl())
                .setThumbnailUrl(firstImageUrl(item.getThumbnails()));
        if (item instanceof StreamInfoItem) {
            final StreamInfoItem streamItem = (StreamInfoItem) item;
            builder.setUploaderName(streamItem.getUploaderName())
                    .setDurationSeconds(streamItem.getDuration())
                    .setViewCount(streamItem.getViewCount());
        }
        return builder.build();
    }

    @Nonnull
    private static List<UnifiedStream> toVideoStreams(
            @Nonnull final List<VideoStream> streams) {
        final List<UnifiedStream> result = new ArrayList<>();
        for (final VideoStream stream : streams) {
            final UnifiedStream unified = safe(() -> toUnifiedStream(stream), null);
            if (unified != null) {
                result.add(unified);
            }
        }
        return result;
    }

    @Nonnull
    private static UnifiedStream toUnifiedStream(@Nonnull final VideoStream stream) {
        return new UnifiedStream.Builder()
                .setUrl(stream.getContent())
                .setFormat(formatName(stream.getFormat()))
                .setResolution(stream.getResolution())
                .setQuality(stream.getQuality())
                .setBitrate(stream.getBitrate())
                .setCodec(stream.getCodec())
                .setVideoOnly(stream.isVideoOnly())
                .build();
    }

    @Nonnull
    private static List<UnifiedStream> toAudioStreams(
            @Nonnull final List<AudioStream> streams) {
        final List<UnifiedStream> result = new ArrayList<>();
        for (final AudioStream stream : streams) {
            final UnifiedStream unified = safe(() -> toUnifiedStream(stream), null);
            if (unified != null) {
                result.add(unified);
            }
        }
        return result;
    }

    @Nonnull
    private static UnifiedStream toUnifiedStream(@Nonnull final AudioStream stream) {
        return new UnifiedStream.Builder()
                .setUrl(stream.getContent())
                .setFormat(formatName(stream.getFormat()))
                .setQuality(stream.getQuality())
                .setBitrate(stream.getBitrate())
                .setCodec(stream.getCodec())
                .setAverageBitrate(stream.getAverageBitrate())
                .build();
    }

    @Nonnull
    private static List<UnifiedSubtitle> toSubtitles(
            @Nonnull final List<SubtitlesStream> streams) {
        final List<UnifiedSubtitle> result = new ArrayList<>();
        for (final SubtitlesStream stream : streams) {
            final UnifiedSubtitle unified = safe(() -> toSubtitle(stream), null);
            if (unified != null) {
                result.add(unified);
            }
        }
        return result;
    }

    @Nonnull
    private static UnifiedSubtitle toSubtitle(@Nonnull final SubtitlesStream stream) {
        return new UnifiedSubtitle(stream.getContent(), stream.getLanguageTag(),
                stream.getDisplayLanguageName(), stream.getExtension(),
                stream.isAutoGenerated());
    }

    @Nullable
    private static String descriptionContent(@Nullable final Description description) {
        return description == null ? null : description.content();
    }

    @Nullable
    private static String formatName(@Nullable final MediaFormat format) {
        return format == null ? null : format.getName();
    }

    @Nullable
    private static String serviceName(final int serviceId) {
        return safe(() -> NewPipe.getService(serviceId).getServiceInfo().getName(), null);
    }

    @Nonnull
    private static List<String> imageUrls(@Nonnull final List<Image> images) {
        final List<String> result = new ArrayList<>();
        for (final Image image : images) {
            if (image != null && image.getUrl() != null) {
                result.add(image.getUrl());
            }
        }
        return result;
    }

    @Nullable
    private static String firstImageUrl(@Nullable final List<Image> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.get(0).getUrl();
    }

    @Nonnull
    private static String describeError(@Nonnull final Exception exception) {
        final String message = exception.getMessage();
        return message == null ? exception.getClass().getSimpleName() : message;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Safe execution helper
    //////////////////////////////////////////////////////////////////////////*/

    private interface SafeCall<T> {
        T call() throws Exception;
    }

    @Nullable
    private static <T> T safe(@Nonnull final SafeCall<T> call,
                              @Nullable final T fallback) {
        try {
            return call.call();
        } catch (final Exception e) {
            return fallback;
        }
    }
}
