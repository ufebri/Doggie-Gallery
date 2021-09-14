package com.bedboy.ufebri.doggie.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bedboy.ufebri.doggie.data.DoggieRepository;
import com.bedboy.ufebri.doggie.di.Injection;
import com.bedboy.ufebri.doggie.ui.home.HomeViewModel;
import com.bedboy.ufebri.doggie.ui.liked.LikedViewModel;
import com.bedboy.ufebri.doggie.ui.popular.PopularViewModel;

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
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
