/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.adaptive

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.him188.ani.app.ui.foundation.layout.AniWindowInsets
import me.him188.ani.app.ui.foundation.layout.AnimatedPane1
import me.him188.ani.app.ui.foundation.layout.paneHorizontalPadding
import me.him188.ani.app.ui.foundation.navigation.BackHandler


/**
 * 自动适应单页模式和双页模式的布局的 paddings
 *
 * Pane 内可以访问 [PaneScope]. 其中有几个非常实用的属性:
 * - [PaneScope.paneContentPadding]: 用于为 pane 增加自动的 content padding. 通常你需要为 pane 的内容直接添加这个 modifier.
 *   如果你不期望为整个容器添加 padding, 可以使用 [PaneScope.listDetailLayoutParameters] [ListDetailLayoutParameters.listPaneContentPaddingValues]
 * - [PaneScope.listDetailLayoutParameters] 用于获取当前的布局参数.
 *
 * ### Window Insets
 *
 * [AniListDetailPaneScaffold] 的 Window Insets 行为在 list pane 和 detail pane 之间有所不同.
 * - [detailPane]: 需要自行使用 [PaneScope.paneContentPadding] 和 [PaneScope.paneWindowInsetsPadding] 来处理 window insets. [AniListDetailPaneScaffold] 不会自动 consume 任何 insets.
 * - [listPaneContent]: 如果有 [listPaneTopAppBar], 此页会自动 consume TopAppBar 所使用的 window insets (`AniWindowInsets.forTopAppBar().only(WindowInsetsSides.Top)`).
 * 但你仍然需要使用 [PaneScope.paneContentPadding] 和 [PaneScope.paneWindowInsetsPadding] 来处理其他 window insets.
 *
 * 总之, 你总是需要在 [listPaneContent] 和 [detailPane] 中使用 [PaneScope.paneContentPadding] 和 [PaneScope.paneWindowInsetsPadding] 来处理 window insets.
 * [listPaneContent] 内会自动帮你处理 TopAppBar 的 insets.
 *
 * 如果要在 [detailPane] 内也增加 [AniTopAppBar], 则需要自行处理 insets.
 *
 * @param listPaneTopAppBar 通常可以放 [AniTopAppBar]. 可以为 `null`, 届时不占额外空间 (也不会造成 insets 消耗).
 * @param listPaneContent 列表内容, 可以是 [Column] 或者 Grid. 需要自行实现 vertical scroll.
 * @param detailPane 详情页内容.
 * @param listPanePreferredWidth See also [androidx.compose.material3.adaptive.layout.PaneScaffoldScope.preferredWidth]
 * @param useSharedTransition 是否在[单页模式][ListDetailLayoutParameters.isSinglePane]时使用 Container Transform 等 [SharedTransitionLayout] 的动画.
 * 启用后将会调整切换 pane 时的 fade 动画逻辑来支持 Container Transform.
 * @param contentWindowInsets 内容的 [WindowInsets]. 这会影响 [PaneScope.contentWindowInsets].
 *
 * @sample me.him188.ani.app.ui.exploration.search.SearchPageLayout
 */
@Composable
fun <T> AniListDetailPaneScaffold(
    navigator: ThreePaneScaffoldNavigator<T>,
    listPaneTopAppBar: @Composable (PaneScope.() -> Unit)? = null,
    listPaneContent: @Composable (PaneScope.() -> Unit),
    detailPane: @Composable (PaneScope.() -> Unit),
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = ListDetailPaneScaffoldDefaults.windowInsets,
    useSharedTransition: Boolean = false,
    listPanePreferredWidth: Dp = Dp.Unspecified,
    layoutParameters: ListDetailLayoutParameters = ListDetailLayoutParameters.calculate(navigator.scaffoldDirective),
) {
    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }
    val layoutParametersState by rememberUpdatedState(layoutParameters)

    SharedTransitionLayout {
        ListDetailPaneScaffold(
            navigator.scaffoldDirective,
            navigator.scaffoldValue,
            listPane = {
                val threePaneScaffoldScope = this
                AnimatedPane1(Modifier.preferredWidth(listPanePreferredWidth), useSharedTransition) {
                    Column {
                        val scope =
                            remember(threePaneScaffoldScope, this@SharedTransitionLayout, this@AnimatedPane1) {
                                object : PaneScope, SharedTransitionScope by this@SharedTransitionLayout {
                                    override val listDetailLayoutParameters: ListDetailLayoutParameters
                                        get() = layoutParametersState
                                    override val animatedVisibilityScope: AnimatedVisibilityScope
                                        get() = this@AnimatedPane1
                                    override val role: ThreePaneScaffoldRole
                                        get() = threePaneScaffoldScope.role

                                    override val contentWindowInsets: WindowInsets
                                        get() = when {
                                            isSinglePane -> contentWindowInsets
                                            else -> contentWindowInsets.only(WindowInsetsSides.Start + WindowInsetsSides.Vertical)
                                        }

                                    override fun Modifier.paneContentPadding(): Modifier =
                                        Modifier
                                            .padding(layoutParametersState.listPaneContentPaddingValues)
                                            .consumeWindowInsets(layoutParametersState.listPaneContentPaddingValues)
                                }
                            }
                        if (listPaneTopAppBar == null) {
                            listPaneContent(scope)
                        } else {
                            listPaneTopAppBar(scope)
                            Column(
                                Modifier.consumeWindowInsets(
                                    AniWindowInsets.forTopAppBar().only(WindowInsetsSides.Top),
                                ),
                            ) {
                                listPaneContent(scope)
                            }
                        }
                    }
                }
            },
            detailPane = {
                val threePaneScaffoldScope = this
                AnimatedPane1(useSharedTransition = useSharedTransition) {
                    Card(
                        shape = layoutParameters.detailPaneShape,
                        colors = layoutParameters.detailPaneColors,
                    ) {
                        val scope =
                            remember(threePaneScaffoldScope, this@SharedTransitionLayout, this@AnimatedPane1) {
                                object : PaneScope, SharedTransitionScope by this@SharedTransitionLayout {
                                    override val listDetailLayoutParameters: ListDetailLayoutParameters
                                        get() = layoutParametersState
                                    override val animatedVisibilityScope: AnimatedVisibilityScope
                                        get() = this@AnimatedPane1
                                    override val role: ThreePaneScaffoldRole
                                        get() = threePaneScaffoldScope.role

                                    override val contentWindowInsets: WindowInsets
                                        get() = when {
                                            isSinglePane -> contentWindowInsets
                                            else -> contentWindowInsets.only(WindowInsetsSides.End + WindowInsetsSides.Vertical)
                                        }

                                    override fun Modifier.paneContentPadding(): Modifier =
                                        Modifier
                                            .padding(layoutParametersState.detailPaneContentPaddingValues)
                                            .consumeWindowInsets(layoutParametersState.detailPaneContentPaddingValues)
                                }
                            }
                        detailPane(scope)
                    }
                }
            },
            modifier,
        )
    }
}

@Stable
interface PaneScope : SharedTransitionScope {
    /**
     * 获取当前的布局参数.
     *
     * 若要为 pane 增加 padding, 可优先使用 [paneContentPadding].
     */
    @Stable
    val listDetailLayoutParameters: ListDetailLayoutParameters

    /**
     * 用于 [Modifier.sharedElement].
     */
    @Stable
    val animatedVisibilityScope: AnimatedVisibilityScope

    /**
     * @see ListDetailPaneScaffoldRole
     */
    @Stable
    val role: ThreePaneScaffoldRole

    /**
     * @see ListDetailLayoutParameters.isSinglePane
     */
    @Stable
    val isSinglePane get() = listDetailLayoutParameters.isSinglePane

    /**
     * 此 Pane 需要 consume 的 [WindowInsets].
     *
     * - 对于单页模式, 这就是传入 [ListDetailPaneScaffold] 的 `contentWindowInsets`,
     * 通常是 [ListDetailPaneScaffoldDefaults.windowInsets].
     *
     * - 对于多页模式, 每个 pane 只 consume 一部分 insets: 左侧 pane 只 consume `Start + Vertical`, 右侧 pane 只 consume `End + Vertical`.
     *
     * 推荐使用 [paneWindowInsetsPadding].
     * 如果需要同时使用 [paneContentPadding], 顺序必须是 [paneContentPadding] 在前:
     *
     * ```kotlin
     * Column(
     *     Modifier
     *         .paneContentPadding() // 必须在最前
     *         .paneWindowInsetsPadding()
     *         .padding(top = searchBarHeight) // other paddings
     * ) {
     *     searchResultList()
     * }
     * ```
     */
    @Stable
    val contentWindowInsets: WindowInsets

    /**
     * @see contentWindowInsets
     */
    @Stable
    fun Modifier.paneWindowInsetsPadding(): Modifier = windowInsetsPadding(contentWindowInsets)

    /**
     * 为 pane 增加自动的 content padding 并 consume 等量的 [WindowInsets]. 通常应用于 pane 的最外层容器:
     */
    @Stable
    fun Modifier.paneContentPadding(): Modifier
}

@Immutable
data class ListDetailLayoutParameters(
    val listPaneContentPaddingValues: PaddingValues,
    val detailPaneContentPaddingValues: PaddingValues,
    val detailPaneShape: Shape,
    val detailPaneColors: CardColors,
    /**
     * 是否为单页模式, 即整个屏幕上只会同时出现一个 pane. 通常在一个 COMPACT 设备上.
     */
    val isSinglePane: Boolean,
) {
    companion object {
        @Composable
        fun calculate(directive: PaneScaffoldDirective): ListDetailLayoutParameters {
            val isTwoPane = directive.maxHorizontalPartitions > 1
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            return if (isTwoPane) {
                ListDetailLayoutParameters(
                    listPaneContentPaddingValues = PaddingValues(
                        start = windowSizeClass.paneHorizontalPadding,
                        end = 0.dp, // ListDetail 两个 pane 之间自带 24.dp
                    ),
                    detailPaneContentPaddingValues = PaddingValues(0.dp),
                    detailPaneShape = MaterialTheme.shapes.extraLarge.copy(
                        topEnd = ZeroCornerSize,
                        bottomEnd = ZeroCornerSize,
                    ),
                    detailPaneColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    isSinglePane = false,
                )
            } else {
                ListDetailLayoutParameters(
                    listPaneContentPaddingValues = PaddingValues(horizontal = windowSizeClass.paneHorizontalPadding),
                    detailPaneContentPaddingValues = PaddingValues(horizontal = windowSizeClass.paneHorizontalPadding),
                    detailPaneShape = RectangleShape,
                    detailPaneColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    isSinglePane = true,
                )
            }
        }
    }
}

@Suppress("UnusedReceiverParameter")
val ListDetailPaneScaffoldDefaults.windowInsets
    @Composable
    get() = AniWindowInsets.forPageContent()