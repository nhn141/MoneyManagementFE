package DI.Models.NewsFeed

data class ReplyCommentRequest(
    val commentId: String,
    val content: String,
    val parentReplyId: String?
)

data class ReplyCommentResponse(
    val replyId: String,
    val content: String,
    val createdAt: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val commentId: String,
    val parentReplyId: String?,
    val parentReplyName: String,
    val replies: List<ReplyCommentResponse>
)

