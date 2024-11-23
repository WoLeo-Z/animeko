/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.subject.details.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import me.him188.ani.app.data.models.subject.PersonInfo
import me.him188.ani.app.data.models.subject.RelatedCharacterInfo
import me.him188.ani.app.data.models.subject.RelatedPersonInfo
import me.him188.ani.app.data.models.subject.RelatedSubjectInfo
import me.him188.ani.app.data.models.subject.SubjectInfo
import me.him188.ani.app.data.models.subject.nameCn
import me.him188.ani.app.navigation.LocalNavigator
import me.him188.ani.app.ui.foundation.Tag
import me.him188.ani.app.ui.foundation.avatar.AvatarImage
import me.him188.ani.app.ui.foundation.layout.desktopTitleBar
import me.him188.ani.app.ui.foundation.layout.desktopTitleBarPadding
import me.him188.ani.app.ui.foundation.text.ProvideTextStyleContentColor


object SubjectDetailsDefaults {
    @Composable
    fun Title(text: String) {
        Text(
            text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}


@Suppress("UnusedReceiverParameter")
@Composable
fun SubjectDetailsDefaults.DetailsTab(
    info: SubjectInfo,
    staff: LazyPagingItems<RelatedPersonInfo>,
    exposedStaff: LazyPagingItems<RelatedPersonInfo>,
    totalStaffCount: Int?,
    characters: LazyPagingItems<RelatedCharacterInfo>,
    exposedCharacters: LazyPagingItems<RelatedCharacterInfo>,
    totalCharactersCount: Int?,
    relatedSubjects: LazyPagingItems<RelatedSubjectInfo>,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    horizontalPadding: Dp = 16.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier,
        state = state,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp), // 这个页面内容比较密集, 如果用 16 显得有点拥挤
    ) {
        item("spacer header") { }

        // 简介
        item("description") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectionContainer {
                    var expanded by rememberSaveable { mutableStateOf(false) }
                    Text(
                        info.summary,
                        Modifier.fillMaxWidth().padding(horizontal = horizontalPadding)
                            .clickable { expanded = !expanded },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 5, // TODO: add animation 
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                TagsList(info, Modifier.padding(horizontal = horizontalPadding))
            }
        }

        item("characters title") {
            Text(
                "角色",
                Modifier.padding(horizontal = horizontalPadding),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        item("characters") {
            PersonCardList(
                allValues = characters,
                exposedCharacters,
                sheetTitle = {
                    Text(
                        if (totalCharactersCount == null) "角色" else "角色 $totalCharactersCount",
                    )
                },
                modifier = Modifier.padding(horizontal = horizontalPadding),
                itemContent = { PersonCard(it) },
            )
        }

        item("staff title") {
            Text(
                "制作人员",
                Modifier.padding(horizontal = horizontalPadding),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        item("staff") {
            PersonCardList(
                allValues = staff,
                exposedStaff,
                sheetTitle = {
                    Text(
                        if (totalStaffCount == null) "制作人员" else "制作人员 $totalStaffCount",
                    )
                },
                modifier = Modifier.padding(horizontal = horizontalPadding),
                itemContent = { PersonCard(it) },
            )
        }

        if (relatedSubjects.itemCount != 0) {
            item("related subjects title") {
                Text(
                    "关联条目",
                    Modifier.padding(horizontal = horizontalPadding),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            item("related subjects") {
                val navigator = LocalNavigator.current
                RelatedSubjectsRow(
                    relatedSubjects,
                    onClick = { navigator.navigateSubjectDetails(it.subjectId) },
                    Modifier.padding(horizontal = horizontalPadding),
                    horizontalSpacing = horizontalPadding,
                    verticalSpacing = horizontalPadding,
                )
            }
        }

        item("spacer footer") {
        }
    }
}

private const val ALWAYS_SHOW_TAGS_COUNT = 8

@Composable
private fun TagsList(
    info: SubjectInfo,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val allTags by remember(info) {
            derivedStateOf { info.tags }
        }
        var isExpanded by rememberSaveable { mutableStateOf(false) }
        val hasMoreTags by remember { derivedStateOf { allTags.size > ALWAYS_SHOW_TAGS_COUNT } }
        FlowRow(
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val presentTags by remember {
                derivedStateOf {
                    when {
                        isExpanded -> allTags
                        allTags.size <= 6 -> allTags
                        else -> {
                            val filteredByCount = allTags.filter { it.count > 100 }
                            if (filteredByCount.size < ALWAYS_SHOW_TAGS_COUNT) {
                                allTags.take(ALWAYS_SHOW_TAGS_COUNT)
                            } else {
                                filteredByCount
                            }
                        }
                    }
                }
            }
            presentTags.forEach { tag ->
                Tag(
                    Modifier
                        .clickable {}
                        .height(40.dp)
                        .padding(vertical = 4.dp),
                ) {
                    ProvideTextStyleContentColor(
                        MaterialTheme.typography.labelMedium,
                    ) {
                        Text(
                            tag.name,
                            Modifier.align(Alignment.CenterVertically),
                            maxLines = 1,
                        )

                        Text(
                            tag.count.toString(),
                            Modifier.padding(start = 6.dp).align(Alignment.CenterVertically),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
            if (hasMoreTags) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    if (isExpanded) {
                        TextButton(
                            { isExpanded = !isExpanded },
                            Modifier.height(40.dp),
                        ) {
                            Text("显示更少")
                        }
                    } else {
                        TextButton(
                            { isExpanded = !isExpanded },
                            Modifier.height(40.dp),
                        ) {
                            Text("显示更多")
                        }
                    }
                }
            }
        }
//        if (hasMoreTags) {
//            
//        }
    }
}

@Composable
private fun <T : Any> PersonCardList(
    allValues: LazyPagingItems<T>,
    exposedValues: LazyPagingItems<T>,
    sheetTitle: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    maxItemsInEachRow: Int = 2,
    itemSpacing: Dp = 12.dp,
    itemContent: @Composable (T) -> Unit,
) {
    Column(modifier) {
        var showSheet by rememberSaveable { mutableStateOf(false) }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            maxItemsInEachRow = maxItemsInEachRow,
        ) {
            for (i in 0 until exposedValues.itemCount) {
                val item = exposedValues[i] ?: continue
                Box(Modifier.weight(1f)) {
                    itemContent(item)
                }
            }
        }
        TextButton(
            { showSheet = true },
            Modifier.padding(top = 8.dp).align(Alignment.End),
        ) {
            Text("查看全部")
        }

        if (showSheet) {
            ModalBottomSheet(
                { showSheet = false },
                modifier = Modifier.desktopTitleBarPadding().statusBarsPadding(),
                contentWindowInsets = { BottomSheetDefaults.windowInsets.add(WindowInsets.desktopTitleBar()) },
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProvideTextStyle(MaterialTheme.typography.titleLarge) {
                        Row { sheetTitle() }
                    }

                    LazyVerticalGrid(
                        GridCells.Fixed(maxItemsInEachRow),
                        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                        verticalArrangement = Arrangement.spacedBy(itemSpacing),
                    ) {
                        items(
                            allValues.itemCount,
                            allValues.itemKey(),
                            contentType = allValues.itemContentType(),
                        ) { index ->
                            allValues[index]?.let {
                                itemContent(it)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonCard(info: RelatedPersonInfo, modifier: Modifier = Modifier) {
    PersonCard(
        avatarUrl = info.personInfo.imageMedium,
        name = info.personInfo.displayName,
        relation = info.position.nameCn ?: "",
        modifier = modifier,
    )
}

@Composable
fun PersonCard(info: RelatedCharacterInfo, modifier: Modifier = Modifier) {
    PersonCard(
        avatarUrl = info.character.imageMedium,
        name = info.character.displayName,
        relation = info.role.nameCn,
        modifier = modifier,
        actorName = remember(info) { getFirstName(info.character.actors) },
    )
}

private fun getFirstName(actors: List<PersonInfo>): String {
    if (actors.isEmpty()) return ""
    val actor = actors.first()
    return actor.displayName
}

@Composable
fun PersonCard(
    avatarUrl: String?,
    name: String,
    relation: String,
    modifier: Modifier = Modifier,
    actorName: String? = null,
) {
    Row(modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.clip(MaterialTheme.shapes.small).size(48.dp)) {
                AvatarImage(avatarUrl, Modifier.matchParentSize(), alignment = Alignment.TopCenter)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                    Text(name, Modifier.basicMarquee(), softWrap = false, fontWeight = FontWeight.Bold)
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                        Text(relation, softWrap = false, maxLines = 1)

                        if (actorName != null) {
                            Text(" · ", softWrap = false, maxLines = 1)
                            Text(actorName, Modifier.basicMarquee(), softWrap = false, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}