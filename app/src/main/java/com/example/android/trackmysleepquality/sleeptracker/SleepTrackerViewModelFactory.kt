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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the SleepDatabaseDao and context to the ViewModel.
 */
/**
 * Notes 1
 * Takes the same argument as the SleepTrackerViewModel, i.e., the data source (SleepDatabaseDao) and the application context
 * Factory extends ViewModelProvider.Factory
 * Override create, which takes any class type as an argument and returns a ViewModel
 * In create, check that there is a SleepTrackerViewModel class available and, if there is, return an instance of it. Otherwise, throw an exception
 */
class SleepTrackerViewModelFactory(
        private val dataSource: SleepDatabaseDao,
        private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepTrackerViewModel::class.java)) {
            return SleepTrackerViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Notes 2
 * From the DAO, the ViewModel knows how to access the database but there is no reference to the database
 * There is no need for the entire database object, just the DAO that accesses the table that is needed
 * Good practice to use the minimum objects to keep the database and ViewModel cleanly separate. However, it is necessary to make sure there is a database when the ViewModel is created
 * Instead of having the ViewModel create the dependency to the database, the ViewModelFactory provides this dependency to the ViewModel; this makes it easier to test the ViewModel
 */