package com.example.in2000_team1.datalayer

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FrostApiService {

    @GET("observations/v0.jsonld")
    suspend fun getObservations(
        @Query("sources") sources: String,
        @Query("elements") elements: String,
        @Query("referencetime") referenceTime: String,
        @Header("X-Gravitee-API-Key") apiKey: String
    ): Response<ObservationResponse>
}

interface PriceApiService {
    @GET("api/v1/prices/{year}/{month}-{day}_{priceArea}.json")
    suspend fun getPrices(
        @Path("year") year: Int,
        @Path("month") month: String,
        @Path("day") day: String,
        @Path("priceArea") priceArea: String
    ): List<Price>
}

interface ForecastApiService {
    @GET("locationforecast/2.0/compact")
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("X-Gravitee-API-Key") apiKey: String
    ): Response<Forecast>
}

interface NowCastApiService{
    @GET("nowcast/2.0/complete")
    suspend fun getNowCast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Header("X-Gravitee-API-Key") apiKey: String
    ): Response<Forecast>

}