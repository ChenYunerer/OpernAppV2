package com.yun.opernv2.net;

import com.yun.opernv2.common.WeiBoUserInfo;
import com.yun.opernv2.model.BaseResponse;
import com.yun.opernv2.model.CategoryInfo;
import com.yun.opernv2.model.FeedbackInfo;
import com.yun.opernv2.model.NetEaseCloudMusicChartInfo;
import com.yun.opernv2.model.NetEaseCloudMusicChartMusicInfo;
import com.yun.opernv2.model.OpernInfo;
import com.yun.opernv2.model.UserLoginRequestInfo;
import com.yun.opernv2.net.request.GetRandomOpernReq;

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

    @POST(value = "user/login")
    Observable<BaseResponse> userLogin(@Body UserLoginRequestInfo info);

    @POST(value = "opern/randomOpern")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getRandomOpernInfo(@Body GetRandomOpernReq request);

    @GET(value = "opern/searchOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfo(@Query("searchParameter") String searchParameter);

    @GET(value = "opern/searchOpernInfoByCategory")
    Observable<BaseResponse<ArrayList<OpernInfo>>> searchOpernInfoByCategory(@Query("categoryOne") String categoryOne, @Query("categoryTwo") String categoryTwo, @Query("index") int index, @Query("numPerPage") int numPerPage);

    @GET(value = "collection/isCollected")
    Observable<BaseResponse> isCollected(@Query("userId") long userId, @Query("opernId") int opernId);

    @GET(value = "collection/add")
    Observable<BaseResponse> addCollection(@Query("userId") long userId, @Query("opernId") int opernId);

    @GET(value = "collection/remove")
    Observable<BaseResponse> removeCollection(@Query("userId") long userId, @Query("opernId") int opernId);

    @GET(value = "collection/collectionOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> getCollectionOpernInfo(@Query("userId") long userId);

    @POST(value = "feedback/commit")
    Observable<BaseResponse> commitFeedback(@Body FeedbackInfo feedbackInfo);

    @GET(value = "feedback/getFeedbackInfos")
    Observable<BaseResponse<ArrayList<FeedbackInfo>>> getFeedbackInfos(@Query("userId") long userId);

    @GET(value = "category/categoryInfo")
    Observable<BaseResponse<ArrayList<CategoryInfo>>> getCategoryInfo();

    @GET(value = "opern/latestUpdateTime")
    Observable<BaseResponse<String>> latestUpdateTime();

    @GET(value = "opern/latestUpdateOpernInfo")
    Observable<BaseResponse<ArrayList<OpernInfo>>> latestUpdateOpernInfo();

    @GET(value = "netEaseCloudMusic/musicChart")
    Observable<BaseResponse<ArrayList<NetEaseCloudMusicChartInfo>>> musicChart();

    @GET(value = "netEaseCloudMusic/musicChartMusic")
    Observable<BaseResponse<ArrayList<NetEaseCloudMusicChartMusicInfo>>> musicChartMusic(@Query("chartId") int chartId);

}
