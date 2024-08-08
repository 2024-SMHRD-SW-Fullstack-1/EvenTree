package com.example.andhack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay

class SharedViewModel : ViewModel() {
    private val _selectedDate = MutableLiveData<CalendarDay>()
    val selectedDate: LiveData<CalendarDay> get() = _selectedDate

    fun setSelectedDate(date: CalendarDay) {
        _selectedDate.value = date
    }
}