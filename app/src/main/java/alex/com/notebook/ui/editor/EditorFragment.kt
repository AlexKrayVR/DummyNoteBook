package alex.com.notebook.ui.editor

import alex.com.notebook.R
import alex.com.notebook.common.logDebug
import alex.com.notebook.databinding.FragmentEditorBinding
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import yuku.ambilwarna.AmbilWarnaDialog

@AndroidEntryPoint
class EditorFragment : Fragment(R.layout.fragment_editor) {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditorViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditorBinding.bind(view)

        binding.apply {

            title.setText(viewModel.noteTitle)
            description.setText(viewModel.noteDescription)


            colorPicker.backgroundTintList =
                ColorStateList.valueOf(viewModel.noteColor)
            colorPicker.setOnClickListener {
                openColorPicker()
            }

            title.addTextChangedListener {
                viewModel.noteTitle = it.toString()
            }
            description.addTextChangedListener {
                viewModel.noteDescription = it.toString()
            }

            saveNote.setOnClickListener {
                viewModel.onSavedClick()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.editorEvent.collect { event->
                        when (event) {
                            is EditorViewModel.EditorEvent.ShowInvalidInputMessage -> {
                                Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                                    .show()
                            }
                            is EditorViewModel.EditorEvent.NavigateBackWithResult -> {
                                setFragmentResult(
                                    "editor_result",
                                    bundleOf("editor_result" to event.result)
                                )
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openColorPicker() {
        val colorPicker = AmbilWarnaDialog(
            requireContext(),
            R.color.white,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog?) {
                }

                override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                    binding.colorPicker.backgroundTintList =
                        ColorStateList.valueOf(color)
                    logDebug("note: $color")

                    viewModel.noteColor = color
                }
            })
        colorPicker.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}