/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.launch

// Create a `SleepQualityViewModel` that takes `sleepNightKey` and `database` as arguments
class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {
    // Navigate back to the `SleepTrackerFragment` by analogously implementing `navigateToSleepTracker` and `_navigateToSleepTracker`, as well as `doneNavigating()`
    private val _navigateToSleepTracker = MutableLiveData<Boolean?>()

    val navigateToSleepTracker: LiveData<Boolean?>
        get() = _navigateToSleepTracker

    fun doneNavigating() {
        _navigateToSleepTracker.value = null
    }

    // Create one click handler, `onSetSleepQuality()` that will be used for all the sleep quality images (emojis)
    fun onSetSleepQuality(quality: Int) {
        // Use a coroutine
        viewModelScope.launch {
            // Get `tonight` using the `sleepNightKey`
            val tonight = database.get(sleepNightKey) /*?: return@withContext*/

            // Set the sleep quality
            tonight.sleepQuality = quality

            // Update the database
            database.update(tonight)

            // Trigger navigation
            _navigateToSleepTracker.value = true
        }
    }
}