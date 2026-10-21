/**
 * A service-agnostic ("universal") facade over the extractor.
 *
 * <p>
 * The classes in this package let clients work with every supported streaming service
 * through one uniform API: a URL of any supported site can be handed to
 * {@link org.schabi.newpipe.extractor.universal.UniversalExtractor} without knowing
 * which service it belongs to, searches and trending lists can be collected across all
 * services at once, and the results are always returned in the same immutable model
 * classes ({@link org.schabi.newpipe.extractor.universal.UnifiedMedia},
 * {@link org.schabi.newpipe.extractor.universal.UnifiedItem},
 * {@link org.schabi.newpipe.extractor.universal.UnifiedStream} and
 * {@link org.schabi.newpipe.extractor.universal.UnifiedSubtitle}), no matter which
 * service produced them.
 * </p>
 */
package org.schabi.newpipe.extractor.universal;
