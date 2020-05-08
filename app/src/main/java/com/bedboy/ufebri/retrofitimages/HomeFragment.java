package com.bedboy.ufebri.retrofitimages;

import android.os.Bundle;
import android.util.Log;
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

import com.bedboy.ufebri.retrofitimages.Utils.BaseApps;
import com.bedboy.ufebri.retrofitimages.Utils.Images;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {


    private ImagesAdapter imagesAdapter;
    private List<String> imagesGrid = new ArrayList<>();
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.pb_home);
        initData(view);
        showData();
        return view;
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
        BaseApps.service.getImages().enqueue(new Callback<Images>() {
            @Override
            public void onResponse(Call<Images> call, Response<Images> response) {
                imagesGrid.addAll(response.body().getMessage());
                imagesGrid.size();
                imagesAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Images> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
