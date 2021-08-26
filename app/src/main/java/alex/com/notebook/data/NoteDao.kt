package alex.com.notebook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT *FROM notebook")
    fun getAllNotes(): Flow<List<Note>>

    @Query("DELETE FROM notebook")
    suspend fun deleteAllNotes()

    @Query("SELECT * FROM notebook WHERE description LIKE '%' || :searchQuery || '%' OR title LIKE '%' || :searchQuery || '%' ORDER BY title ASC")
    //@Query("SELECT * FROM notebook WHERE description LIKE '%' || :searchQuery || '%' ORDER BY description ASC")
    fun getNotesSortedByName(searchQuery: String): Flow<List<Note>>

//    fun getNotes(searchQuery: String): Flow<List<Note>> = when (searchQuery) {
//        "" -> getAllNotes()
//        else -> getNotesSortedByName(searchQuery)
//    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete()
    suspend fun delete(note: Note)

    @Update()
    suspend fun update(note: Note)

}