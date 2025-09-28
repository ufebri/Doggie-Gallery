package com.raylabs.doggie.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.raylabs.doggie.config.Constant
import com.raylabs.doggie.data.DoggieRepository
import com.raylabs.doggie.data.source.local.entity.DoggieEntity
import com.raylabs.doggie.vo.Resource

class PopularViewModel(private val repository: DoggieRepository) : ViewModel() {

    private var data: LiveData<Resource<List<DoggieEntity>>>? = null

    val image: LiveData<Resource<List<DoggieEntity>>>
        get() {
            if (data == null) {
                data = repository.getPopularImage(Constant.IMAGE_ITEM_COUNT_LOADED)
            }
            return data!!
        }
}
