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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
/**
 * Create class SleepTrackerViewModel that extends AndroidViewModel
 * Class is the same as ViewModel but takes the `application` context in as a parameter and makes it available as a property - Needed to access resources, such as strings and styles
 * Pass in an instance of the SleepDatabaseDao - ViewModel needs access to the data in the database, which is through the interface defined in the DAO
 * Need a factory to instantiate the ViewModel and provide it with the datasource
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {
                // When the "Start" button is pressed -> Call a function in SleepTrackerViewModel that creates a new instance of SleepNight and stores it in the database
                // Add the code needed to implement the click handler for the "Start" button
                // Use coroutines because, among other things, clicking the buttons in the UI triggers database operations (e.g., creating or updating a SleepNight), which we do not want to slow down our UI
                // Define a variable, `tonight`, to hold the current night, and make it `MutableLiveData`
                private var tonight = MutableLiveData<SleepNight?>()

                // Define a variable, `nights`, then `getAllNights()` from the database and assign to the `nights` variable
                private val nights = database.getAllNights()

                // Initialize the `tonight` variable by creating an `init` block and calling `initializeTonight()`
                init {
                    initializeTonight()
                }

                /**
                 * Create the three corresponding variables (one for each state variable)
                 * Assign each variable a `Transformations` that tests it against the value of `tonight`
                 */
                // START button should be `visible` when `tonight` is null
                val startButtonVisible = tonight.map {
                        null == it
                }

                // STOP button should be `visible` when `tonight` is not null
                val stopButtonVisible = tonight.map {
                        null != it
                }

                // CLEAR button should be visible if `nights` contains any `nights`
                val clearButtonVisible = nights.map {
                        it.isNotEmpty()
                }

                // Set a `LiveData` that changes when you want to navigate (from `SleepTrackerFragment` to `SleepQualityFragment`)
                private val _navigateToSleepQuality = MutableLiveData<SleepNight?>()

                // Use encapsulation to expose only a gettable version to the fragment
                val navigateToSleepQuality: MutableLiveData<SleepNight?>
                        get() = _navigateToSleepQuality

                // Add a `doneNavigation()` function that resets the event
                fun doneNavigating() {
                        _navigateToSleepQuality.value = null
                }
        
                // Create the encapsulated event (for the snackbar)
                private var _showSnackbarEvent = MutableLiveData<Boolean>()
        
                val showSnackBarEvent: LiveData<Boolean>
                        get() = _showSnackbarEvent

                fun doneShowingSnackbar() {
                        _showSnackbarEvent.value = false
                }

                // Implement `initializeTonight()`
                private fun initializeTonight() {
                        // Use `viewModelScope.launch{}` to start a coroutine in the ViewModelScope
                        viewModelScope.launch {
                                // Get the value for `tonight` from the database by calling `getTonightFromDatabase` and assigning it to `tonight.value`
                                tonight.value = getTonightFromDatabase()
                        }
                }

                // Implement `getTonightFromDatabase()` - Define the function as a private suspend function that returns a nullable `SleepNight` if there is no current started `sleepNight`
                private suspend fun getTonightFromDatabase(): SleepNight? {
                        // Let the coroutine get tonight (`night`) from the database
                        var night = database.getTonight()

                        // If the start and end times are not the same, i.e., the night has already been completed, then return null
                        if (night?.stopTimeMilli != night?.startTimeMilli) {
                                night = null
                        }

                        // Otherwise, return `night`
                        return night
                }

                // Implement onStartTracking(), the click handler for the Start button
                fun onStartTracking() {
                        // Launch a coroutine in viewModelScope
                        viewModelScope.launch {
                                // Create a new SleepNight, which captures the current time as the start time
                                val newNight = SleepNight()

                                // Insert the `newNight` into the database
                                insert(newNight)

                                // Set the value of `tonight` to the new night
                                tonight.value = getTonightFromDatabase()
                        }
                }

                // Define `insert()` as a private suspend function that takes a `SleepNight` as its argument
                private suspend fun insert(night: SleepNight) {
                        // Insert the `night` into the database
                        database.insert(night)
                }

                // Implement onStopTracking(), the click handler for the Stop button
                fun onStopTracking() {
                        // Launch a coroutine in viewModelScope
                        viewModelScope.launch {
                                // Set the value of `oldNight` to the value of `tonight`. If the value of `tonight` is null, then return to launch (???)
                                val oldNight = tonight.value ?: return@launch

                                // Set the value of the `oldNight` stop time to the current system time in milliseconds
                                oldNight.stopTimeMilli = System.currentTimeMillis()

                                // Update `oldNight` in the database
                                update(oldNight)

                                // Trigger the navigation from SleepTrackerFragment to SleepQualityFragment
                                _navigateToSleepQuality.value = oldNight
                        }
                }

                // Define `update()` as a private suspend function that takes a `SleepNight` as its argument
                private suspend fun update(night: SleepNight) {
                        database.update(night)
                }

                // Implement onClear(), the click handler for the Clear button
                fun onClear() {
                        viewModelScope.launch {
                                clear()
                                tonight.value = null

                                _showSnackbarEvent.value = true
                        }
                }

                // Define `clear()` as a private suspend function
                private suspend fun clear() {
                        database.clear()
                }

                // Transform `nights` into `nightsString` using the `formatNights()` function from `Util.kt`
                val nightsString = nights.map { nights ->
                        formatNights(nights, application.resources)
                }
}

