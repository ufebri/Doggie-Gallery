package com.bedboy.ufebri.doggie.ui.liked;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bedboy.ufebri.doggie.ImagesAdapter;
import com.bedboy.ufebri.doggie.R;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.databinding.FragmentLikedBinding;
import com.bedboy.ufebri.doggie.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class LikedFragment extends Fragment {

    private ImagesAdapter adapter;
    private List<DoggieEntity> imagesGrid = new ArrayList<>();
    private LikedViewModel viewModel;
    private FragmentLikedBinding fragmentLikedBinding;


    public LikedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentLikedBinding = FragmentLikedBinding.inflate(inflater);
        return fragmentLikedBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
            viewModel = new ViewModelProvider(this, factory).get(LikedViewModel.class);
            viewModel.getImage().observe(getViewLifecycleOwner(),
                    result -> {
                        if (result != null) {
                            switch (result.status) {
                                case LOADING:
                                    fragmentLikedBinding.pbLiked.setVisibility(View.VISIBLE);
                                    fragmentLikedBinding.rvLiked.setVisibility(View.GONE);
                                    break;
                                case SUCCESS:
                                    if (result.data != null) {
                                        imagesGrid.addAll(result.data);
                                        fragmentLikedBinding.rvLiked.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                        fragmentLikedBinding.rvLiked.setHasFixedSize(true);
                                        adapter = new ImagesAdapter(imagesGrid);
                                        fragmentLikedBinding.rvLiked.setAdapter(adapter);

                                        fragmentLikedBinding.pbLiked.setVisibility(View.GONE);
                                        fragmentLikedBinding.rvLiked.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case ERROR:
                                    fragmentLikedBinding.pbLiked.setVisibility(View.GONE);
                                    fragmentLikedBinding.rvLiked.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed Get Image", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }
    }
}