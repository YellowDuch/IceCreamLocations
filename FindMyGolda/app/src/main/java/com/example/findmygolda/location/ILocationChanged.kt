package com.example.findmygolda.location

import android.location.Location

interface ILocationChanged {
    fun locationChanged(location : Location)
}