package com.example.fludde.utils;

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

        Mockito.verify(responseHandler).onSuccess(Mockito.anyInt(), (Header[]) Mockito.any(), Mockito.any());
    }

    @Test
    public void testSetApiKey() {
        String apiKey = "";
        ApiUtils.setApiKey(apiKey);


    }

    @Test
    public void testHandleFailure() {
        int statusCode = 404;
        Throwable throwable = new Throwable("Not Found");

        ApiUtils.handleFailure(statusCode, throwable);

    }
}
