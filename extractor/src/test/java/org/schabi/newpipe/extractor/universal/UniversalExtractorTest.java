package org.schabi.newpipe.extractor.universal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.schabi.newpipe.extractor.InitNewPipeTest;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.ServiceList;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.downloader.Downloader;
import org.schabi.newpipe.extractor.downloader.Request;
import org.schabi.newpipe.extractor.downloader.Response;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;

import java.util.List;

/**
 * Tests for {@link UniversalExtractor} which only exercise the local
 * (network-free) URL detection and service listing.
 *
 * <p>
 * The downloader installed here throws on any request, so a test failure would
 * also reveal if a code path unexpectedly tried to use the network.
 * </p>
 */
public class UniversalExtractorTest {

    @BeforeAll
    public static void setUp() {
        InitNewPipeTest.initEmpty();
        // Init with a dummy downloader, as a downloader is required during
        // extractor initialization, but nothing may ever be transmitted.
        NewPipe.init(new Downloader() {
            @Override
            public Response execute(@NotNull final Request request) {
                throw new UnsupportedOperationException("No communication expected");
            }
        });
    }

    @Test
    public void detectYoutubeStream() throws Exception {
        final StreamingService service = UniversalExtractor.detectService(
                "https://www.youtube.com/watch?v=9Dpqou5cI08");
        assertEquals(ServiceList.YouTube.getServiceId(), service.getServiceId());
        assertEquals(StreamingService.LinkType.STREAM,
                service.getLinkTypeByUrl("https://www.youtube.com/watch?v=9Dpqou5cI08"));
    }

    @Test
    public void detectSoundcloudStream() throws Exception {
        final StreamingService service = UniversalExtractor.detectService(
                "https://soundcloud.com/liluzivert/15-ysl");
        assertEquals(ServiceList.SoundCloud.getServiceId(), service.getServiceId());
        assertEquals(StreamingService.LinkType.STREAM,
                service.getLinkTypeByUrl("https://soundcloud.com/liluzivert/15-ysl"));
    }

    @Test
    public void detectBandcampStream() throws Exception {
        final String url = "https://goodgoodblood-tl.bandcamp.com/track/when-it-all-wakes-up";
        final StreamingService service = UniversalExtractor.detectService(url);
        assertEquals(ServiceList.Bandcamp.getServiceId(), service.getServiceId());
        assertEquals(StreamingService.LinkType.STREAM, service.getLinkTypeByUrl(url));
    }

    @Test
    public void detectPeertubeStream() throws Exception {
        final String url =
                "https://framatube.org/videos/watch/9c9de5e8-0a1e-484a-b099-e80766180a6d";
        final StreamingService service = UniversalExtractor.detectService(url);
        assertEquals(ServiceList.PeerTube.getServiceId(), service.getServiceId());
        assertEquals(StreamingService.LinkType.STREAM, service.getLinkTypeByUrl(url));
    }

    @Test
    public void detectMediaCccStream() throws Exception {
        final String url = "https://media.ccc.de/v/"
                + "jhremote20-3001-abschlusspraesentation_jugend_hackt_remote_2020";
        final StreamingService service = UniversalExtractor.detectService(url);
        assertEquals(ServiceList.MediaCCC.getServiceId(), service.getServiceId());
        assertEquals(StreamingService.LinkType.STREAM, service.getLinkTypeByUrl(url));
    }

    @Test
    public void detectYoutubeChannelAndPlaylist() throws Exception {
        final StreamingService service = ServiceList.YouTube;
        assertEquals(StreamingService.LinkType.CHANNEL,
                service.getLinkTypeByUrl("https://www.youtube.com/channel/"
                        + "UC-lHJZR3Gqxm24_Vd_AJ5Yw"));
        assertEquals(StreamingService.LinkType.PLAYLIST,
                service.getLinkTypeByUrl("https://www.youtube.com/playlist?"
                        + "list=PLrAXtmErZgOeiKm4sgNOknGvNjby9efdf"));
    }

    @Test
    public void unknownUrlIsRejected() {
        assertThrows(ExtractionException.class, () -> UniversalExtractor.detectService(
                "https://example.com/some/random/page"));
    }

    @Test
    public void supportedServicesListsAllServices() {
        final List<UnifiedServiceInfo> services = UniversalExtractor.supportedServices();
        assertEquals(NewPipe.getServices().size(), services.size());
        assertTrue(services.stream()
                .anyMatch(info -> ServiceList.YouTube.getServiceId() == info.getServiceId()));
        assertTrue(services.stream().allMatch(info -> info.getBaseUrl() != null));
    }
}
