package com.strangecoder.notesapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.strangecoder.notesapp.MainActivity
import com.strangecoder.notesapp.R
import com.strangecoder.notesapp.databinding.FragmentSplashBinding
import com.strangecoder.notesapp.ui.MainViewModel
import kotlinx.coroutines.*

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }

    override fun onStart() {
        super.onStart()
        val currentUser = viewModel.firebaseAuth.currentUser
        if (currentUser != null) {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToNotesListFragment())
        } else {
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}