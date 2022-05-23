package com.zvonimirplivelic.githound.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QueryFilterParameter(
    var sortBy: String = "",
    var orderBy: String = "",
    var resultsPerPage: Int = 30,
    var pageNumber: Int = 1
) : Parcelable