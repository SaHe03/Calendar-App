package com.example.calendarapp

import androidx.annotation.ColorRes
import androidx.annotation.StringRes

val spinnerPeriodFlows = listOf(
    PeriodFlowStatus.SPOTTY,
    PeriodFlowStatus.NORMAL,
    PeriodFlowStatus.HEAVY,
    PeriodFlowStatus.FLOODING,
    PeriodFlowStatus.PREGNANT
)
@StringRes
fun PeriodFlowStatus.labelRes(): Int = when (this) {
    PeriodFlowStatus.SPOTTY -> R.string.period_Spotty
    PeriodFlowStatus.NORMAL -> R.string.period_Normal
    PeriodFlowStatus.HEAVY -> R.string.period_Heavy
    PeriodFlowStatus.FLOODING -> R.string.period_Flooding
    PeriodFlowStatus.EXPECTING -> R.string.period_Expecting
    PeriodFlowStatus.PREGNANT -> R.string.period_Pregnant
}
@ColorRes
fun PeriodFlowStatus.backgroundRes(): Int = when (this) {
    PeriodFlowStatus.SPOTTY -> R.color.period_Spotty_Color
    PeriodFlowStatus.NORMAL -> R.color.period_Normal_Color
    PeriodFlowStatus.HEAVY -> R.color.period_Heavy_Color
    PeriodFlowStatus.FLOODING -> R.color.period_Flooding_Color
    PeriodFlowStatus.EXPECTING -> R.color.period_Expecting_Color
    PeriodFlowStatus.PREGNANT -> R.color.period_Pregnant_Color
}
val selectablePeriodFlows: List<PeriodFlowStatus>
    get() = PeriodFlowStatus.entries.filter {
        it != PeriodFlowStatus.EXPECTING
    }