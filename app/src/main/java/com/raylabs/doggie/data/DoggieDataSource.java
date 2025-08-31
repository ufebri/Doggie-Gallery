package com.raylabs.doggie.data;

import androidx.lifecycle.LiveData;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.vo.Resource;

import java.util.List;

public interface DoggieDataSource {

    LiveData<Resource<List<DoggieEntity>>> getAllImage(String countItem);

    LiveData<Resource<List<DoggieEntity>>> getLikedImage(String countItem);

    LiveData<Resource<List<DoggieEntity>>> getPopularImage(String countItem);

    LiveData<Resource<List<DoggieEntity>>> getCategories();
}
