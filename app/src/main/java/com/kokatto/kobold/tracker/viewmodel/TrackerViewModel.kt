package com.kokatto.kobold.tracker.viewmodel

import com.kokatto.kobold.api.Network
import com.kokatto.kobold.api.model.basemodel.MerchantModel
import com.kokatto.kobold.api.model.request.PageVisitTrackerRequest
import com.kokatto.kobold.api.model.response.BaseResponse
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TrackerViewModel {
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    fun postPageVisitTracker(
        request: PageVisitTrackerRequest,
    ) {
        scope.launch {
            Network.trackerApi.postPageVisitTracker(request)
        }
    }

    fun onDestroy() {
        scope.cancel()
    }
}
