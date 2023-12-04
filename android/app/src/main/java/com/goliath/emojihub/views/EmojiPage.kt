package com.goliath.emojihub.views

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.ui.theme.Color.Black
import com.goliath.emojihub.ui.theme.Color.LightGray
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.views.components.EmojiCell
import com.goliath.emojihub.views.components.EmojiCellDisplay
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun EmojiPage() {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val viewModel = hiltViewModel<EmojiViewModel>()
    val emojiList = viewModel.emojiList.collectAsLazyPagingItems()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            viewModel.videoUri = uri
            navController.navigate(NavigationDestination.TransformVideo)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchEmojiList()
    }

    Column(Modifier.background(White)) {
        TopNavigationBar("Emoji", shouldNavigate = false) {
            IconButton(onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.READ_MEDIA_VIDEO
                    ) -> {
                        pickMediaLauncher.launch(PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.VideoOnly
                        ))
                    }
                    else -> {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                    }
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = ""
                )
            }
        }

        Column(Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Trending 🔥", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Button(
                    onClick = { viewModel.toggleSortingMode() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (viewModel.sortByDate == 0) Black else LightGray,
                        contentColor = White
                    )
                ) {
                    Text(text = if (viewModel.sortByDate == 1) "Sort by Date" else "Sort by Save Count", fontSize = 12.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(emojiList.itemCount) { index ->
                    emojiList[index]?.let{
                        EmojiCell(emoji = it, displayMode = EmojiCellDisplay.VERTICAL) { selectedEmoji ->
                            viewModel.currentEmoji = selectedEmoji
                            navController.navigate(NavigationDestination.PlayEmojiVideo)
                        }
                    }
                }
            }
        }
    }
}