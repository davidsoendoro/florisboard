/*
 * Copyright (C) 2021 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kokatto.kobold.uicomponent

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.kokatto.kobold.api.model.request.PageName
import com.kokatto.kobold.api.model.request.PageVisitTrackerRequest
import com.kokatto.kobold.tracker.viewmodel.TrackerViewModel
import dev.patrickgold.florisboard.ime.text.keyboard.TextKeyboardView

@Suppress("UNUSED_PARAMETER")
class KoboldKeyboardView : TextKeyboardView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    val trackerViewModel = TrackerViewModel()

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView is KoboldKeyboardView && visibility == View.VISIBLE && changedView == this) {
            trackerViewModel.postPageVisitTracker(PageVisitTrackerRequest(PageName.KEYBOARD_OPEN))
        }
    }
}
