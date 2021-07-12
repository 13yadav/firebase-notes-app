package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.strangecoder.notesapp.MainActivity
import com.strangecoder.notesapp.databinding.FragmentRegisterBinding
import com.strangecoder.notesapp.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        binding.registerButton.setOnClickListener {
            createUser()
        }
    }

    private fun createUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    viewModel.firebaseAuth
                        .createUserWithEmailAndPassword(email, password)
                        .await()
                    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                    findNavController()
                        .navigate(RegisterFragmentDirections.actionRegisterFragmentToNotesListFragment())
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("rrLOG", e.toString())
                        Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}