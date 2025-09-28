package com.raylabs.doggie.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class BaseResponseTest {

    @Test
    public void gettersSetters_shouldStoreValues() {
        BaseResponse resp = new BaseResponse();

        resp.setStatus("success");
        resp.setMessage(Arrays.asList("hound", "pug"));

        assertEquals("success", resp.getStatus());
        assertNotNull(resp.getMessage());
        assertEquals(2, resp.getMessage().size());
        assertEquals("hound", resp.getMessage().get(0));
        assertEquals("pug", resp.getMessage().get(1));
    }

    @Test
    public void gson_shouldDeserializeAndSerializeWithExpose() {
        // JSON sesuai API (status + array message)
        String json = "{\"status\":\"success\",\"message\":[\"akita\",\"beagle\"]}";

        // Gunakan Expose agar kita memastikan field @Expose dipakai
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        BaseResponse resp = gson.fromJson(json, BaseResponse.class);
        assertNotNull(resp);
        assertEquals("success", resp.getStatus());
        assertEquals(Arrays.asList("akita", "beagle"), resp.getMessage());

        // Serialize kembali dan cek field yang keluar
        String out = gson.toJson(resp);
        // out harus mengandung status dan message (sesuai @SerializedName)
        assertTrue(out.contains("\"status\":\"success\""));
        assertTrue(out.contains("\"message\":[\"akita\",\"beagle\"]"));
    }

    @Test
    public void gson_shouldHandleEmptyMessageArray() {
        String json = "{\"status\":\"success\",\"message\":[]}";
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        BaseResponse resp = gson.fromJson(json, BaseResponse.class);
        assertNotNull(resp);
        assertEquals("success", resp.getStatus());
        assertNotNull(resp.getMessage());
        assertEquals(0, resp.getMessage().size());

        // set message kosong lalu serialize
        resp.setMessage(Collections.emptyList());
        String out = gson.toJson(resp);
        assertTrue(out.contains("\"message\":[]"));
    }
}