package com.goliath.emojihub.data_sources

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BottomSheetController {
    val bottomSheetState: StateFlow<Boolean>
    fun setBottomSheetState(isShown: Boolean)

    fun dismiss()
}
@Singleton
class BottomSheetControllerImpl @Inject constructor(

    ): BottomSheetController {
        private val _bottomSheetState = MutableStateFlow(false)
        override val bottomSheetState: StateFlow<Boolean>
            get() = _bottomSheetState

        override fun setBottomSheetState(isShown: Boolean) {
            _bottomSheetState.update {
                isShown
            }
        }

        override fun dismiss() {
            _bottomSheetState.update { false }
        }
    }