package com.bedboy.ufebri.doggie.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bedboy.ufebri.doggie.config.Constant;
import com.bedboy.ufebri.doggie.data.DoggieRepository;
import com.bedboy.ufebri.doggie.data.source.local.entity.DoggieEntity;
import com.bedboy.ufebri.doggie.vo.Resource;

import java.util.List;

public class CategoriesViewModel extends ViewModel {

    private final DoggieRepository repository;
    private LiveData<Resource<List<DoggieEntity>>> data;

    public CategoriesViewModel(DoggieRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<DoggieEntity>>> getData() {
        if (data == null) data = repository.getCategories();
        return data;
    }
}
