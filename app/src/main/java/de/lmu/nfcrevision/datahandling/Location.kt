package de.lmu.nfcrevision.datahandling

import androidx.room.*

@Entity
data class Location(@PrimaryKey val locationName: String)

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(location: Location)

    @Query("SELECT locationName from LOCATION")
    suspend fun getAllLocations(): List<String>

}