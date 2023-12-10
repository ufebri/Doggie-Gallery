package com.bedboy.ufebri.doggie.ui.categories;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.databinding.FragmentCategoriesBinding;
import com.bedboy.ufebri.doggie.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private CategoriesAdapter adapter;
    private List<DoggieEntity> mData = new ArrayList<>();
    private CategoriesViewModel viewModel;
    private FragmentCategoriesBinding binding;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
            viewModel = new ViewModelProvider(this, factory).get(CategoriesViewModel.class);
            viewModel.getData().observe(getViewLifecycleOwner(),
                    result -> {
                        if (result != null) {
                            switch (result.status) {
                                case LOADING:
                                    binding.pbCategories.setVisibility(View.VISIBLE);
                                    binding.rvCategories.setVisibility(View.GONE);
                                    break;
                                case SUCCESS:
                                    if (result.data != null) {
                                        mData.addAll(result.data);
                                        binding.rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        binding.rvCategories.setHasFixedSize(true);
                                        adapter = new CategoriesAdapter(mData);
                                        binding.rvCategories.setAdapter(adapter);

                                        binding.pbCategories.setVisibility(View.GONE);
                                        binding.rvCategories.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case ERROR:
                                    binding.pbCategories.setVisibility(View.GONE);
                                    binding.rvCategories.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed Get Image", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }
    }
}