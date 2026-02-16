package com.taximeter.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taximeter.app.calculator.TaxiFareCalculator
import com.taximeter.app.location.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class TaxiMeterViewModel(application: Application) : AndroidViewModel(application) {
    private val calculator = TaxiFareCalculator()
    private val locationTracker = LocationTracker(application)

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _distanceTraveled = MutableStateFlow(0.0)
    val distanceTraveled: StateFlow<Double> = _distanceTraveled

    private val _fareBreakdown = MutableStateFlow<TaxiFareCalculator.FareBreakdown?>(null)
    val fareBreakdown: StateFlow<TaxiFareCalculator.FareBreakdown?> = _fareBreakdown

    private val _rateInfo = MutableStateFlow("")
    val rateInfo: StateFlow<String> = _rateInfo

    init {
        updateRateInfo()
        observeLocation()
    }

    fun toggleMeter() {
        _isRunning.value = !_isRunning.value
        if (_isRunning.value) {
            locationTracker.startTracking()
            startTimer()
        } else {
            locationTracker.stopTracking()
        }
    }

    fun resetMeter() {
        _isRunning.value = false
        _elapsedTime.value = 0L
        locationTracker.stopTracking()
        locationTracker.resetDistance()
        _distanceTraveled.value = 0.0
        updateFareBreakdown()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedTime.value++
                updateFareBreakdown()
            }
        }
    }

    private fun observeLocation() {
        viewModelScope.launch {
            locationTracker.distanceTraveled.collect { distance ->
                _distanceTraveled.value = distance
                updateFareBreakdown()
            }
        }
    }

    private fun updateFareBreakdown() {
        val minutes = _elapsedTime.value / 60.0
        _fareBreakdown.value = calculator.calculateFare(_distanceTraveled.value, minutes)
        updateRateInfo()
    }

    private fun updateRateInfo() {
        _rateInfo.value = calculator.getCurrentRateInfo()
    }
}