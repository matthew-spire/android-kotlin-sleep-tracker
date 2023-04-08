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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        // Need a reference to the application that this fragment is attached to, which gets passed into the ViewModelFactory provider
        // requireNotNull is a Kotlin function that throws an illegal argument exception if the value is null
        val application = requireNotNull(this.activity).application

        // Need a reference to the data source via a reference to the DAO
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // Create an instance of the SleepTrackerViewModelFactory, passing it the dataSource and application
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        // Ask the ViewModelProvider for a SleepTrackerViewModel
        val sleepTrackerViewModel = ViewModelProvider(
            this,
            viewModelFactory
        )[SleepTrackerViewModel::class.java]

        // Need to finish setting up data binding and connect the ViewModel to the UI
        // Layout needs to know about the ViewModel, then we can reference functions and data in the ViewModel from the layout to display LiveData

        // Set the variable for the ViewModel in our layout, which is accessible via the binding object to the ViewModel
        binding.sleepTrackerViewModel = sleepTrackerViewModel

        // Specify the current activity as the lifecycle owner of the binding. Necessary so that the binding can observe LiveData updates
        binding.lifecycleOwner = this

        // Create an Adapter and tell the RecyclerView to use the adapter to display list items on the screen
        val adapter = SleepNightAdapter()
        binding.sleepList.adapter = adapter

        // Tell the Adapter the data to be adapted
        sleepTrackerViewModel.nights.observe(viewLifecycleOwner) {
            it?.let {
                adapter.sleepNightData = it
            }
        }

        // Add an observer for `navigateToSleepQuality`
        sleepTrackerViewModel.navigateToSleepQuality.observe(viewLifecycleOwner) { night ->
            // Navigate and pass along the ID of the current `night`, then call `doneNavigating()`
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(
                        night.nightId
                    )
                )
                sleepTrackerViewModel.doneNavigating()
            }
        }

        // Observer for the snackbar
        sleepTrackerViewModel.showSnackBarEvent.observe(viewLifecycleOwner) {
            if (it == true) {
                Snackbar.make(
                    requireActivity().findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        }

        return binding.root
    }
}
