package com.raylabs.doggie.ui.liked;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raylabs.doggie.config.Constant;
import com.raylabs.doggie.data.DoggieRepository;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.vo.Resource;

import java.util.List;

public class LikedViewModel extends ViewModel {

    private final DoggieRepository repository;

    public LikedViewModel(DoggieRepository repository) {
        this.repository = repository;
    }

    private LiveData<Resource<List<DoggieEntity>>> data;

    public LiveData<Resource<List<DoggieEntity>>> getImage() {
        if (data == null) data = repository.getLikedImage(Constant.IMAGE_ITEM_COUNT_LOADED);
        return data;
    }
}
