package com.example.in2000_team1.datalayer

class ApplianceCalc {
    fun calculateApplianceCost(appliance: Appliance, pricePerKwh: Double): Double {
        val kilowattHours = appliance.usageWatts * (appliance.durationMinutes / 60.0) / 1000.0
        return kilowattHours * pricePerKwh
    }
}