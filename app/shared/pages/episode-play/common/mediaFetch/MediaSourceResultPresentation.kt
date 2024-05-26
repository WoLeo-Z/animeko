package me.him188.ani.app.ui.subject.episode.mediaFetch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import me.him188.ani.app.ui.foundation.HasBackgroundScope
import me.him188.ani.app.ui.foundation.rememberBackgroundScope
import me.him188.ani.datasources.core.fetch.MediaSourceResult
import me.him188.ani.datasources.core.fetch.MediaSourceState

/**
 * 单个数据源的搜索结果
 */
@Stable
class MediaSourceResultPresentation(
    private val delegate: MediaSourceResult,
    override val backgroundScope: CoroutineScope,
) : HasBackgroundScope {
    val mediaSourceId get() = delegate.mediaSourceId
    val state: MediaSourceState by delegate.state.produceState()
    val isLoading by derivedStateOf { state is MediaSourceState.Working }
    val isDisabled by derivedStateOf { state is MediaSourceState.Disabled }
    val isFailed by derivedStateOf { state is MediaSourceState.Failed }

    val totalCount: Int by delegate.resultsIfEnabled
        .map { it.size }
        .produceState(0)

    fun restart() = delegate.restart()
}

/**
 * 在 [MediaSelector] 使用, 管理多个 [MediaSourceResultPresentation] 的结果
 */
@Stable
class MediaSelectorSourceResults(
    val list: List<MediaSourceResultPresentation>,
) {
    private companion object {
        // 按照结果数量从大到小; 把禁用的放在最后, 然后按照 id 排序
        private val listComparator = compareByDescending<MediaSourceResultPresentation> {
            if (it.isDisabled) {
                Int.MIN_VALUE
            } else {
                it.totalCount
            }
        }.thenComparing<String> { it.mediaSourceId }
    }

    val anyLoading by derivedStateOf { list.any { it.isLoading } }

    val listSorted by derivedStateOf {
        list.sortedWith(listComparator)
    }

    val enabledSourceCount by derivedStateOf { list.count { !it.isDisabled } }
    val totalSourceCount by derivedStateOf { list.size }
}

private val EmptyMediaSelectorSourceResults by lazy(LazyThreadSafetyMode.NONE) { MediaSelectorSourceResults(emptyList()) }

@Stable
fun emptyMediaSelectorSourceResults() = EmptyMediaSelectorSourceResults

@Composable
fun rememberMediaSelectorSourceResults(
    resultsPerSource: () -> List<MediaSourceResult>,
): MediaSelectorSourceResults {
    val background = rememberBackgroundScope()
    val results by remember { derivedStateOf(resultsPerSource) }
    return remember(background) {
        derivedStateOf {
            MediaSelectorSourceResults(results.map {
                MediaSourceResultPresentation(it, background.backgroundScope)
            })
        }
    }.value
}