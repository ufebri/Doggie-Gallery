package com.raylabs.doggie.ui.liked;

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
import com.raylabs.doggie.databinding.FragmentLikedBinding;
import com.raylabs.doggie.ui.ImagesAdapter;
// import com.raylabs.doggie.ui.detail.DetailActivity; // Dihapus
import com.raylabs.doggie.ui.detail.DetailBottomSheetFragment; // Ditambahkan
import com.raylabs.doggie.viewmodel.ViewModelFactory;

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
        // Inflate the layout for this fragment
        fragmentLikedBinding = FragmentLikedBinding.inflate(inflater, container, false); // Ditambahkan container dan false
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
                            // Karena data liked bisa berubah (tambah/hapus), selalu perbarui list dari awal
                            imagesGrid.clear(); 
                            if (result.status == com.raylabs.doggie.vo.Status.LOADING) {
                                fragmentLikedBinding.pbLiked.setVisibility(View.VISIBLE);
                                fragmentLikedBinding.rvLiked.setVisibility(View.GONE);
                            } else if (result.status == com.raylabs.doggie.vo.Status.SUCCESS) {
                                if (result.data != null && !result.data.isEmpty()) {
                                    imagesGrid.addAll(result.data);
                                    fragmentLikedBinding.rvLiked.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                    fragmentLikedBinding.rvLiked.setHasFixedSize(true);
                                    adapter = new ImagesAdapter(imagesGrid, item -> {
                                        if (item.getLink() != null && !item.getLink().isEmpty()) {
                                            DetailBottomSheetFragment.newInstance(item.getLink()).show(getParentFragmentManager(), "DetailBottomSheetFragmentTag");
                                        }
                                    });
                                    fragmentLikedBinding.rvLiked.setAdapter(adapter);
                                    fragmentLikedBinding.rvLiked.setVisibility(View.VISIBLE);
                                } else {
                                    // Tampilkan pesan jika tidak ada gambar yang disukai
                                    fragmentLikedBinding.rvLiked.setVisibility(View.GONE);
                                    // Anda bisa menambahkan TextView untuk pesan "No liked images yet."
                                    Toast.makeText(getContext(), "No liked images yet.", Toast.LENGTH_SHORT).show(); 
                                }
                                fragmentLikedBinding.pbLiked.setVisibility(View.GONE);
                            } else if (result.status == com.raylabs.doggie.vo.Status.ERROR) {
                                fragmentLikedBinding.pbLiked.setVisibility(View.GONE);
                                fragmentLikedBinding.rvLiked.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Failed to load liked images.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLikedBinding = null; // Membersihkan view binding
    }
}
