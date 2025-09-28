package com.raylabs.doggie.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.raylabs.doggie.data.DoggieRepository;
import com.raylabs.doggie.di.Injection;
import com.raylabs.doggie.ui.categories.CategoriesViewModel;
import com.raylabs.doggie.ui.categories.detail.BreedGalleryViewModel;
import com.raylabs.doggie.ui.home.HomeViewModel;
import com.raylabs.doggie.ui.liked.LikedViewModel;
import com.raylabs.doggie.ui.popular.PopularViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private static volatile ViewModelFactory INSTANCE;
    private final Application application;
    private final DoggieRepository doggieRepository;

    public ViewModelFactory(Application application, DoggieRepository doggieRepository) {
        this.application = application;
        this.doggieRepository = doggieRepository;
    }

    public static ViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                INSTANCE = new ViewModelFactory(application, Injection.provideRepository(application.getApplicationContext()));
            }
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) new HomeViewModel(doggieRepository);
        }
        if (modelClass.isAssignableFrom(LikedViewModel.class)) {
            return (T) new LikedViewModel(doggieRepository);
        }
        if (modelClass.isAssignableFrom(PopularViewModel.class)) {
            return (T) new PopularViewModel(doggieRepository);
        }
        if (modelClass.isAssignableFrom(CategoriesViewModel.class)) {
            return (T) new CategoriesViewModel(doggieRepository);
        }
        if (modelClass.isAssignableFrom(BreedGalleryViewModel.class)) {
            return (T) new BreedGalleryViewModel(doggieRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
