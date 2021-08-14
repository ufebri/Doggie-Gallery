package com.bedboy.ufebri.doggie.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bedboy.ufebri.doggie.ImagesAdapter;
import com.bedboy.ufebri.doggie.R;
import com.bedboy.ufebri.doggie.data.BaseResponse;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.databinding.FragmentHomeBinding;
import com.bedboy.ufebri.doggie.network.ApiConfig;
import com.bedboy.ufebri.doggie.viewmodel.ViewModelFactory;
import com.bedboy.ufebri.doggie.vo.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                                    //Setup Recyclerview
                                    imagesGrid.addAll(result.data);
                                    fragmentHomeBinding.recAnimal.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                                    fragmentHomeBinding.recAnimal.setHasFixedSize(true);
                                    imagesAdapter = new ImagesAdapter(imagesGrid);
                                    fragmentHomeBinding.recAnimal.setAdapter(imagesAdapter);

                                    fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                                    fragmentHomeBinding.recAnimal.setVisibility(View.VISIBLE);
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
}
