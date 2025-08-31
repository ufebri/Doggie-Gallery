package com.raylabs.doggie.data;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raylabs.doggie.config.Constant;
import com.raylabs.doggie.data.source.local.LocalDataSource;
import com.raylabs.doggie.data.source.local.entity.DoggieEntity;
import com.raylabs.doggie.data.source.remote.ApiResponse;
import com.raylabs.doggie.data.source.remote.RemoteDataSource;
import com.raylabs.doggie.utils.AppExecutors;
import com.raylabs.doggie.vo.Resource;
import com.raylabs.doggie.vo.Status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executor;

public class DoggieRepositoryTest {

    @Rule public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();

    private RemoteDataSource remote;
    private LocalDataSource local;
    private DoggieRepository repo;

    private static class DirectExecutor implements Executor {
        @Override public void execute(Runnable r) { r.run(); }
    }

    private static <T> T last(LiveData<T> live) {
        final Object[] box = new Object[1];
        live.observeForever(v -> box[0] = v);
        @SuppressWarnings("unchecked") T out = (T) box[0];
        return out;
    }

    private static String url(String breed) {
        // Penting: index [4] = breed (dipakai repo: response.split(\"/\")[4])
        return "https://images.dog.ceo/breeds/" + breed + "/n02088094_1007.jpg";
    }

    @Before
    public void setUp() throws Exception {
        // mock final/kelas kompleks OK berkat mockito-inline
        remote = Mockito.mock(RemoteDataSource.class);
        local  = Mockito.mock(LocalDataSource.class);

        AppExecutors executors = new AppExecutors(new DirectExecutor(), new DirectExecutor(), new DirectExecutor());

        // Reset singleton supaya setiap test dapat instance baru
        Field f = DoggieRepository.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, null);

        repo = DoggieRepository.getInstance(remote, local, executors);
    }

    @Test
    public void getAllImage_dbEmpty_fetch_thenSaveForYou_success() {
        // DB awal kosong
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getAllDoggie("for-you")).thenReturn(db);

        // Remote channel yang bisa kita emit manual
        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage("3")).thenReturn(api);

        // Saat insertDoggie dipanggil, update live data DB agar NBR membaca ulang
        doAnswer(inv -> {
            List<DoggieEntity> inserted = inv.getArgument(0);
            db.setValue(inserted);
            return null;
        }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("3");

        // awal → LOADING
        Resource<List<DoggieEntity>> first = last(live);
        assertEquals(Status.LOADING, first.status);
        assertTrue(first.data != null && first.data.isEmpty());

        // emit sukses dari remote
        api.setValue(ApiResponse.success(Arrays.asList(url("pug"), url("beagle"))));

        // hasil → SUCCESS dengan data tersimpan & tag benar
        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertEquals(2, res.data.size());
        assertEquals("for-you", res.data.get(0).getTag());
        assertEquals("pug", res.data.get(0).getType());
        assertEquals("beagle", res.data.get(1).getType());
    }

    @Test
    public void getAllImage_dbHasData_skipFetch() {
        // Seed DB
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.singletonList(new DoggieEntity("akita", url("akita"), "for-you")));
        when(local.getAllDoggie("for-you")).thenReturn(db);

        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("5");
        Resource<List<DoggieEntity>> res = last(live);

        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertEquals(1, res.data.size());
        verify(remote, never()).getAllImage(anyString());
        verify(local, never()).insertDoggie(anyList());
    }

    @Test
    public void getLikedImage_savesLikedTag() {
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getAllDoggie("liked")).thenReturn(db);

        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage("2")).thenReturn(api);

        doAnswer(inv -> { db.setValue(inv.getArgument(0)); return null; }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getLikedImage("2");
        last(live); // LOADING
        api.setValue(ApiResponse.success(Collections.singletonList(url("husky"))));

        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertEquals(1, res.data.size());
        assertEquals("liked", res.data.get(0).getTag());
        assertEquals("husky", res.data.get(0).getType());
    }

    @Test
    public void getPopularImage_savesPopularTag() {
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getAllDoggie("popular")).thenReturn(db);

        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage("1")).thenReturn(api);

        doAnswer(inv -> { db.setValue(inv.getArgument(0)); return null; }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getPopularImage("1");
        last(live); // LOADING
        api.setValue(ApiResponse.success(Collections.singletonList(url("poodle"))));

        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertEquals(1, res.data.size());
        assertEquals("popular", res.data.get(0).getTag());
        assertEquals("poodle", res.data.get(0).getType());
    }

    @Test
    public void getCategories_usesConstantCount_andForYouTag() {
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getCategoriesDogie()).thenReturn(db);

        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage(Constant.IMAGE_ITEM_COUNT_LOADED)).thenReturn(api);

        doAnswer(inv -> { db.setValue(inv.getArgument(0)); return null; }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getCategories();
        last(live); // LOADING
        api.setValue(ApiResponse.success(Collections.singletonList(url("shiba"))));

        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertEquals(1, res.data.size());
        assertEquals("for-you", res.data.get(0).getTag());
        assertEquals("shiba", res.data.get(0).getType());
    }

    @Test
    public void remoteEmpty_returnsSuccessWithDbData() {
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getAllDoggie("for-you")).thenReturn(db);

        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage("2")).thenReturn(api);

        doAnswer(inv -> { db.setValue(inv.getArgument(0)); return null; }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("2");
        last(live); // LOADING
        api.setValue(ApiResponse.empty("no more data", Collections.emptyList()));

        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.SUCCESS, res.status);
        assertNotNull(res.data);
        assertTrue(res.data.isEmpty());
    }

    @Test
    public void remoteError_returnsErrorWithDbData() {
        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
        db.setValue(Collections.emptyList());
        when(local.getAllDoggie("for-you")).thenReturn(db);

        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
        when(remote.getAllImage("2")).thenReturn(api);

        doAnswer(inv -> { db.setValue(inv.getArgument(0)); return null; }).when(local).insertDoggie(anyList());

        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("2");
        last(live); // LOADING
        api.setValue(ApiResponse.error("network down", Collections.emptyList()));

        Resource<List<DoggieEntity>> res = last(live);
        assertEquals(Status.ERROR, res.status);
        assertEquals("network down", res.message);
        assertNotNull(res.data); // boleh kosong
    }
}