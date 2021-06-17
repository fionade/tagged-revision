package de.lmu.nfcrevision.datahandling

import androidx.room.*

@Entity(tableName = "question")
data class Question(val question: String,
                    val answer: String,
                    val location: String,
                    var lastSeen: Long = 0,
                    @PrimaryKey(autoGenerate = true)
                    var id: Long = 0) {

}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM question WHERE location = (:location) ORDER BY lastSeen ASC")
    suspend fun getQuestions(location: String): List<Question>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quiz: Question): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quizList: List<Question>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(quiz: Question)
}