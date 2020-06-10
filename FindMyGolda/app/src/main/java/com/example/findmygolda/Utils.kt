package com.example.findmygolda

import android.location.Location
import com.example.findmygolda.database.Branch

fun parseMinutesToMilliseconds(minutes : Int) : Long{
    return (minutes * Constants.MINUTES_TO_MILLISECONDS).toLong()
}

fun getBranchLocation(branch: Branch): Location {
    val branchLocation = Location(Constants.LOCATION_NAME)
    branchLocation.longitude = branch.longitude
    branchLocation.latitude = branch.latitude
    return branchLocation
}
