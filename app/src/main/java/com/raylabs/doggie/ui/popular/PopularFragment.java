package com.raylabs.doggie.ui.popular;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.doggie.databinding.FragmentPopularBinding;
import com.raylabs.doggie.ui.ImagesAdapter;
import com.raylabs.doggie.ui.detail.DetailActivity;
import com.raylabs.doggie.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class PopularFragment extends Fragment {

    private ImagesAdapter adapter;
    private List<DoggieEntity> imagesGrid = new ArrayList<>();
    private PopularViewModel viewModel;
    private FragmentPopularBinding binding;

    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPopularBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
            viewModel = new ViewModelProvider(this, factory).get(PopularViewModel.class);
            viewModel.getImage().observe(getViewLifecycleOwner(),
                    result -> {
                        if (result != null) {
                            switch (result.status) {
                                case LOADING:
                                    binding.pbPopular.setVisibility(View.VISIBLE);
                                    binding.rvPopular.setVisibility(View.GONE);
                                    break;
                                case SUCCESS:
                                    if (result.data != null) {
                                        imagesGrid.addAll(result.data);
                                        binding.rvPopular.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                        binding.rvPopular.setHasFixedSize(true);
                                        adapter = new ImagesAdapter(imagesGrid, item -> startActivity(new Intent(getActivity(), DetailActivity.class).putExtra("link", item.getLink())));
                                        binding.rvPopular.setAdapter(adapter);

                                        binding.pbPopular.setVisibility(View.GONE);
                                        binding.rvPopular.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case ERROR:
                                    binding.pbPopular.setVisibility(View.GONE);
                                    binding.rvPopular.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed Get Image", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }
    }
}