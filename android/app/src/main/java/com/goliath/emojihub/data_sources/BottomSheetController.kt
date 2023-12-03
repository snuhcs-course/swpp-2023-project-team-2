package com.goliath.emojihub.data_sources

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
interface BottomSheetController {
    val state: ModalBottomSheetState
    var content: @Composable ColumnScope.() -> Unit
    val isVisible: Boolean get() = state.isVisible
    fun setSheetContent(n: @Composable ColumnScope.() -> Unit) { content = n }
    suspend fun show() = state.show()
    suspend fun hide()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun bottomSheet(): BottomSheetController {
    return object : BottomSheetController {
        override val state: ModalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        override var content by remember {
            mutableStateOf<@Composable ColumnScope.() -> Unit>({
                Box(modifier = Modifier.size(1.dp))
            },)
        }

        override suspend fun hide() {
            state.hide()
            content = { Box(modifier = Modifier.size(1.dp)) }
        }
    }
}