package alex.com.notebook.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {

//    fun getNotes(searchQuery: String): Flow<List<Note>> = when (searchQuery) {
//        "" -> noteDao.getAllNotes()
//        else -> noteDao.getNotesSortedByName(searchQuery)
//    }

    fun getNotes(searchQuery: String): Flow<List<Note>> = noteDao.getNotesSortedByName(searchQuery)

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }
}