package com.raylabs.doggie.data;

import androidx.lifecycle.LiveData;
import androidx.paging.PagingData;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.vo.BreedCategory;
import com.raylabs.doggie.vo.Resource;

import java.util.List;

import kotlinx.coroutines.flow.Flow;

public interface DoggieDataSource {

    LiveData<Resource<List<DoggieEntity>>> getAllImage(String countItem);

    LiveData<Resource<List<DoggieEntity>>> getLikedImage(String countItem);

    LiveData<Resource<List<DoggieEntity>>> getPopularImage(String countItem);

    Flow<PagingData<BreedCategory>> getCategories();

    Flow<PagingData<String>> getBreedImages(String breed, String subBreed, int pageSize);
}
