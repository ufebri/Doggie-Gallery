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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedboy.ufebri.doggie.ImagesAdapter;
import com.bedboy.ufebri.doggie.R;
import com.bedboy.ufebri.doggie.data.BaseResponse;
import com.bedboy.ufebri.doggie.databinding.FragmentHomeBinding;
import com.bedboy.ufebri.doggie.network.ApiConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {


    private ImagesAdapter imagesAdapter;
    private List<String> imagesGrid = new ArrayList<>();
    private ProgressBar progressBar;
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
            
        }
    }

    private void initData(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rec_animal);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        imagesAdapter = new ImagesAdapter(imagesGrid);
        recyclerView.setAdapter(imagesAdapter);
    }

    private void showData() {
        progressBar.setVisibility(View.VISIBLE);
        ApiConfig.getApiService().getImages().enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                imagesGrid.addAll(response.body().getMessage());
                imagesGrid.size();
                imagesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
