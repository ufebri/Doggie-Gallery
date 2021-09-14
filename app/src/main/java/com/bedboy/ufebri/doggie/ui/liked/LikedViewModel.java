package com.bedboy.ufebri.doggie.ui.liked;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bedboy.ufebri.doggie.config.Constant;
import com.bedboy.ufebri.doggie.data.DoggieRepository;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.vo.Resource;

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
