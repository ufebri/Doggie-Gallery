package com.bedboy.ufebri.doggie.data;

import androidx.lifecycle.LiveData;

import com.bedboy.ufebri.doggie.vo.Resource;

import java.util.List;

public interface DoggieDataSource {

    LiveData<Resource<List<String>>> getAllImage();
}
