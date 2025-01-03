package com.example.youmanage.data.remote.activitylogs

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
