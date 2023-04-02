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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SleepDatabaseDao {
    // Add annotated insert() method for inserting a single SleepNight
    @Insert
    suspend fun insert(night: SleepNight)

    // Add annotated update() method for updating a single SleepNight
    @Update
    suspend fun update(night: SleepNight)

    // Query to get a specific night based on its key
    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :key")
    suspend fun get(key: Long): SleepNight

    // Query to clear the database, i.e. delete all rows, without deleting the table
    @Query("DELETE FROM daily_sleep_quality_table")
    suspend fun clear()

    // Query to return all rows in the table in descending order
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    /*suspend*/ fun getAllNights(): LiveData<List<SleepNight>>

    // Query to return the most recent night by looking at all nights. Nullable so that it can handle if the table is empty
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    suspend fun getTonight(): SleepNight?
}
