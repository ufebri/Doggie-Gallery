package com.raylabs.doggie.data.source.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.data.source.local.room.DoggieDao;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LocalDataSourceTest {

    @Rule public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();

    private DoggieDao dao;

    @Before
    public void setUp() throws Exception {
        dao = Mockito.mock(DoggieDao.class);
        // reset singleton agar setiap test mulai dari null
        Field f = LocalDataSource.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, null);
    }

    @After
    public void tearDown() throws Exception {
        // pastikan bersih juga setelah test
        Field f = LocalDataSource.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    public void getAllDoggie_delegatesToDao_andReturnsSameLiveData() {
        String tag = "for-you";
        List<DoggieEntity> seed = Collections.singletonList(
                new DoggieEntity("pug", "https://images.dog.ceo/breeds/pug/1.jpg", "for-you")
        );
        MutableLiveData<List<DoggieEntity>> daoLive = new MutableLiveData<>();
        daoLive.setValue(seed);

        when(dao.getDataDoggie(tag)).thenReturn(daoLive);

        LocalDataSource lds = new LocalDataSource(dao);
        LiveData<List<DoggieEntity>> out = lds.getAllDoggie(tag);

        // memastikan method DAO dipanggil dengan argumen yang benar
        verify(dao).getDataDoggie(tag);
        // LiveData yang dikembalikan adalah referensi yang sama dari DAO
        assertSame(daoLive, out);
        // nilai di dalamnya juga sesuai
        out.observeForever(list -> assertEquals(seed, list));
    }

    @Test
    public void getCategoriesDogie_delegatesToDao_andReturnsSameLiveData() {
        List<DoggieEntity> seed = Arrays.asList(
                new DoggieEntity("beagle", "https://images.dog.ceo/breeds/beagle/1.jpg", "for-you"),
                new DoggieEntity("husky", "https://images.dog.ceo/breeds/husky/1.jpg", "for-you")
        );
        MutableLiveData<List<DoggieEntity>> daoLive = new MutableLiveData<>();
        daoLive.setValue(seed);

        when(dao.getCategories()).thenReturn(daoLive);

        LocalDataSource lds = new LocalDataSource(dao);
        LiveData<List<DoggieEntity>> out = lds.getCategoriesDogie();

        verify(dao).getCategories();
        assertSame(daoLive, out);
        out.observeForever(list -> assertEquals(seed, list));
    }

    @Test
    public void insertDoggie_delegatesToDao() {
        LocalDataSource lds = new LocalDataSource(dao);
        List<DoggieEntity> payload = Arrays.asList(
                new DoggieEntity("akita", "link1", "popular"),
                new DoggieEntity("poodle", "link2", "liked")
        );

        lds.insertDoggie(payload);

        verify(dao).insertDoggie(payload);
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void getInstance_returnsSameSingleton_andUsesProvidedDao() {
        LocalDataSource first = LocalDataSource.getInstance(dao);
        LocalDataSource second = LocalDataSource.getInstance(Mockito.mock(DoggieDao.class));

        assertSame(first, second); // singleton

        // pastikan instance yang terbentuk memakai DAO pertama
        String tag = "for-you";
        MutableLiveData<List<DoggieEntity>> daoLive = new MutableLiveData<>();
        when(dao.getDataDoggie(tag)).thenReturn(daoLive);

        LiveData<List<DoggieEntity>> out = second.getAllDoggie(tag);
        verify(dao).getDataDoggie(tag);
        assertSame(daoLive, out);
    }
}