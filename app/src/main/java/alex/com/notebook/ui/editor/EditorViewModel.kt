package alex.com.notebook.ui.editor

import alex.com.notebook.data.Note
import alex.com.notebook.data.NoteRepository
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ADD_NOTE_RESULT_OK = 1
const val EDIT_NOTE_RESULT_OK = 2

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val state: SavedStateHandle
) :
    ViewModel() {

    fun onSavedClick() {
        if (noteTitle.isBlank()) {
            showInvalidInputMessage("Title cannot be empty")
            return
        }
        if (noteDescription.isBlank()) {
            showInvalidInputMessage("Description cannot be empty")
            return
        }

        if (note != null) {
            val updatedNote =
                note.copy(title = noteTitle, color = noteColor, description = noteDescription)
            updateNote(updatedNote)
        } else {
            val newNote = Note(title = noteTitle, color = noteColor, description = noteDescription)
            createNote(newNote)
        }
    }

    private fun showInvalidInputMessage(errorMessage: String) = viewModelScope.launch {
        editorEventChannel.send(EditorEvent.ShowInvalidInputMessage(errorMessage))
    }

    private fun createNote(newNote: Note) = viewModelScope.launch {
        repository.insert(newNote)
        editorEventChannel.send(EditorEvent.NavigateBackWithResult(ADD_NOTE_RESULT_OK))
    }

    private fun updateNote(updatedNote: Note) = viewModelScope.launch {
        repository.update(updatedNote)
        editorEventChannel.send(EditorEvent.NavigateBackWithResult(EDIT_NOTE_RESULT_OK))
    }

    private val editorEventChannel = Channel<EditorEvent>()
    val editorEvent = editorEventChannel.receiveAsFlow()

    sealed class EditorEvent {
        data class ShowInvalidInputMessage(val message: String) : EditorEvent()
        data class NavigateBackWithResult(val result: Int) : EditorEvent()
    }

    val note = state.get<Note>("note")

    var noteTitle = state.get<String>("noteTitle") ?: note?.title ?: ""
        set(value) {
            field = value
            state.set("noteTitle", value)
        }

    var noteColor = state.get<Int>("noteColor") ?: note?.color ?: -10564169
        set(value) {
            field = value
            state.set("noteColor", value)
        }

    var noteDescription = state.get<String>("noteDescription") ?: note?.description ?: ""
        set(value) {
            field = value
            state.set("noteDescription", value)
        }
}