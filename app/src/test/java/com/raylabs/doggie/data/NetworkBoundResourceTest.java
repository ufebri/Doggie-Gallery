package com.raylabs.doggie.data;

import static com.raylabs.doggie.vo.Status.ERROR;
import static com.raylabs.doggie.vo.Status.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.raylabs.doggie.data.source.remote.ApiResponse;
import com.raylabs.doggie.utils.AppExecutors;
import com.raylabs.doggie.vo.Resource;
import com.raylabs.doggie.vo.Status;

import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * Unit tests for NetworkBoundResource.
 * <p>
 * Catatan:
 * - Di sini diasumsikan ApiResponse punya factory methods:
 * ApiResponse.success(body), ApiResponse.error(msg), ApiResponse.empty()
 * dan field: status, body, message seperti yang dipakai di implementasi.
 * Kalau signature proyekmu sedikit berbeda, tinggal ubah bagian createCall().
 */
public class NetworkBoundResourceTest {

    @Rule
    public InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();

    private static class DirectExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    private static class TestAppExecutors extends AppExecutors {
        public TestAppExecutors() {
            super(new DirectExecutor(), new DirectExecutor(), new DirectExecutor());
        }
    }

    /**
     * Helper observer utk menangkap value terakhir LiveData<Resource<T>>.
     */
    private static final class Capture<T> {
        Resource<T> value;
    }

    @Test
    public void whenShouldNotFetch_returnsDbSuccess() {
        // DB berisi "DB-DATA"
        MutableLiveData<String> db = new MutableLiveData<>();
        db.setValue("DB-DATA");

        NetworkBoundResource<String, String> nbr = new NetworkBoundResource<String, String>(new TestAppExecutors()) {
            @Override
            protected LiveData<String> loadFromDB() {
                return db;
            }

            @Override
            protected Boolean shouldFetch(String data) {
                return false;
            }

            @Override
            protected LiveData<ApiResponse<String>> createCall() {
                // tidak dipanggil karena shouldFetch=false
                return new MutableLiveData<>();
            }

            @Override
            protected void saveCallResult(String data) { /* no-op */ }
        };

        Capture<String> cap = new Capture<>();
        nbr.asLiveData().observeForever(res -> cap.value = res);

        assertNotNull(cap.value);
        // State terakhir harus SUCCESS dengan data dari DB
        assertEquals(Status.SUCCESS, cap.value.status);
        assertEquals("DB-DATA", cap.value.data);
        assertNull(cap.value.message);
    }

    @Test
    public void whenFetchSuccess_emitsLoadingThenSuccessWithSavedData() {
        // "DB awal" -> sesudah saveCallResult, DB jadi "NETWORK-DATA-SAVED"
        MutableLiveData<String> db = new MutableLiveData<>();
        db.setValue("DB-INIT");

        // Siapkan API live data yang bisa kita emit manual
        MutableLiveData<ApiResponse<String>> api = new MutableLiveData<>();

        final String[] stored = new String[1];

        NetworkBoundResource<String, String> nbr = new NetworkBoundResource<String, String>(new TestAppExecutors()) {
            @Override
            protected LiveData<String> loadFromDB() {
                // loadFromDB membaca dari stored jika ada, else dari db awal
                MutableLiveData<String> read = new MutableLiveData<>();
                read.setValue(stored[0] != null ? stored[0] : db.getValue());
                return read;
            }

            @Override
            protected Boolean shouldFetch(String data) {
                return true;
            }

            @Override
            protected LiveData<ApiResponse<String>> createCall() {
                return api;
            }

            @Override
            protected void saveCallResult(String data) {
                stored[0] = data + "-SAVED";
            }
        };

        Capture<String> cap = new Capture<>();
        nbr.asLiveData().observeForever(res -> cap.value = res);

        // Saat memulai fetch, harus ada LOADING dgn data DB-INIT
        assertNotNull(cap.value);
        assertEquals(Status.LOADING, cap.value.status);
        assertEquals("DB-INIT", cap.value.data);

        // Emit sukses dari API
        api.setValue(ApiResponse.success("NETWORK-DATA"));

        // Setelah sukses: saveCallResult -> loadFromDB lagi -> SUCCESS dengan data tersimpan
        assertNotNull(cap.value);
        assertEquals(Status.SUCCESS, cap.value.status);
        assertEquals("NETWORK-DATA-SAVED", cap.value.data);
        assertNull(cap.value.message);
    }

    @Test
    public void whenFetchEmpty_emitsLoadingThenSuccessWithDb() {
        MutableLiveData<String> db = new MutableLiveData<>();
        db.setValue("ONLY-DB");

        MutableLiveData<ApiResponse<String>> api = new MutableLiveData<>();

        NetworkBoundResource<String, String> nbr = new NetworkBoundResource<String, String>(new TestAppExecutors()) {
            @Override
            protected LiveData<String> loadFromDB() {
                MutableLiveData<String> read = new MutableLiveData<>();
                read.setValue(db.getValue());
                return read;
            }

            @Override
            protected Boolean shouldFetch(String data) {
                return true;
            }

            @Override
            protected LiveData<ApiResponse<String>> createCall() {
                return api;
            }

            @Override
            protected void saveCallResult(String data) { /* no-op for EMPTY */ }
        };

        Capture<String> cap = new Capture<>();
        nbr.asLiveData().observeForever(res -> cap.value = res);

        // Saat mulai fetch -> LOADING
        assertEquals(Status.LOADING, Objects.requireNonNull(cap.value).status);
        assertEquals("ONLY-DB", cap.value.data);

        // Emit EMPTY
        api.setValue(ApiResponse.empty("", ""));

        // Harus SUCCESS dengan data dari DB
        assertEquals(Status.SUCCESS, cap.value.status);
        assertEquals("ONLY-DB", cap.value.data);
        assertNull(cap.value.message);
    }

    @Test
    public void whenFetchError_emitsLoadingThenErrorWithDbAndMessage() {
        MutableLiveData<String> db = new MutableLiveData<>();
        db.setValue("CACHED");

        MutableLiveData<ApiResponse<String>> api = new MutableLiveData<>();

        final boolean[] onFetchFailedCalled = {false};

        NetworkBoundResource<String, String> nbr = new NetworkBoundResource<String, String>(new TestAppExecutors()) {
            @Override
            protected LiveData<String> loadFromDB() {
                MutableLiveData<String> read = new MutableLiveData<>();
                read.setValue(db.getValue());
                return read;
            }

            @Override
            protected Boolean shouldFetch(String data) {
                return true;
            }

            @Override
            protected LiveData<ApiResponse<String>> createCall() {
                return api;
            }

            @Override
            protected void saveCallResult(String data) { /* no-op */ }

            @Override
            protected void onFetchFailed() {
                onFetchFailedCalled[0] = true;
            }
        };

        Capture<String> cap = new Capture<>();
        nbr.asLiveData().observeForever(res -> cap.value = res);

        assertEquals(Status.LOADING, Objects.requireNonNull(cap.value).status);
        assertEquals("CACHED", cap.value.data);

        // Emit ERROR dari API
        api.setValue(ApiResponse.error("network down", ""));

        assertTrue(onFetchFailedCalled[0]);
        assertEquals(Status.ERROR, cap.value.status);
        assertEquals("network down", cap.value.message);
        // data terakhir tetap data DB
        assertEquals("CACHED", cap.value.data);
    }

    @Test
    public void equals_returnsTrueForSameObject() {
        Resource<String> resource = Resource.success("data");
        assertEquals(resource, resource);
    }

    @Test
    public void equals_returnsTrueForEqualObjects() {
        Resource<String> resource1 = new Resource<>(SUCCESS, "data", "message");
        Resource<String> resource2 = new Resource<>(SUCCESS, "data", "message");
        assertEquals(resource1, resource2);
        assertEquals(resource2, resource1);
    }

    @Test
    public void equals_returnsFalseForDifferentFields() {
        Resource<String> base = new Resource<>(SUCCESS, "data", "message");

        Resource<String> differentStatus = new Resource<>(ERROR, "data", "message");
        Resource<String> differentData = new Resource<>(SUCCESS, "otherData", "message");
        Resource<String> differentMessage = new Resource<>(SUCCESS, "data", "otherMessage");

        assertNotEquals(base, differentStatus);
        assertNotEquals(base, differentData);
        assertNotEquals(base, differentMessage);
    }

    @Test
    public void hashCode_isEqualForEqualObjects() {
        Resource<String> resource1 = new Resource<>(SUCCESS, "data", "message");
        Resource<String> resource2 = new Resource<>(SUCCESS, "data", "message");
        assertEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    public void hashCode_differsForDifferentObjects() {
        Resource<String> base = new Resource<>(SUCCESS, "data", "message");

        Resource<String> differentStatus = new Resource<>(ERROR, "data", "message");
        Resource<String> differentData = new Resource<>(SUCCESS, "otherData", "message");
        Resource<String> differentMessage = new Resource<>(SUCCESS, "data", "otherMessage");

        assertNotEquals(base.hashCode(), differentStatus.hashCode());
        assertNotEquals(base.hashCode(), differentData.hashCode());
        assertNotEquals(base.hashCode(), differentMessage.hashCode());
    }

    @Test
    public void toString_containsStatusMessageAndData() {
        Resource<String> resource = new Resource<>(SUCCESS, "data", "message");
        String str = resource.toString();
        assertTrue(str.contains("status=" + SUCCESS));
        assertTrue(str.contains("message='message'"));
        assertTrue(str.contains("data=data"));
    }
}