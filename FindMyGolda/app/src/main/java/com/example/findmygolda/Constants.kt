package com.example.findmygolda

class Constants {
    companion object {
        const val ANITA_GEO_FILE_NAME = "AnitaGeoJson"
        const val PREFERENCE_RADIUS_FROM_BRANCH = "radiusFromBranch"
        const val PREFERENCE_TIME_BETWEEN_NOTIFICATIONS = "timeBetweenNotifications"
        const val BASE_URL = "https://wow-final.firebaseio.com/"
        const val ANITA_MARKER_IMAGE_ID = "anitaMarkerImageId"
        const val DEFAULT_MAP_ZOOM = 15.0
        const val ANITA_LAYER_ID = "anitaLayer"
        const val ANITA_SOURCE_ID = "anitaSource"
        const val INTERVAL_CHECK_LOCATION = 1000L
        const val MAX_RESPONSE_TIME = 5000L
        const val BRANCHES_TABLE_NAME = "branches"
        const val ALERTS_TABLE_NAME = "alerts"
        const val DB_NAME = "database"
        const val CHIP_TITTLE_A_TO_Z = "A-Z"
        const val CHIP_TITTLE_DISTANCE = "Distance"
        const val CHANNEL_ID = "com.example.findMyGolda.branchDetails"
        const val GROUP_ID = "com.example.findMyGolda"
        const val CHANNEL_NAME = "Golda notifications"
        const val MAP_BOX_TOKEN = "pk.eyJ1IjoibW9yc2t5MiIsImEiOiJjazhlN21tMG4xM2R3M2xtajcxM2s0NW10In0.8TX4Iy3nPrjw9KJR3WzN0w"
        const val HUNDREDS_METERS = 100
        const val MIN_TIME_BETWEEN_NOTIFICATIONS = 5
        const val FIRST_CHIP_INDEX = 0
        const val NOTIFICATION_CHANEL_DESCRIPTION = "Golda notifications"
        const val DEFAULT_DISTANCE_TO_BRANCH = 5
        const val DEFAULT_TIME_BETWEEN_ALERTS = 1
        const val MINUTES_TO_MILLISECONDS = 60000
        const val LOCATION_NAME = "branchLocation"
        const val GOLDA_API_URL = "golda.json"
        const val ANITA_API_URL = " anita.json"
        const val WORKER_WORK_NAME = "com.example.android.FindMyGolda.RefreshDataWorker"
        const val REQUEST_CODE_PENDING_INTENT_MARK_AS_READ = 1
        const val REQUEST_CODE_PENDING_INTENT_DELETE_ALERT = 2
    }
}