package com.raylabs.doggie.ui.popular;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raylabs.doggie.config.Constant;
import com.raylabs.doggie.data.DoggieRepository;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.vo.Resource;

import java.util.List;

public class PopularViewModel extends ViewModel {

    private final DoggieRepository repository;

    private LiveData<Resource<List<DoggieEntity>>> data;

    public PopularViewModel(DoggieRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<DoggieEntity>>> getImage() {
        if (data == null) data = repository.getPopularImage(Constant.IMAGE_ITEM_COUNT_LOADED);
        return data;
    }
}
