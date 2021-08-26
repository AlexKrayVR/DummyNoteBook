package alex.com.notebook.ui.notes

import alex.com.notebook.R
import alex.com.notebook.common.logDebug
import alex.com.notebook.data.Note
import alex.com.notebook.databinding.FragmentNotesBinding
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotesFragment : Fragment(R.layout.fragment_notes), NotesAdapter.OnItemClickListener {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesViewModel by viewModels()
    private val notesAdapter = NotesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentNotesBinding.bind(view)

        setFragmentResultListener("editor_result") { _, bundle ->
            val result = bundle.getInt("editor_result")
            viewModel.onEditorResult(result)
        }

        binding.apply {
            notesRecycler.apply {
                adapter = notesAdapter
                setHasFixedSize(true)
            }
            addNote.setOnClickListener {
                viewModel.onAddNewNoteClick()
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val note = notesAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onNoteSwiped(note)
                }
            }).attachToRecyclerView(notesRecycler)

        }

        //https://habr.com/ru/company/otus/blog/564050/
//https://stackoverflow.com/questions/67023147/is-lifecyclescope-launchwhenstarted-safe-or-not-if-it-is-not-safe-for-what-ca

        //This coroutine will run the given block when the lifecycle
        //is at least in the Started state and will suspend when
        //the view moves to the Stopped state

        // Collects from the flow when the View is at least STARTED and
        // SUSPENDS the collection when the lifecycle is STOPPED.
        // Collecting the flow cancels when the View is DESTROYED.

        //lifecycleScope.launchWhenStarted приостанавливает выполнение корутины.
        // Новые местоположения не обрабатываются, но производитель callbackFlow
        // тем не менее продолжает отправлять местоположения.
        // Использование API lifecycleScope.launch или launchIn еще более опасно,
        // поскольку представление продолжает использовать местоположения,
        // даже если оно находится в фоновом режиме!
        // Что потенциально может привести к отказу вашего приложения.

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            //viewLifecycleOwner.lifecycleScope.launch {
            //repeatOnLifecycle — это функция приостановки, принимающая Lifecycle.State в качестве параметра, который используется для автоматического создания и запуска новой корутины с переданным ей блоком, когда жизненный цикл достигает этого state, и отмены текущей корутины, выполняющей этот блок, когда жизненный цикл падает ниже state.
            //
            //Это позволяет обойтись без использования шаблонного кода, поскольку код, для отмены корутины, когда она больше не нужна, автоматически выполняется функцией repeatOnLifecycle
            //viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.notesEvent.collect { event ->
                when (event) {
                    is NotesViewModel.NotesEvent.NavigateToAddNoteScreen -> {
                        val action =
                            NotesFragmentDirections.actionNotesFragmentToEditorFragment4(
                                null,
                                "New Note"
                            )
                        findNavController().navigate(action)
                    }
                    is NotesViewModel.NotesEvent.NavigateToEditNoteScreen -> {
                        val action =
                            NotesFragmentDirections.actionNotesFragmentToEditorFragment4(
                                event.note,
                                "Edit Note"
                            )
                        findNavController().navigate(action)
                    }
                    is NotesViewModel.NotesEvent.ShowNoteSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        setHasOptionsMenu(true)

        viewModel.notes.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_notes, menu)
        val searchItem = menu.findItem(R.id.actionSearch)

        val searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                logDebug("newText: $newText")
                if (newText != null) {
                    viewModel.searchQuery.value = newText
                }
                return true
            }
        })
    }


    override fun onItemClick(note: Note) {
        viewModel.onNoteSelected(note)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}