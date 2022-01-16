package com.mammoth.kotlinflowdemo.domain

import com.google.gson.JsonObject
import com.mammoth.kotlinflowdemo.data.APIs
import javax.inject.Inject

class SampleUseCase @Inject constructor(
    private val apIs: APIs
) {
    suspend operator fun invoke(): JsonObject {
        //here you can add some domain logic or call another UseCase
        return apIs.sampleGet()
    }
}