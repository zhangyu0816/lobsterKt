package com.yimi.rentme

import com.yimi.rentme.bean.*
import com.yimi.rentme.roomdata.ChatListInfo
import com.zb.baselibs.bean.AliPay
import com.zb.baselibs.bean.HttpWrapBean
import com.zb.baselibs.bean.WXPay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // 上传图片
    @Multipart
    @POST("YmUpload_image")
    suspend fun uploadImages(
        @Part("isCompre") isCompre: RequestBody,
        @Part("isCutImage") isCutImage: RequestBody,
        @Part("fileFileName") fileName: RequestBody,
        @Part("fileContentType") fileContentType: RequestBody,
        @Part file: MultipartBody.Part
    ): HttpWrapBean<ResourceUrl>

    // 上传视频
    @Multipart
    @POST("YmUpload_videoFile")
    suspend fun uploadVideo(
        @Part("fileFileName") fileName: RequestBody,
        @Part file: MultipartBody.Part
    ): HttpWrapBean<ResourceUrl>

    // 上传语音
    @Multipart
    @POST("YmUpload_soundFile")
    suspend fun uploadSound(
        @Part("fileFileName") fileName: RequestBody,
        @Part file: MultipartBody.Part
    ): HttpWrapBean<ResourceUrl>

    // 功能开关
    @GET("api/AppCommon_functionSwitch")
    suspend fun functionSwitch(): HttpWrapBean<CommonSwitch>

    // 图片验证码
    @GET("api/ImageCaptca_findImageCaptcha")
    suspend fun findImageCaptcha(): HttpWrapBean<ImageCaptcha>

    // 我的信息
    @GET("api/Member_myInfo")
    suspend fun myInfo(): HttpWrapBean<MineInfo>

    // 修改个人信息
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfo")
    suspend fun modifyMemberInfo(@FieldMap map: Map<String, String>): HttpWrapBean<Any?>

    // 修改个人信息--不审核
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfoForNoVerify")
    suspend fun modifyMemberInfoForNoVerify(@FieldMap map: Map<String, Any>): HttpWrapBean<Any?>

    // 快捷登录
    @FormUrlEncoded
    @POST("api/Union_loginByUnionV2")
    suspend fun loginByUnion(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 用户注册验证码
    @FormUrlEncoded
    @POST("api/Login_registCaptcha")
    suspend fun registerCaptcha(@Field("userName") userName: String): HttpWrapBean<Any?>

    // 验证 注册验证码
    @FormUrlEncoded
    @POST("api/Login_verifyCaptcha")
    suspend fun verifyCaptcha(
        @Field("userName") userName: String,
        @Field("captcha") captcha: String
    ): HttpWrapBean<Any?>

    // 获取绑定手机号短信验证码
    @FormUrlEncoded
    @POST("api/Union_banderCaptcha")
    suspend fun banderCaptcha(
        @Field("userName") userName: String,
        @Field("imageCaptchaToken") imageCaptchaToken: String,
        @Field("imageCaptchaCode") imageCaptchaCode: String
    ): HttpWrapBean<Any?>

    // 绑定手机号
    @FormUrlEncoded
    @POST("api/Union_bindingPhone")
    suspend fun bindingPhone(
        @Field("userName") userName: String,
        @Field("captcha") captcha: String
    ): HttpWrapBean<Any?>

    // 验证 手机号是否注册
    @FormUrlEncoded
    @POST("api/Login_checkUserName")
    suspend fun checkUserName(@Field("userName") userName: String): HttpWrapBean<CheckUser>

    // 用户登录验证码
    @FormUrlEncoded
    @POST("api/Login_loginCaptcha")
    suspend fun loginCaptcha(@Field("userName") userName: String): HttpWrapBean<Any?>

    // 根据验证码登录
    @FormUrlEncoded
    @POST("api/Login_captchaLogin")
    suspend fun loginByCaptcha(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 根据密码登录
    @FormUrlEncoded
    @POST("api/Login_login")
    suspend fun loginByPass(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 用户注册
    @FormUrlEncoded
    @POST("api/Login_regist")
    suspend fun register(@FieldMap map: Map<String, String>): HttpWrapBean<LoginInfo>

    // 钱包和受欢迎信息
    @GET("api/Member_walletAndPop")
    suspend fun walletAndPop(): HttpWrapBean<WalletInfo>

    // 更新设备信息
    @FormUrlEncoded
    @POST("api/Login_modifyPushInfo")
    suspend fun modifyPushInfo(
        @Field("deviceCode") deviceCode: String,
        @Field("channelId") channelId: String,
        @Field("deviceHardwareInfo") deviceHardwareInfo: String,
        @Field("usePl") usePl: Int
    ): HttpWrapBean<Any?>

    // 我关注的人的动态列表
    @FormUrlEncoded
    @POST("api/Interactive_attentionDyn")
    suspend fun attentionDyn(
        @Field("pageNo") pageNo: Int,
        @Field("timeSortType") timeSortType: Int
    ): HttpWrapBean<ArrayList<DiscoverInfo>>

    // 获取他人信息
    @FormUrlEncoded
    @POST("api/Contact_otherInfo")
    suspend fun otherInfo(@Field("otherUserId") otherUserId: Long): HttpWrapBean<MemberInfo>

    // 推荐
    @FormUrlEncoded
    @POST("api/Interactive_dynPiazzaList")
    suspend fun dynPiazzaList(@FieldMap map: Map<String, String>): HttpWrapBean<ArrayList<DiscoverInfo>>

    // 个人动态
    @FormUrlEncoded
    @POST("api/Interactive_personOtherDyn")
    suspend fun personOtherDyn(@FieldMap map: Map<String, String>): HttpWrapBean<ArrayList<DiscoverInfo>>

    // 取消点赞
    @FormUrlEncoded
    @POST("api/Interactive_dynCancelLike")
    suspend fun dynCancelLike(@Field("friendDynId") friendDynId: Long): HttpWrapBean<Any?>

    // 给动态点赞
    @FormUrlEncoded
    @POST("api/Interactive_dynDoLike")
    suspend fun dynDoLike(@Field("friendDynId") friendDynId: Long): HttpWrapBean<Any?>

    // 搜索
    @FormUrlEncoded
    @POST("api/SimpleRent_search")
    suspend fun search(@FieldMap map: Map<String, String>): HttpWrapBean<ArrayList<MemberInfo>>

    // 动态详情
    @FormUrlEncoded
    @POST("api/Interactive_dynDetail")
    suspend fun dynDetail(@Field("friendDynId") friendDynId: Long): HttpWrapBean<DiscoverInfo>

    // 动态访问
    @FormUrlEncoded
    @POST("api/Interactive_dynVisit")
    suspend fun dynVisit(@Field("friendDynId") friendDynId: Long): HttpWrapBean<Any?>

    // 分享
    @GET("api/Share_memberInfoConf")
    suspend fun memberInfoConf(): HttpWrapBean<ShareInfo>

    // 关注状态
    @FormUrlEncoded
    @POST("api/Collect_attentionStatus")
    suspend fun attentionStatus(@Field("otherUserId") otherUserId: Long): HttpWrapBean<Any?>

    // 关注他人
    @FormUrlEncoded
    @POST("api/Collect_attentionOther")
    suspend fun attentionOther(@Field("otherUserId") otherUserId: Long): HttpWrapBean<Any?>

    // 取消关注
    @FormUrlEncoded
    @POST("api/Collect_cancelAttention")
    suspend fun cancelAttention(@Field("otherUserId") otherUserId: Long): HttpWrapBean<Any?>

    // 查看评论
    @FormUrlEncoded
    @POST("api/Interactive_seeReviews")
    suspend fun seeReviews(
        @Field("friendDynId") friendDynId: Long, @Field("timeSortType") timeSortType: Int,
        @Field("pageNo") pageNo: Int, @Field("row") row: Int
    ): HttpWrapBean<ArrayList<Review>>

    // 查看喜欢
    @FormUrlEncoded
    @POST("api/Interactive_seeLikers")
    suspend fun seeLikers(
        @Field("friendDynId") friendDynId: Long,
        @Field("pageNo") pageNo: Int,
        @Field("pageSize") pageSize: Int
    ): HttpWrapBean<ArrayList<Review>>

    // 给动态评论
    @FormUrlEncoded
    @POST("api/Interactive_dynDoReview")
    suspend fun dynDoReview(@FieldMap map: Map<String, String>): HttpWrapBean<Any?>

    // 打赏列表
    @FormUrlEncoded
    @POST("api/Interactive_seeGiftRewards")
    suspend fun seeGiftRewards(
        @Field("friendDynId") friendDynId: Long, @Field("rewardSortType") rewardSortType: Int,
        @Field("pageNo") pageNo: Int, @Field("row") row: Int
    ): HttpWrapBean<ArrayList<Reward>>

    // 打赏列表
    @FormUrlEncoded
    @POST("api/Interactive_seeUserGiftRewards")
    suspend fun seeUserGiftRewards(
        @Field("otherUserId") otherUserId: Long, @Field("rewardSortType") rewardSortType: Int,
        @Field("pageNo") pageNo: Int, @Field("row") row: Int
    ): HttpWrapBean<ArrayList<Reward>>

    // 礼物列表
    @GET("api/Gift_giftList")
    suspend fun giftList(): HttpWrapBean<ArrayList<GiftInfo>>

    // 充值
    @FormUrlEncoded
    @POST("api/Tran_rechargeDiscountList")
    suspend fun rechargeDiscountList(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<RechargeInfo>>

    // 充钱到我的钱包
    @FormUrlEncoded
    @POST("api/Tran_rechargeWallet")
    suspend fun rechargeWallet(
        @Field("money") money: Double,
        @Field("moneyDiscountId") moneyDiscountId: Long
    ): HttpWrapBean<OrderTran>

    // 用支付宝支付交易
    @FormUrlEncoded
    @POST("api/Pay_alipayFastPayTran")
    suspend fun alipayFastPayTran(@Field("tranOrderId") tranOrderId: String): HttpWrapBean<AliPay>

    // 用微信支付交易
    @FormUrlEncoded
    @POST("api/Pay_wxpayAppPayTran")
    suspend fun wxpayAppPayTran(@Field("tranOrderId") tranOrderId: String): HttpWrapBean<WXPay>

    // 用钱包支付交易
    @FormUrlEncoded
    @POST("api/Pay_walletPayTran")
    suspend fun walletPayTran(@Field("tranOrderId") tranOrderId: String): HttpWrapBean<Any?>

    // 创建订单
    @FormUrlEncoded
    @POST("api/Gift_submitOrder")
    suspend fun submitOrder(
        @Field("friendDynId") friendDynId: Long, @Field("giftId") giftId: Long,
        @Field("giftNum") giftNum: Int
    ): HttpWrapBean<OrderNumber>

    // 创建订单
    @FormUrlEncoded
    @POST("api/Gift_submitUserOrder")
    suspend fun submitUserOrder(
        @Field("otherUserId") otherUserId: Long, @Field("giftId") giftId: Long,
        @Field("giftNum") giftNum: Int
    ): HttpWrapBean<OrderNumber>

    // 举报类型
    @GET("api/Complain_comType")
    suspend fun comType(): HttpWrapBean<ArrayList<Report>>

    // 举报
    @FormUrlEncoded
    @POST("api/Complain_comsub")
    suspend fun comsub(
        @Field("complainTypeId") complainTypeId: Long,
        @Field("comUserId") comUserId: Long,
        @Field("comText") comText: String,
        @Field("images") images: String
    ): HttpWrapBean<Any?>

    // 删除动态
    @FormUrlEncoded
    @POST("api/Interactive_deleteDyn")
    suspend fun deleteDyn(@Field("friendDynId") friendDynId: Long): HttpWrapBean<Any?>

    // 评估
    @FormUrlEncoded
    @POST("api/Pair_makeEvaluate")
    suspend fun makeEvaluate(
        @Field("otherUserId") otherUserId: Long,
        @Field("likeOtherStatus") likeOtherStatus: Int
    ): HttpWrapBean<Int>

    // 首充
    @GET("api/MemberOrder_isFirstOpenMember")
    suspend fun firstOpenMemberPage(): HttpWrapBean<Int>

    // 会员价格
    @GET("api/MemberOrder_openedMemberPriceList")
    suspend fun openedMemberPriceList(): HttpWrapBean<ArrayList<VipInfo>>

    // 提交VIP订单 - 需登录
    @FormUrlEncoded
    @POST("api/MemberOrder_submitOpenedMemberOrder")
    suspend fun submitOpenedMemberOrder(
        @Field("memberOfOpenedProductId") memberOfOpenedProductId: Long,
        @Field("productCount") productCount: Int
    ): HttpWrapBean<OrderNumber>

    // 获取交易订单号
    @FormUrlEncoded
    @POST("api/MemberOrder_payOrderForTran")
    suspend fun payOrderForTran(@Field("orderNumber") orderNumber: String): HttpWrapBean<OrderTran>

    // 访问他人
    @FormUrlEncoded
    @POST("api/Interactive_otherUserInfoVisit")
    suspend fun otherUserInfoVisit(@Field("otherUserId") otherUserId: Long): HttpWrapBean<Any?>

    // 他人出租信息
    @FormUrlEncoded
    @POST("api/SimpleRent_otherInfo")
    suspend fun otherRentInfo(@Field("otherUserId") otherUserId: Long): HttpWrapBean<RentInfo>

    // 三合一接口 （返回关注数量、粉丝数量、喜欢数量、被喜欢数量）
    @FormUrlEncoded
    @POST("api/Collect_contactNum")
    suspend fun contactNum(@Field("otherUserId") otherUserId: Long): HttpWrapBean<ContactNum>

    // 我关注的
    @FormUrlEncoded
    @POST("api/Collect_myConcerns")
    suspend fun myConcerns(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<MemberInfo>>

    // 别人的关注
    @FormUrlEncoded
    @POST("api/Collect_otherConcerns")
    suspend fun otherConcerns(
        @Field("pageNo") pageNo: Int,
        @Field("otherUserId") otherUserId: Long
    ): HttpWrapBean<ArrayList<MemberInfo>>

    // 我的粉丝
    @FormUrlEncoded
    @POST("api/Collect_myFans")
    suspend fun myFans(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<MemberInfo>>

    // 别人的粉丝
    @FormUrlEncoded
    @POST("api/Collect_otherFans")
    suspend fun otherFans(
        @Field("pageNo") pageNo: Int,
        @Field("otherUserId") otherUserId: Long
    ): HttpWrapBean<ArrayList<MemberInfo>>

    // 喜欢我的人列表
    @FormUrlEncoded
    @POST("api/Pair_likeMeList")
    suspend fun likeMeList(@FieldMap map: Map<String, String>): HttpWrapBean<ArrayList<LikeMe>>

    // 看过我的
    @FormUrlEncoded
    @POST("api/Interactive_visitorBySeeMeList")
    suspend fun visitorBySeeMeList(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<MemberInfo>>

    // 解除匹配
    @FormUrlEncoded
    @POST("api/Pair_relievePair")
    suspend fun relievePair(@Field("otherUserId") otherUserId: Long): HttpWrapBean<Any?>

    // 更新(提交当前位置)
    @FormUrlEncoded
    @POST("api/Pair_updatePairPool")
    suspend fun updatePairPool(@FieldMap map: HashMap<String, String>): HttpWrapBean<Any?>

    // 发布动态
    @FormUrlEncoded
    @POST("api/Interactive_publishDyn")
    suspend fun publishDyn(
        @Field("text") text: String, @Field("images") images: String,
        @Field("videoUrl") videoUrl: String, @Field("resTime") resTime: Int,
        @Field("isSyncPiazza") isSyncPiazza: Int, @Field("isPrivate") isPrivate: Int,
        @Field("isAppearance") isAppearance: Int, @Field("addressInfo") addressInfo: String,
        @Field("friendTitle") friendTitle: String
    ): HttpWrapBean<Any?>

    // 寻找漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_findBottle")
    suspend fun findBottle(@FieldMap map: Map<String, String>): HttpWrapBean<BottleInfo>

    // 随机获取一条最新动态
    @FormUrlEncoded
    @POST("api/Interactive_randomNewDyn")
    suspend fun randomNewDyn(@FieldMap map: Map<String, String>): HttpWrapBean<DiscoverInfo>

    // 拾取漂流瓶(包括销毁漂流瓶)
    @FormUrlEncoded
    @POST("api/DriftBottle_pcikBottle")
    suspend fun pickBottle(
        @Field("driftBottleId") driftBottleId: Long,
        @Field("driftBottleType") driftBottleType: Int
    ): HttpWrapBean<Any?>

    // 投掷漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_castBottle")
    suspend fun castBottle(@Field("text") text: String): HttpWrapBean<Any?>

    // 漂流瓶未读会话列表
    @FormUrlEncoded
    @POST("api/DriftBottle_chatList")
    suspend fun driftBottleChatList(
        @Field("isPublicAccount") isPublicAccount: Int,
        @Field("pageNo") pageNo: Int
    ): HttpWrapBean<ArrayList<BottleCache>>

    // 我的漂流瓶列表
    @FormUrlEncoded
    @POST("api/DriftBottle_myBottleList")
    suspend fun myBottleList(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<BottleInfo>>

    // 清除与这个人的消息
    @FormUrlEncoded
    @POST("api/DriftBottle_clearHistoryMsg")
    suspend fun clearAllDriftBottleHistoryMsg(
        @Field("otherUserId") otherUserId: Long,
        @Field("driftBottleId") driftBottleId: Long
    ): HttpWrapBean<Any?>

    // 我的漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_myBottle")
    suspend fun myBottle(@Field("driftBottleId") driftBottleId: Long): HttpWrapBean<BottleInfo>

    // 漂流瓶历史记录
    @FormUrlEncoded
    @POST("api/DriftBottle_historyMsgList")
    suspend fun bottleHistoryMsgList(
        @Field("otherUserId") otherUserId: Long, @Field("driftBottleId") driftBottleId: Long,
        @Field("pageNo") pageNo: Int
    ): HttpWrapBean<ArrayList<PrivateMsg>>

    // 清空用户消息
    @FormUrlEncoded
    @POST("api/DriftBottle_readOverHistoryMsg")
    suspend fun readOverDriftBottleHistoryMsg(
        @Field("otherUserId") otherUserId: Long, @Field("driftBottleId") driftBottleId: Long,
        @Field("messageId") messageId: Long
    ): HttpWrapBean<Any?>

    // 获取阿里百川登录账号
    @FormUrlEncoded
    @POST("api/Contact_myImAccountInfo")
    suspend fun myImAccountInfo(@Field("imPlatformType") imPlatformType: Int): HttpWrapBean<ImAccount>

    // 获取别人阿里百川账号
    @FormUrlEncoded
    @POST("api/Contact_otherImAccountInfo")
    suspend fun otherImAccountInfo(
        @Field("otherUserId") otherUserId: Long,
        @Field("imPlatformType") imPlatformType: Int
    ): HttpWrapBean<ImAccount>

    // 预匹配列表
    @FormUrlEncoded
    @POST("api/Pair_prePairList")
    suspend fun prePairList(@FieldMap map: Map<String, String>): HttpWrapBean<ArrayList<PairInfo>>

    // 加入匹配池 (提交当前位置)
    @FormUrlEncoded
    @POST("api/Pair_joinPairPool")
    suspend fun joinPairPool(@FieldMap map: Map<String, String>): HttpWrapBean<Any?>

    // 自动喜欢
    @GET("api/Pair_pushGoodUser")
    suspend fun pushGoodUser(): HttpWrapBean<Int>

    // 访问数量
    @GET("api/Interactive_visitorBySeeMeCountV2")
    suspend fun visitorBySeeMeCount(): HttpWrapBean<VisitorCount>

    // 显示爱情盲盒
    @GET("api/Share_setSendMessageForApp")
    suspend fun getAi(): HttpWrapBean<Int>

    // 清除全部未读消息
    @FormUrlEncoded
    @POST("api/Interactive_readNewDynMsgAll")
    suspend fun readNewDynMsgAll(@Field("reviewType") reviewType: Int): HttpWrapBean<Any?>

    // 新消息列表
    @FormUrlEncoded
    @POST("api/Interactive_dynNewMsgListV2")
    suspend fun dynNewMsgList(
        @Field("pageNo") pageNo: Int,
        @Field("reviewType") reviewType: Int
    ): HttpWrapBean<ArrayList<MineNews>>

    // 我的新消息数量(礼物、评论、点赞)
    @GET("api/Interactive_newDynMsgAllNum")
    suspend fun newDynMsgAllNum(): HttpWrapBean<NewsCount>

    // 系统消息未读
    @GET("api/SystemMsg_chat")
    suspend fun systemChat(): HttpWrapBean<SystemNews>

    // 未读会话列表
    @FormUrlEncoded
    @POST("api/Contact_thirdChatList")
    suspend fun chatList(
        @Field("pageNo") pageNo: Int, @Field("pageSize") pageSize: Int,
        @Field("isPublicAccount") isPublicAccount: Int
    ): HttpWrapBean<ArrayList<ChatListInfo>>

    // 闪聊未读会话列表
    @FormUrlEncoded
    @POST("api/FlashTalk_chatList")
    suspend fun flashChatList(
        @Field("pageNo") pageNo: Int, @Field("pageSize") pageSize: Int,
        @Field("isPublicAccount") isPublicAccount: Int
    ): HttpWrapBean<ArrayList<ChatListInfo>>

    // 匹配列表
    @FormUrlEncoded
    @POST("api/Pair_pairList")
    suspend fun pairList(@Field("pageNo") pageNo: Int): HttpWrapBean<ArrayList<LikeMe>>
}