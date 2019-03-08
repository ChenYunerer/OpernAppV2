package com.yun.opernv2.net;

import com.yun.opernv2.model.BaseResponse;
import com.yun.opernv2.model.ScoreBaseInfoDO;
import com.yun.opernv2.model.ScorePictureInfoDO;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Yun on 2017/8/10 0010.
 */
public interface ApiService {

    @GET(value = "app/score/homeRecommend")
    Observable<BaseResponse<ArrayList<ScoreBaseInfoDO>>> homeRecommend(@Query("page") int page);

    @GET(value = "app/score/scoreBase")
    Observable<BaseResponse<ArrayList<ScoreBaseInfoDO>>> scoreBase(@Query("searchParameter") String searchParameter);

    @GET(value = "app/score/scorePicture")
    Observable<BaseResponse<ArrayList<ScorePictureInfoDO>>> scorePicture(@Query("scoreId") int scoreId);

}
