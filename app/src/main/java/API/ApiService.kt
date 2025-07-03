package API

import DI.Models.GroupModeration.BanKickUserRequest
import DI.Models.GroupModeration.DeleteMessageRequest
import DI.Models.GroupModeration.GroupUserActionRequest
import DI.Models.GroupModeration.ModerationLogResponse
import DI.Models.GroupModeration.MuteUserRequest
import DI.Models.GroupModeration.UserGroupStatusDTO
import DI.Models.Analysis.BarChart.DailySummary
import DI.Models.Analysis.BarChart.MonthlySummary
import DI.Models.Analysis.BarChart.WeeklySummary
import DI.Models.Analysis.BarChart.YearlySummary
import DI.Models.Analysis.CategoryBreakdown
import DI.Models.Auth.RefreshTokenRequest
import DI.Models.Auth.RefreshTokenResponse
import DI.Models.Auth.SignInRequest
import DI.Models.Auth.SignUpRequest
import DI.Models.BatchMessageReactionsRequestDTO
import DI.Models.Category.AddCategoryRequest
import DI.Models.Category.Category
import DI.Models.Category.Transaction
import DI.Models.Category.UpdateCategoryRequest
import DI.Models.Chat.Chat
import DI.Models.Chat.ChatMessage
import DI.Models.Chat.LatestChatResponses
import DI.Models.CreateMessageReactionDTO
import DI.Models.EnhancedMessageDTO
import DI.Models.Friend.AcceptFriendRequestResponse
import DI.Models.Friend.AddFriendRequest
import DI.Models.Friend.AddFriendResponse
import DI.Models.Friend.DeleteFriendResponse
import DI.Models.Friend.Friend
import DI.Models.Friend.FriendRequest
import DI.Models.Friend.RejectFriendRequestResponse
import DI.Models.Group.AdminLeaveResult
import DI.Models.Group.AvatarDTO
import DI.Models.Group.CreateGroupRequest
import DI.Models.Group.Group
import DI.Models.Group.GroupChatHistoryDto
import DI.Models.Group.GroupMember
import DI.Models.Group.GroupMemberProfile
import DI.Models.Group.SendGroupMessageRequest
import DI.Models.Group.UpdateGroupRequest
import DI.Models.GroupFund.CreateGroupFundDto
import DI.Models.GroupFund.DeleteResponse
import DI.Models.GroupFund.GroupFundDto
import DI.Models.GroupFund.UpdateGroupFundDto
import DI.Models.GroupTransaction.CreateGroupTransactionDto
import DI.Models.GroupTransaction.GroupTransactionDto
import DI.Models.GroupTransaction.UpdateGroupTransactionDto
import DI.Models.GroupTransactionComment.CreateGroupTransactionCommentDto
import DI.Models.GroupTransactionComment.GroupTransactionCommentDto
import DI.Models.GroupTransactionComment.UpdateGroupTransactionCommentDto
import DI.Models.MentionNotificationDTO
import DI.Models.MessageMentionDTO
import DI.Models.MessageReactionDTO
import DI.Models.MessageReactionSummaryDTO
import DI.Models.NewsFeed.Comment
import DI.Models.NewsFeed.CreateCommentRequest
import DI.Models.NewsFeed.NewsFeedResponse
import DI.Models.NewsFeed.Post
import DI.Models.NewsFeed.PostDetail
import DI.Models.NewsFeed.ReplyCommentRequest
import DI.Models.NewsFeed.ReplyCommentResponse
import DI.Models.NewsFeed.UpdatePostTargetRequest
import DI.Models.Ocr.OcrData
import DI.Models.RemoveMessageReactionDTO
import DI.Models.Reports.ReportRequest
import DI.Models.UserInfo.AvatarUploadResponse
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import DI.Models.Wallet.AddWalletRequest
import DI.Models.Wallet.Wallet
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Authentication

    @POST("Accounts/SignUp")
    suspend fun signUp(@Body request: SignUpRequest): Response<ResponseBody>

    @POST("Accounts/SignIn")
    suspend fun signIn(@Body request: SignInRequest): Response<ResponseBody>

    @POST("Accounts/RefreshToken")
    suspend fun refreshToken(@Body token: RefreshTokenRequest): RefreshTokenResponse

    // Categories

    @GET("Categories")
    suspend fun getCategories(): List<Category>

    @POST("Categories")
    suspend fun addCategory(@Body request: AddCategoryRequest): Response<Category>

    @GET("Categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): Response<Category>

    @PUT("Categories")
    suspend fun updateCategory(@Body request: UpdateCategoryRequest): Response<Category>

    @DELETE("Categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<ResponseBody>


    // Wallets
    @GET("Wallets")
    suspend fun getWallets(): List<Wallet>

    @GET("Wallets/{id}")
    suspend fun getWalletById(@Path("id") id: String): Response<Wallet>

    @POST("Wallets")
    suspend fun createWallet(@Body request: AddWalletRequest): Response<Wallet>

    @PUT("Wallets")
    suspend fun updateWallet(@Body wallet: Wallet): Response<Wallet>

    @DELETE("Wallets/{id}")
    suspend fun deleteWallet(@Path("id") id: String): Response<ResponseBody>


    @GET("Transactions")
    suspend fun getTransactions(): List<Transaction>

    @PUT("Transactions")
    suspend fun updateTransaction(@Body transaction: Transaction): Response<Transaction>

    @POST("Transactions")
    suspend fun createTransaction(@Body transaction: Transaction): Response<Transaction>

    @GET("Transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: String): Response<Transaction>

    @DELETE("Transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<ResponseBody>

    @GET("Transactions/date-range")
    suspend fun getTransactionsByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Response<List<Transaction>>

    @GET("Transactions/search")
    suspend fun searchTransactions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("amountRange") amountRange: String? = null,
        @Query("keywords") keywords: String? = null,
        @Query("timeRange") timeRange: String? = null,
        @Query("dayOfWeek") dayOfWeek: String? = null
    ): Response<List<Transaction>>

    @GET("Statistics/category-breakdown")
    suspend fun getCategoryBreakdown(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<CategoryBreakdown>

    // Ocr

    @POST("Gemini/extract-ocr")
    suspend fun extractOcr(@Body ocrString: String): OcrData

    // Chat

    @GET("Messages/chats")
    suspend fun getAllChats(): List<Chat>

    @GET("Messages/{receiverId}")
    suspend fun getChatWithOtherUser(@Path("receiverId") otherUserId: String): List<ChatMessage>

    @GET("Messages/latest")
    suspend fun getLatestChats(): LatestChatResponses

    @POST("Messages/read/{otherUserId}")
    suspend fun markAllMessagesAsReadFromSingleChat(@Path("otherUserId") otherUserId: String): Response<ResponseBody>

    // Friend

    @GET("Friends")
    suspend fun getAllFriends(): List<Friend>

    @POST("Friends/add")
    suspend fun addFriend(@Body request: AddFriendRequest): Response<AddFriendResponse>

    @GET("Friends/requests")
    suspend fun getFriendRequests(): List<FriendRequest>

    @POST("Friends/accept/{friendId}")
    suspend fun acceptFriendRequest(@Path("friendId") friendId: String): Response<AcceptFriendRequestResponse>

    @POST("Friends/reject/{friendId}")
    suspend fun rejectFriendRequest(@Path("friendId") friendId: String): Response<RejectFriendRequestResponse>

    @DELETE("Friends/{friendId}")
    suspend fun deleteFriend(@Path("friendId") friendId: String): Response<DeleteFriendResponse>

    // Profile

    @GET("Accounts/profile")
    suspend fun getProfile(): Profile

    @Multipart
    @POST("Accounts/avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<AvatarUploadResponse>

    @PUT("Accounts/profile")
    suspend fun updateProfile(@Body updatedProfile: UpdatedProfile): Response<Void>

    @GET("Accounts/users/{userId}")
    suspend fun getOtherUserProfile(@Path("userId") userId: String): Profile

    // Calendar
    @GET("Calendar/daily")
    suspend fun getDailySummary(@Query("date") date: String): DailySummary

    @GET("Calendar/weekly")
    suspend fun getWeeklySummary(@Query("startDate") startDate: String): WeeklySummary

    @GET("Calendar/monthly")
    suspend fun getMonthlySummary(
        @Query("year") year: String,
        @Query("month") month: String
    ): MonthlySummary

    @GET("Calendar/yearly")
    suspend fun getYearlySummary(@Query("year") year: String): YearlySummary

    // Group Fund
    @GET("GroupFunds/{groupId}")
    suspend fun getGroupFundsByGroupId(@Path("groupId") groupId: String): Response<List<GroupFundDto>>

    @POST("GroupFunds")
    suspend fun createGroupFund(@Body request: CreateGroupFundDto): Response<GroupFundDto>

    @PUT("GroupFunds/{id}")
    suspend fun updateGroupFund(
        @Path("id") id: String,
        @Body request: UpdateGroupFundDto
    ): Response<GroupFundDto>

    //NewsFeed
    @GET("NewsFeed")
    suspend fun getNewsFeed(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<NewsFeedResponse>

    @Multipart
    @POST("NewsFeed")
    suspend fun createPost(
        @Query("content") content: String,
        @Query("category") category: String = "general",
        @Query("targetType") targetType: Int?,
        @Query("targetGroupIds") targetGroupIds: String? = null,
        @Part file: MultipartBody.Part? = null
    ): Response<Post>

    @GET("NewsFeed/{postId}")
    suspend fun getPostDetail(
        @Path("postId") postId: String
    ): Response<PostDetail>

    @POST("NewsFeed/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): Response<Unit>

    @DELETE("NewsFeed/{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): Response<Unit>

    @POST("NewsFeed/comment")
    suspend fun createComment(
        @Body request: CreateCommentRequest
    ): Response<Comment>

    @DELETE("NewsFeed/comment/{commentId}")
    suspend fun deleteComment(
        @Path("commentId") commentId: String
    ): Response<Unit>

    @PATCH("NewsFeed/{postId}/target")
    suspend fun updatePostTarget(
        @Path("postId") postId: String,
        @Body request: UpdatePostTargetRequest
    ): Response<Unit>

    @POST("NewsFeed/comment/reply")
    suspend fun replyToComment(
        @Body request: ReplyCommentRequest
    ): Response<ReplyCommentResponse>

    @DELETE("NewsFeed/comment/reply/{replyId}")
    suspend fun deleteReply(
        @Path("replyId") replyId: String
    ): Response<Unit>


    //Report
    @POST("Reports/generate")
    suspend fun generateReport(
        @Body request: ReportRequest
    ): Response<ResponseBody>

    @DELETE("GroupFunds/{id}")
    suspend fun deleteGroupFund(@Path("id") id: String): Response<DeleteResponse>

    // Group Transaction
    @GET("GroupTransactions/{groupFundId}")
    suspend fun getGroupTransactionsByGroupFundId(@Path("groupFundId") groupFundId: String): Response<List<GroupTransactionDto>>

    @POST("GroupTransactions")
    suspend fun createGroupTransaction(@Body dto: CreateGroupTransactionDto): Response<GroupTransactionDto>

    @PUT("GroupTransactions")
    suspend fun updateGroupTransaction(@Body dto: UpdateGroupTransactionDto): Response<GroupTransactionDto>

    @DELETE("GroupTransactions/{id}")
    suspend fun deleteGroupTransaction(@Path("id") id: String): Response<Unit>

    // Group
    @GET("/api/groups")//
    suspend fun getUserGroups(): Response<List<Group>>

    @POST("/api/groups")//
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<Group>

    @PUT("/api/groups/{groupId}")//
    suspend fun updateGroup(
        @Path("groupId") groupId: String,
        @Body request: UpdateGroupRequest
    ): Response<Group>

    // Group Member
    @GET("/api/groups/{groupId}/members")
    suspend fun getGroupMembers(@Path("groupId") groupId: String): Response<List<GroupMember>>

    @POST("/api/groups/{groupId}/members/{userId}")
    suspend fun addUserToGroup(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @DELETE("/api/groups/{groupId}/members/{userId}")
    suspend fun removeUserFromGroup(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @POST("/api/groups/{groupId}/admin-leave")
    suspend fun adminLeaveGroup(@Path("groupId") groupId: String): Response<AdminLeaveResult>

    @POST("/api/groups/{groupId}/leave")
    suspend fun leaveGroup(@Path("groupId") groupId: String): Response<Unit>

    @POST("/api/groups/{groupId}/members/{userId}/collaborator")
    suspend fun assignCollaboratorRole(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): Response<Unit>

    @GET("/api/groups/{groupId}/members/{memberId}/profile")
    suspend fun getGroupMemberProfile(
        @Path("groupId") groupId: String,
        @Path("memberId") memberId: String
    ): Response<GroupMemberProfile>

    // Group Message
    @GET("/api/groups/{groupId}/messages")
    suspend fun getGroupMessages(@Path("groupId") groupId: String): Response<GroupChatHistoryDto>

    @POST("/api/groups/messages")
    suspend fun sendGroupMessage(@Body request: SendGroupMessageRequest): Response<Unit>

    @POST("/api/groups/{groupId}/read")
    suspend fun markGroupMessagesRead(@Path("groupId") groupId: String): Response<Unit>

    // Group Transaction Comment
    @GET("GroupTransactionComment/transaction/{transactionId}")
    suspend fun getGroupTransactionComments(@Path("transactionId") transactionId: String): Response<List<GroupTransactionCommentDto>>

    @POST("GroupTransactionComment")
    suspend fun addGroupTransactionComment(@Body request: CreateGroupTransactionCommentDto): Response<GroupTransactionCommentDto>

    @PUT("GroupTransactionComment")
    suspend fun updateGroupTransactionComment(@Body request: UpdateGroupTransactionCommentDto): Response<GroupTransactionCommentDto>

    @DELETE("GroupTransactionComment/{commentId}")
    suspend fun deleteGroupTransactionComment(@Path("commentId") commentId: String): Response<Unit>

    // Group Avatar
    @Multipart
    @POST("groups/{groupId}/avatar")
    suspend fun uploadGroupAvatar(
        @Path("groupId") groupId: String,
        @Part file: MultipartBody.Part
    ): Response<AvatarDTO>

    @GET("groups/{groupId}/avatar")
    suspend fun getGroupAvatar(@Path("groupId") groupId: String): Response<AvatarDTO>

    @PUT("groups/{groupId}/avatar")
    suspend fun updateGroupAvatar(
        @Path("groupId") groupId: String,
        @Body avatarUrl: AvatarDTO
    ): Response<Unit>

    @DELETE("groups/{groupId}/avatar")
    suspend fun deleteGroupAvatar(@Path("groupId") groupId: String): Response<Unit>

    // Group Moderation
    @POST("GroupModeration/mute")
    suspend fun muteUser(@Body request: MuteUserRequest): Response<Unit>

    @POST("GroupModeration/unmute")
    suspend fun unmuteUser(@Body request: GroupUserActionRequest): Response<Unit>


    @POST("GroupModeration/ban")
    suspend fun banUser(@Body request: BanKickUserRequest): Response<Unit>


    @POST("GroupModeration/unban")
    suspend fun unbanUser(@Body request: GroupUserActionRequest): Response<Unit>

    @POST("GroupModeration/kick")
    suspend fun kickUser(@Body request: BanKickUserRequest): Response<Unit>

    @POST("GroupModeration/delete-message")
    suspend fun deleteMessage(@Body request: DeleteMessageRequest): Response<Unit>

    @POST("GroupModeration/grant-mod-role")
    suspend fun grantModRole(@Body request: GroupUserActionRequest): Response<Unit>

    @POST("GroupModeration/revoke-mod-role")
    suspend fun revokeModRole(@Body request: GroupUserActionRequest): Response<Unit>

    @GET("GroupModeration/logs")
    suspend fun getModerationLogs(
        @Path("groupId") groupId: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ModerationLogResponse>

    @GET("GroupModeration/status/{groupId}")
    suspend fun getUserGroupStatus(@Path("groupId") groupId: String): Response<UserGroupStatusDTO>

    @GET("GroupModeration/members/{groupId}")
    suspend fun getAllGroupMemberStatuses(@Path("groupId") groupId: String): Response<List<UserGroupStatusDTO>>

    // Message Enhancement
    @POST("MessageEnhancements/reactions")
    suspend fun addReaction(
        @Body request: CreateMessageReactionDTO
    ): Response<MessageReactionDTO>

    @DELETE("MessageEnhancements/reactions")
    suspend fun removeReaction(
        @Body request: RemoveMessageReactionDTO
    ): Response<Unit>

    @GET("MessageEnhancements/reactions/{messageId}")
    suspend fun getMessageReactions(
        @Path("messageId") messageId: String,
        @Query("messageType") messageType: String = "direct"
    ): Response<MessageReactionSummaryDTO>

    @POST("MessageEnhancements/reactions/batch")
    suspend fun getMultipleMessageReactions(
        @Body request: BatchMessageReactionsRequestDTO
    ): Response<Map<String, MessageReactionSummaryDTO>>

    @GET("MessageEnhancements/mentions/{messageId}")
    suspend fun getMessageMentions(
        @Path("messageId") messageId: String
    ): Response<List<MessageMentionDTO>>

    @GET("MessageEnhancements/mentions/unread")
    suspend fun getUnreadMentions(): Response<List<MentionNotificationDTO>>

    @PUT("MessageEnhancements/mentions/{mentionId}/read")
    suspend fun markMentionAsRead(
        @Path("mentionId") mentionId: String
    ): Response<Unit>

    @PUT("MessageEnhancements/mentions/read-all")
    suspend fun markAllMentionsAsRead(): Response<Int>

    @GET("MessageEnhancements/enhanced/{messageId}")
    suspend fun getEnhancedMessage(
        @Path("messageId") messageId: String,
        @Query("messageType") messageType: String = "direct"
    ): Response<EnhancedMessageDTO>

    @POST("MessageEnhancements/enhanced/batch")
    suspend fun getEnhancedMessages(
        @Body request: BatchMessageReactionsRequestDTO
    ): Response<List<EnhancedMessageDTO>>

}
