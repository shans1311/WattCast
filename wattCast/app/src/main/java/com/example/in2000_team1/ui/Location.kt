package com.example.in2000_team1.ui


// Enum class for the five locations in Norway, with coordinates and the needed area codes for the APIs
// These five locations are chosen because they are the ones to choose from in the hvakosterstrommen.no API
// https://frost.met.no/sources/v0.jsonld has been examined to get the exact coordinates for the right locations

@Suppress("NonAsciiCharacters") // We can suppress the warning about the character "ø", as it does not cause problems, and is used in the UI.
enum class Location(val latitude: Double, val longitude: Double, val frostApiCode: String, val priceCode: String) {
    Oslo(59.9139, 10.7522, "SN18700", "NO1"),
    Kristiansand(58.2, 8.0767, "SN39040", "NO2"),
    Trondheim(63.4107, 10.4538, "SN68860", "NO3"),
    Tromsø(69.6767, 18.9133, "SN90490", "NO4"),
    Bergen(60.383, 5.3327, "SN50540", "NO5");
}
