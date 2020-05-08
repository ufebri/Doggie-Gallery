package com.bedboy.ufebri.retrofitimages;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.bedboy.ufebri.retrofitimages.Utils.BaseApps;
import com.bedboy.ufebri.retrofitimages.Utils.Images;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    ImagesAdapter imagesAdapter;
    List<String> imagesGrid = new ArrayList<>();

    private GridLayoutManager gridLayoutManager;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();
        getImage();
    }


    private void getImage() {
        progressDialog.show();
        progressDialog.setMessage("Sedang Mengambil Data");
        BaseApps.service.getImages().enqueue(new Callback<Images>() {
            @Override
            public void onResponse(Call<Images> call, Response<Images> response) {
                imagesGrid.addAll(response.body().getMessage());
                Log.d("Berhasil", response.toString() + response.body().getMessage().size());
                imagesGrid.size();
                imagesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Images> call, Throwable t) {
                Log.d("Gagal", t.getMessage());
            }
        });
    }

    private void init() {
        progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
        RecyclerView recyclerView = findViewById(R.id.rec_animal);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        imagesAdapter = new ImagesAdapter(imagesGrid);
        recyclerView.setAdapter(imagesAdapter);


    }
}
