package com.android.locator.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.locator.databinding.FragmentListUserBinding
import kotlinx.coroutines.launch

class UserListFragment():Fragment() {
    private var _binding: FragmentListUserBinding?=null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val userListViewModel: UserListViewModel by viewModels {
        UserListViewModelFactory(args.choice)
    }
    private val args: UserListFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListUserBinding.inflate(inflater, container, false)
        binding.listUserRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        binding.apply {
            listUserTitle.text = when (args.choice) {
                0 -> "Witnesses"
                1 -> "My Likes"
                2 -> "My Notifications"
                3 -> "Campus List"
                else -> ""
            }
            listUserBackBtn.setOnClickListener {
                navController.navigate(
                    UserListFragmentDirections.actionUserList2UserSetting()
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (args.choice) {
                    0 -> userListViewModel.witness.collect {witnesses ->
                        binding.listUserRecyclerView.adapter = WitnessListAdapter(witnesses)
                    }
                    1 -> userListViewModel.likes.collect {liked_cats ->
                        binding.listUserRecyclerView.adapter = CatListAdapter(liked_cats) {
                            // emtpy onclick event
                        }
                    }
                    2 -> userListViewModel.notifications.collect { not_witnesses ->
                        binding.listUserRecyclerView.adapter = WitnessListAdapter(not_witnesses)
                    }
                    3 -> userListViewModel.campuses.collect { campuses ->
                        binding.listUserRecyclerView.adapter = CampusListAdapter(campuses) {
                            // TODO: set campus in repo
                        }
                    }
                }
            }
        }
    }
}