package alex.com.notebook.ui.notes

import alex.com.notebook.data.Note
import alex.com.notebook.data.NoteRepository
import alex.com.notebook.ui.editor.ADD_NOTE_RESULT_OK
import alex.com.notebook.ui.editor.EDIT_NOTE_RESULT_OK
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val state: SavedStateHandle
) :
    ViewModel() {

    val allNotes:LiveData<List<Note>> =
        repository.getNotes("").asLiveData()

    val searchQuery = MutableStateFlow("")
    //val searchQuery = state.getLiveData("searchQuery", "").asFlow()


    private val noteFlowSimple = searchQuery
        .flatMapLatest {
            repository.getNotes(it)
        }

    val notes = noteFlowSimple.asLiveData()


    //Kotlin Channel - это примитив для общения и передачи потоков данных между корутинами.
    private val notesEventChannel = Channel<NotesEvent>()

    val notesEvent = notesEventChannel.receiveAsFlow()


    sealed class NotesEvent {
        object NavigateToAddNoteScreen : NotesEvent()
        data class NavigateToEditNoteScreen(val note: Note) : NotesEvent()

        //data class ShowUndoDeleteNoteMessage(val note: Note) : NotesEvent()
        data class ShowNoteSavedConfirmationMessage(val message: String) : NotesEvent()
    }

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun onNoteSelected(note: Note) = viewModelScope.launch {
        notesEventChannel.send(NotesEvent.NavigateToEditNoteScreen(note))
    }

    fun onAddNewNoteClick() = viewModelScope.launch {
        notesEventChannel.send(NotesEvent.NavigateToAddNoteScreen)
    }

    private fun showNoteSavedConfirmationMessage(message: String) = viewModelScope.launch {
        notesEventChannel.send(NotesEvent.ShowNoteSavedConfirmationMessage(message))
    }

    fun onNoteSwiped(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun onEditorResult(result: Int) {
        when (result) {
            ADD_NOTE_RESULT_OK -> showNoteSavedConfirmationMessage("Note added")
            EDIT_NOTE_RESULT_OK -> showNoteSavedConfirmationMessage("Note updated")
        }
    }
}