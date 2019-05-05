package com.zzy.minibo.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zzy.minibo.Adapter.FriendshipAdapter;
import com.zzy.minibo.Members.User;
import com.zzy.minibo.R;

import java.util.ArrayList;
import java.util.List;

public class FriendshipFragment extends Fragment {

    private ViewPager viewPager;
    private FriendshipAdapter friendshipAdapter;
    private List<User> userListCache = new ArrayList<>();

    public FriendshipFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friendship, container, false);
        viewPager = view.findViewById(R.id.fdsp_viewpager);
        friendshipAdapter = new FriendshipAdapter(getContext(),userListCache);

        return view;
    }

}
