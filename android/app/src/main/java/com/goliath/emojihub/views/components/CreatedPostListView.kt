package com.goliath.emojihub.views.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalBottomSheetController
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.models.Post
import com.goliath.emojihub.ui.theme.Color
import com.goliath.emojihub.viewmodels.EmojiViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CreatedPostListView(
    postList: StateFlow<PagingData<Post>>
) {
    val navController = LocalNavController.current
    val bottomSheetController = LocalBottomSheetController.current

    val emojiViewModel = hiltViewModel<EmojiViewModel>()


    val pagingPostList = postList.collectAsLazyPagingItems()

    Column (Modifier.background(Color.White)) {
        TopNavigationBar(
            title = "내가 작성한 포스트",
            navigate = { navController.popBackStack() }
        )

        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(pagingPostList.itemCount) { index ->
                    pagingPostList[index]?.let {
                        PostCell(post = it)
                        Divider(color = Color.EmojiHubDividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }

    if (bottomSheetController.isVisible) {
        CustomBottomSheet(
            bottomSheetContent = emojiViewModel.bottomSheetContent
        )
    }
}