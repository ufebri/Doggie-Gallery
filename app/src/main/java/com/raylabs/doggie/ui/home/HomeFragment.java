package com.raylabs.doggie.ui.home;

// import android.content.Intent; // Dihapus
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
import com.raylabs.doggie.databinding.FragmentHomeBinding;
import com.raylabs.doggie.ui.ImagesAdapter;
// import com.raylabs.doggie.ui.detail.DetailActivity; // Dihapus
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment; // Ditambahkan
import com.raylabs.doggie.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ImagesAdapter imagesAdapter;
    private List<DoggieEntity> imagesGrid = new ArrayList<>();
    private HomeViewModel viewModel;
    private FragmentHomeBinding fragmentHomeBinding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater);
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
            viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);
            viewModel.getImage().observe(getViewLifecycleOwner(),
                    result -> {
                        if (result != null) {
                            switch (result.status) {
                                case LOADING:
                                    fragmentHomeBinding.pbHome.setVisibility(View.VISIBLE);
                                    fragmentHomeBinding.recAnimal.setVisibility(View.GONE);
                                    break;
                                case SUCCESS:
                                    if (result.data != null) {
                                        imagesGrid.clear(); // Bersihkan list sebelum menambahkan data baru untuk menghindari duplikasi
                                        imagesGrid.addAll(result.data);
                                        fragmentHomeBinding.recAnimal.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                        fragmentHomeBinding.recAnimal.setHasFixedSize(true);
                                        // Mengubah pemanggilan DetailActivity menjadi DetailBottomSheetFragment
                                        imagesAdapter = new ImagesAdapter(imagesGrid, item -> {
                                            if (item.getLink() != null && !item.getLink().isEmpty()) {
                                                DetailBottomSheetFragment.newInstance(item.getLink()).show(getParentFragmentManager(), "DetailBottomSheetFragmentTag");
                                            }
                                        });
                                        fragmentHomeBinding.recAnimal.setAdapter(imagesAdapter);

                                        fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                                        fragmentHomeBinding.recAnimal.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                case ERROR:
                                    fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                                    fragmentHomeBinding.recAnimal.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Failed Get Image", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentHomeBinding = null; // Membersihkan view binding
    }
}
