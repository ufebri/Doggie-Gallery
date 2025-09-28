package com.raylabs.doggie.data;

public class DoggieRepositoryTest {

//    @Rule
//    public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();
//
//    private RemoteDataSource remote;
//    private LocalDataSource local;
//    private BreedCategoryLocalDataSource breedCategoryLocalDataSource;
//    private DoggieRepository repo;
//
//    @Before
//    public void setUp() throws Exception {
//        // mock final/kelas kompleks OK berkat mockito-inline
//        remote = Mockito.mock(RemoteDataSource.class);
//        local = Mockito.mock(LocalDataSource.class);
//        breedCategoryLocalDataSource = Mockito.mock(BreedCategoryLocalDataSource.class);
//
//        Mockito.when(remote.getAllBreedsSync()).thenReturn(Collections.emptyMap());
//        Mockito.when(breedCategoryLocalDataSource.count(Mockito.<Continuation<Integer>>any())).thenReturn(0);
//        Mockito.when(breedCategoryLocalDataSource.allIds(Mockito.<Continuation<List<String>>>any())).thenReturn(Collections.emptyList());
//        Mockito.when(breedCategoryLocalDataSource.oldestEntries(
//                Mockito.anyInt(),
//                Mockito.<Continuation<List<BreedCategoryEntity>>>any()
//        )).thenReturn(Collections.emptyList());
//        Mockito.when(breedCategoryLocalDataSource.upsertAll(
//                Mockito.anyList(),
//                Mockito.<Continuation<Unit>>any()
//        )).thenReturn(Unit.INSTANCE);
//        Mockito.when(breedCategoryLocalDataSource.updatePreview(
//                Mockito.anyString(),
//                Mockito.nullable(String.class),
//                Mockito.anyLong(),
//                Mockito.<Continuation<Unit>>any()
//        )).thenReturn(Unit.INSTANCE);
//
//        AppExecutors executors = new AppExecutors(new DirectExecutor(), new DirectExecutor(), new DirectExecutor());
//
//        // Reset singleton supaya setiap test dapat instance baru
//        Field f = DoggieRepository.class.getDeclaredField("INSTANCE");
//        f.setAccessible(true);
//        f.set(null, null);
//
//        Context context = Mockito.mock(Context.class, Mockito.RETURNS_DEEP_STUBS);
//        Mockito.when(context.getApplicationContext()).thenReturn(context);
//
//        repo = DoggieRepository.getInstance(context, remote, local, breedCategoryLocalDataSource, executors);
//    }
//
//    private static <T> T last(LiveData<T> live) {
//        final Object[] box = new Object[1];
//        live.observeForever(v -> box[0] = v);
//        @SuppressWarnings("unchecked") T out = (T) box[0];
//        return out;
//    }
//
//    private static String url(String breed) {
//        // Penting: index [4] = breed (dipakai repo: response.split(\"/\")[4])
//        return "https://images.dog.ceo/breeds/" + breed + "/n02088094_1007.jpg";
//    }
//
//    @Test
//    public void getLikedImage_savesLikedTag() {
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.emptyList());
//        when(local.getAllDoggie("liked")).thenReturn(db);
//
//        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
//        when(remote.getAllImage("2")).thenReturn(api);
//
//        doAnswer(inv -> {
//            db.setValue(inv.getArgument(0));
//            return null;
//        }).when(local).insertDoggie(anyList());
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getLikedImage("2");
//        last(live); // LOADING
//        api.setValue(ApiResponse.success(Collections.singletonList(url("husky"))));
//
//        Resource<List<DoggieEntity>> res = last(live);
//        assertEquals(Status.SUCCESS, res.status);
//        assertNotNull(res.data);
//        assertEquals(1, res.data.size());
//        assertEquals("liked", res.data.get(0).getTag());
//        assertEquals("husky", res.data.get(0).getType());
//    }
//
//    @Test
//    public void getAllImage_dbEmpty_fetch_thenSaveForYou_success() {
//        // DB awal kosong
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.emptyList());
//        when(local.getAllDoggie("for-you")).thenReturn(db);
//
//        // Remote channel yang bisa kita emit manual
//        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
//        when(remote.getAllImage("3")).thenReturn(api);
//
//        // Saat insertDoggie dipanggil, update live data DB agar NBR membaca ulang
//        doAnswer(inv -> {
//            List<DoggieEntity> inserted = inv.getArgument(0);
//            db.setValue(inserted);
//            return null;
//        }).when(local).insertDoggie(anyList());
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("3");
//
//        // awal → LOADING
//        Resource<List<DoggieEntity>> first = last(live);
//        assertEquals(Status.LOADING, first.status);
//        assertTrue(first.data != null && first.data.isEmpty());
//
//        // emit sukses dari remote
//        api.setValue(ApiResponse.success(Arrays.asList(url("pug"), url("beagle"))));
//
//        // hasil → SUCCESS dengan data tersimpan & tag benar
//        Resource<List<DoggieEntity>> res = last(live);
//        assertEquals(Status.SUCCESS, res.status);
//        assertNotNull(res.data);
//        assertEquals(2, res.data.size());
//        assertEquals("for-you", res.data.get(0).getTag());
//        assertEquals("pug", res.data.get(0).getType());
//        assertEquals("beagle", res.data.get(1).getType());
//    }
//
//    @Test
//    public void getAllImage_dbHasData_skipFetch() {
//        // Seed DB
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.singletonList(new DoggieEntity("akita", url("akita"), "for-you")));
//        when(local.getAllDoggie("for-you")).thenReturn(db);
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("5");
//        Resource<List<DoggieEntity>> res = last(live);
//
//        assertEquals(Status.SUCCESS, res.status);
//        assertNotNull(res.data);
//        assertEquals(1, res.data.size());
//        verify(remote, never()).getAllImage(anyString());
//        verify(local, never()).insertDoggie(anyList());
//    }
//
//    @Test
//    public void getPopularImage_savesPopularTag() {
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.emptyList());
//        when(local.getAllDoggie("popular")).thenReturn(db);
//
//        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
//        when(remote.getAllImage("1")).thenReturn(api);
//
//        doAnswer(inv -> {
//            db.setValue(inv.getArgument(0));
//            return null;
//        }).when(local).insertDoggie(anyList());
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getPopularImage("1");
//        last(live); // LOADING
//        api.setValue(ApiResponse.success(Collections.singletonList(url("poodle"))));
//
//        Resource<List<DoggieEntity>> res = last(live);
//        assertEquals(Status.SUCCESS, res.status);
//        assertNotNull(res.data);
//        assertEquals(1, res.data.size());
//        assertEquals("popular", res.data.get(0).getTag());
//        assertEquals("poodle", res.data.get(0).getType());
//    }
//
//    @Test
//    public void remoteEmpty_returnsSuccessWithDbData() {
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.emptyList());
//        when(local.getAllDoggie("for-you")).thenReturn(db);
//
//        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
//        when(remote.getAllImage("2")).thenReturn(api);
//
//        doAnswer(inv -> {
//            db.setValue(inv.getArgument(0));
//            return null;
//        }).when(local).insertDoggie(anyList());
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("2");
//        last(live); // LOADING
//        api.setValue(ApiResponse.empty("no more data", Collections.emptyList()));
//
//        Resource<List<DoggieEntity>> res = last(live);
//        assertEquals(Status.SUCCESS, res.status);
//        assertNotNull(res.data);
//        assertTrue(res.data.isEmpty());
//    }
//
//    @Test
//    public void remoteError_returnsErrorWithDbData() {
//        MutableLiveData<List<DoggieEntity>> db = new MutableLiveData<>();
//        db.setValue(Collections.emptyList());
//        when(local.getAllDoggie("for-you")).thenReturn(db);
//
//        MutableLiveData<ApiResponse<List<String>>> api = new MutableLiveData<>();
//        when(remote.getAllImage("2")).thenReturn(api);
//
//        doAnswer(inv -> {
//            db.setValue(inv.getArgument(0));
//            return null;
//        }).when(local).insertDoggie(anyList());
//
//        LiveData<Resource<List<DoggieEntity>>> live = repo.getAllImage("2");
//        last(live); // LOADING
//        api.setValue(ApiResponse.error("network down", Collections.emptyList()));
//
//        Resource<List<DoggieEntity>> res = last(live);
//        assertEquals(Status.ERROR, res.status);
//        assertEquals("network down", res.message);
//        assertNotNull(res.data); // boleh kosong
//    }
//
//    private static class DirectExecutor implements Executor {
//        @Override
//        public void execute(Runnable r) {
//            r.run();
//        }
//    }
}
