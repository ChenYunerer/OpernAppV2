package com.yun.opernv2.net;

import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.model.BaseResponse;
import com.yun.opernv2.model.OpernInfo;
import com.yun.opernv2.net.request.AddCollectionReq;
import com.yun.opernv2.net.request.GetCollectionReq;
import com.yun.opernv2.net.request.GetRandomOpernReq;
import com.yun.opernv2.net.request.RemoveCollectionReq;
import com.yun.opernv2.net.request.SearchOpernReq;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Yun on 2017/8/10 0010.
 */
public interface ApiService {

    @GET(value = "https://api.weibo.com/2/users/show.json")
    Observable<WeiBoUserInfo> getWeiBoUserInfo(@Query("access_token") String access_token, @Query("uid") String uid);

    @POST(value = "opern/randomOpern")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getRandomOpernInfo(@Body GetRandomOpernReq request);

    @POST(value = "opern/searchOpern")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfo(@Body SearchOpernReq request);

    @GET(value = "opern/searchOpernInfoByCategory")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfoByCategory(@Query("categoryOne") String categoryOne, @Query("categoryTwo") String categoryTwo, @Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "collection/isCollected")
    Observable<BaseResponse> isCollected(@Query("userId") long userId, @Query("opernId") int opernId);

    @POST(value = "collection/add")
    Observable<BaseResponse> addCollection(@Body AddCollectionReq request);

    @POST(value = "collection/remove")
    Observable<BaseResponse> removeCollection(@Body RemoveCollectionReq request);

    @POST(value = "collection/get")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getCollectionOpernInfo(@Body GetCollectionReq request);
}
