package com.goliath.emojihub.views.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goliath.emojihub.ui.theme.Color

@Composable
fun CircularIndeterminateProgressBar(
    isDisplayed: Boolean
){

    if(isDisplayed){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            CircularProgressIndicator(
                color = Color.LightGray,
                strokeWidth = 5.dp
            )
            Text(
                text = "비디오 변환 중 ...",
                fontSize = 12.sp,
                color = Color.White,
            )
        }
    }

}


