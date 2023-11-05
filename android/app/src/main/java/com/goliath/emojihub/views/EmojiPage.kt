package com.goliath.emojihub.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goliath.emojihub.models.Emoji
import com.goliath.emojihub.views.components.EmojiCell
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.LocalNavController
import com.goliath.emojihub.NavigationDestination
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.ui.theme.Color.White
import com.goliath.emojihub.viewmodels.EmojiViewModel
import com.goliath.emojihub.views.components.TopNavigationBar

@Composable
fun EmojiPage(
    emojiList: List<Emoji>
) {
    val context = LocalContext.current
    val navController = LocalNavController.current

    val viewModel = hiltViewModel<EmojiViewModel>()

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                        }
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

            Text("Trending 🔥", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(emojiList.size) {index ->
                    EmojiCell(emoji = emojiList[index])
                }
            }
        }
    }
}

@Preview
@Composable
fun EmojiPagePreview() {
    EmojiPage(emojiList = (1..10).map { createDummyEmoji() })
}