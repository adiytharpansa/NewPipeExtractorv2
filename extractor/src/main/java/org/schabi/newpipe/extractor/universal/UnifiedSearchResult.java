package org.schabi.newpipe.extractor.universal;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The result of a search or trending collection across one or more services.
 *
 * <p>
 * Collecting from several services is best-effort: a service which fails does not
 * fail the whole result. Instead, its name is mapped to the failure message in
 * {@link #getErrorsByService()}, while the items of the successful services are
 * returned as usual.
 * </p>
 */
public final class UnifiedSearchResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @Nonnull
    private final List<UnifiedItem> items;
    @Nonnull
    private final Map<String, String> errorsByService;

    /**
     * Creates a new search result.
     *
     * @param items the collected items of all successful services
     * @param errorsByService the failure messages, keyed by service name
     */
    public UnifiedSearchResult(@Nonnull final List<UnifiedItem> items,
                               @Nonnull final Map<String, String> errorsByService) {
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.errorsByService = Collections.unmodifiableMap(new LinkedHashMap<>(errorsByService));
    }

    /**
     * Gets the collected items.
     *
     * @return the items, never {@code null}
     */
    @Nonnull
    public List<UnifiedItem> getItems() {
        return items;
    }

    /**
     * Gets the failures of the services which could not be collected.
     *
     * @return the failure messages, keyed by service name, never {@code null}
     */
    @Nonnull
    public Map<String, String> getErrorsByService() {
        return errorsByService;
    }

    /**
     * Checks whether every queried service succeeded.
     *
     * @return {@code true} if no service failed, {@code false} otherwise
     */
    public boolean isComplete() {
        return errorsByService.isEmpty();
    }
}
