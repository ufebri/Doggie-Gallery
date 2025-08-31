package com.raylabs.doggie.ui.categories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.raylabs.doggie.data.DoggieRepository;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.vo.Resource;

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
