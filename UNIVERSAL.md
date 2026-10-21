# NewPipeExtractor — Universal Layer

This fork adds a **universal (service-agnostic) layer** on top of the classic
NewPipeExtractor API, in the package
`org.schabi.newpipe.extractor.universal`.

## What it is for

In the classic API, a client has to know up front which service a URL belongs
to, pick the matching extractor itself and work with a different set of model
classes per content type. The universal layer removes that coupling:

- **Paste any supported URL** — the service is detected from the URL and the
  result always comes back as the same immutable model (`UnifiedMedia`),
  whether the link points to a stream, a channel or a playlist.
- **Search everything at once** — one `searchAll(query)` call queries every
  supported service and merges the results into one list of `UnifiedItem`s,
  each tagged with the service it came from.
- **Trending across services** — `trendingAll()` merges the default kiosk
  (trending) page of every service the same way.
- **Failures are isolated** — if one service is down or rejects a query, that
  failure is reported in `UnifiedSearchResult.getErrorsByService()` instead of
  failing the whole call; the other services' results are returned normally.
- **Adding a service later does not change client code** — apps written
  against this layer keep working unchanged when the extractor gains a new
  service implementation.

Like the rest of the extractor, everything runs **on the user's device**
(through the `Downloader` the app provides), so requests originate from the
user's own network instead of a hosted server API.

## Supported services

| Service     | Streams | Channels | Playlists | Search | Trending |
|-------------|---------|----------|-----------|--------|----------|
| YouTube     | yes     | yes      | yes       | yes    | yes      |
| SoundCloud  | yes     | yes      | yes       | yes    | yes      |
| Bandcamp    | yes     | yes      | yes       | yes    | yes      |
| PeerTube    | yes     | yes      | yes       | yes    | yes      |
| media.ccc.de| yes     | yes      | –         | yes    | yes      |

(The exact capabilities of each service are also available at runtime through
`UniversalExtractor.supportedServices()`.)

## Usage

Initialize the extractor once, as usual, with your own `Downloader`
implementation:

```java
NewPipe.init(myDownloader,
        new Localization("en", "GB"),
        new ContentCountry("GB"));
```

Extract any supported URL — no service knowledge needed:

```java
UnifiedMedia media = UniversalExtractor.extract(
        "https://www.youtube.com/watch?v=9Dpqou5cI08");
// also works, unchanged:
//   "https://soundcloud.com/artist/track"
//   "https://artist.bandcamp.com/track/name"
//   "https://framatube.org/videos/watch/<id>"
//   "https://media.ccc.de/v/<id>"

System.out.println(media.getKind());        // STREAM, CHANNEL or PLAYLIST
System.out.println(media.getServiceName()); // e.g. "YouTube"
System.out.println(media.getName());
System.out.println(media.getDashMpdUrl());  // for Media3 / ExoPlayer
for (UnifiedStream stream : media.getVideoStreams()) {
    System.out.println(stream.getResolution() + " -> " + stream.getUrl());
}
for (UnifiedSubtitle sub : media.getSubtitles()) {
    System.out.println(sub.getLanguageTag() + " -> " + sub.getUrl());
}
for (UnifiedItem item : media.getRelatedItems()) {
    System.out.println(item.getServiceName() + ": " + item.getName());
}
```

Search all services at once:

```java
UnifiedSearchResult result = UniversalExtractor.searchAll("lofi");
for (UnifiedItem item : result.getItems()) {
    System.out.println(item.getServiceName() + " | "
            + item.getName() + " | " + item.getUrl());
}
result.getErrorsByService().forEach((service, error) ->
        System.out.println(service + " failed: " + error));
```

Trending (default kiosk) of all services:

```java
UnifiedSearchResult trending = UniversalExtractor.trendingAll();
```

Or search a single service by id:

```java
UnifiedSearchResult youtubeOnly =
        UniversalExtractor.search(ServiceList.YouTube.getServiceId(), "lofi");
```

## Honest limits

- "Universal" means **one API for every site the extractor has a service
  module for** — currently the five services listed above. A site without a
  service implementation (e.g. TikTok, Instagram, Vimeo) cannot be extracted
  until someone writes a service module for it; unknown URLs are rejected
  with an `ExtractionException`.
- Per-field availability depends on the service: a field a service does not
  expose is `null`, `-1` or an empty list in the unified model, never an
  exception. Only a failure to fetch the main page itself is thrown.
- Channel results carry the items of the channel's first tab (when the
  service exposes tabs); playlist results carry the first page of entries.
  Use the classic per-service extractors if you need paging or tabs in full.
