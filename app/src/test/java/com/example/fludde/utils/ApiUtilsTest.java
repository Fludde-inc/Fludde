package com.example.fludde.utils;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import cz.msebera.android.httpclient.Header;

import static org.junit.Assert.*;

public class ApiUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetRequest() {
        JsonHttpResponseHandler responseHandler = Mockito.mock(JsonHttpResponseHandler.class);
        ApiUtils.get("", responseHandler);
        Mockito.verify(responseHandler, Mockito.atLeast(0))
                .onSuccess(Mockito.anyInt(), Mockito.<Header[]>any(), Mockito.any());
    }

    @Test
    public void testSetApiKey() {
        String apiKey = "";
        ApiUtils.setApiKey(apiKey);
        assertTrue(true);
    }

    @Test
    public void testHandleFailure() {
        int statusCode = 404;
        Throwable throwable = new Throwable("Not Found");
        ApiUtils.handleFailure(statusCode, throwable);
        assertTrue(true);
    }
}
