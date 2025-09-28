package com.raylabs.doggie.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raylabs.doggie.data.DoggieRepository
import com.raylabs.doggie.di.Injection
import com.raylabs.doggie.ui.categories.CategoriesViewModel
import com.raylabs.doggie.ui.categories.detail.BreedGalleryViewModel
import com.raylabs.doggie.ui.home.HomeViewModel
import com.raylabs.doggie.ui.liked.LikedViewModel
import com.raylabs.doggie.ui.popular.PopularViewModel

class ViewModelFactory private constructor(
    private val doggieRepository: DoggieRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(doggieRepository)
            modelClass.isAssignableFrom(LikedViewModel::class.java) -> LikedViewModel(
                doggieRepository
            )

            modelClass.isAssignableFrom(PopularViewModel::class.java) -> PopularViewModel(
                doggieRepository
            )

            modelClass.isAssignableFrom(CategoriesViewModel::class.java) -> CategoriesViewModel(
                doggieRepository
            )

            modelClass.isAssignableFrom(BreedGalleryViewModel::class.java) -> BreedGalleryViewModel(
                doggieRepository
            )

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    Injection.provideRepository(application.applicationContext)
                ).also { instance = it }
            }
        }
    }
}
