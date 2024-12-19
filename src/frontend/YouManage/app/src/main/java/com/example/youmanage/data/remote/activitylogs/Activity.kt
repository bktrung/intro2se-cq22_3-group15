package com.example.youmanage.data.remote.activitylogs

import com.google.gson.annotations.SerializedName

data class Activity(
    val id: Int,
    val project: Int,
    val user: String,
    val action: String,
    val changes: Changes,
    val description: String,
    val timestamp: String
)

data class Changes(
    val users: List<String>? = null, // Trường hợp thêm/xóa thành viên
    val title: Any? = null, // Trường hợp cập nhật tiêu đề (old/new)
    val description: Any? = null, // Trường hợp cập nhật mô tả (old/new)
    val status: Any? = null, // Trường hợp cập nhật trạng thái (old/new)
    val priority: Any? = null, // Trường hợp cập nhật độ ưu tiên (old/new)
    val assignee: String? = null, // Trường hợp chỉ định người thực hiện
    val start_date: String? = null, // Ngày bắt đầu
    val end_date: String? = null, // Ngày kết thúc
    val role_name: String? = null, // Tên role
    val action: String? = null // Hành động (assigned/removed)
)
// Project Update
data class ProjectUpdateChanges(
    val name: ChangeDetail,
    val description: ChangeDetail,
    @SerializedName("duedate")
    val dueDate: ChangeDetail
)

data class MemberChanges(
    val users: List<String>
)

data class TaskUpdateChange(
    val title: ChangeDetail,
    val description: ChangeDetail,
    val status: ChangeDetail,
    val priority: ChangeDetail
)

data class TaskDeleteChange(
    val title: String,
    val description: String,
    val status: String,
    val priority: String,
    val assignee: String?, // Có thể null nếu không có người được gán
    val startDate: String?, // Dữ liệu ISO 8601, nullable nếu không có giá trị
    val endDate: String? // Dữ liệu ISO 8601, nullable nếu không có giá trị
)

data class CommentChange(
    val content: String
)

data class IssueUpdateChanges(
    val title: ChangeDetail?,
    val status: ChangeDetail?
)

data class IssueDeleteChanges(
    val title: ChangeDetail,
    val description: ChangeDetail,
    val status: ChangeDetail
)

data class RoleChanges(
    @SerializedName("role_name")
    val name: ChangeDetail,
    val description: ChangeDetail
)

data class ChangeDetail(
    val old: String,
    val new: String
)

data class TaskUpdateChanges(
    val status: ChangeDetail
)

data class MemberAddChanges(
    val users: List<String>
)


