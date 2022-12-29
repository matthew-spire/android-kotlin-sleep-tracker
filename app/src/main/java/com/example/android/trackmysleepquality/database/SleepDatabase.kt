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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Create an abstract class that extends RoomDatabase
// Only one table and one DAO, but there could be many tables and many DAO's.
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase: RoomDatabase() {
    // Declare an abstract value of type SleepNightDao.
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // Declare a companion object.
    // Companion object allows clients to access the methods for creating or getting the database w/o instantiating the class.
    // Only purpose of the class is to provide the database, there is no reason to instantiate the class.
    companion object {
        // Declare a @Volatile INSTANCE variable.
        // @Volatile annotation helps ensure the value of INSTANCE is always up to date and the same to all execution threads.
        // Value of a variable annotated with @Volatile will never be cached and all writes and reads will be done to and from the main memory. Changes made by one thread to INSTANCE are visible to all other threads immediately and there is no possibility of a mismatch.
        // INSTANCE keeps a reference to the database once there is a database, which helps avoid repeatedly opening connections to the database (expensive).
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        // Define a getInstance() method with a synchronized block.
        // Use a databaseBuilder, which requires a Context.
        fun getInstance(context: Context) : SleepDatabase {
            // Multiple threads can potentially ask for a database instance at the same time, leaving us with two databases instead of one.
            // Using synchronized means that only one thread of execution at a time can enter this block of code, ensuring the database only gets initialized once.
            // Pass `this` into synchronized so that there is access to the Context
            synchronized(this) {
                // Take advantage of Kotlin's smart casts to make sure we always return a SleepDatabase. Smart casts is only available to local variables, not class variables.
                var instance = INSTANCE

                // Check if there is already a database
                if (instance == null) {
                    // Create the database using Room.databaseBuilder
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java, // What database to build using a reference to the database class
                        "sleep_history_database" // Name of the database
                    )
                        .fallbackToDestructiveMigration()
                        .build() // Wipe and rebuild the database.
                                // Normally have to provide a migration object w/ a migration strategy when the database is created.
                                // Migration means that if the database schema changes, the migration provides a way to convert the existing tables and data into the new schema.
                                // Migration object defines how you take all rows with old schema and convert them to rows in the new schema.
                                // Common when upgrading from one version of the app w/ its database schema to a new version of the app w/ a newer database schema.
                    INSTANCE = instance // Assign instance to the newly created database.
                }
                return instance
            }
        }
    }
}

// TODO (06) Inside the synchronized block:
//...Check whether the database already exist,
//......and if it does not, use Room.databaseBuilder to create it.