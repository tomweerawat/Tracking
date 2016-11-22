package com.example.win81user.tracking;


import com.example.win81user.tracking.model.ServerRequest;
import com.example.win81user.tracking.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("tracking/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}
