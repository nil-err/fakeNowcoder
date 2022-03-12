$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");
    // 获取标题和内容
    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    // 发送异步请求
    $.post(
        CONTEXT_PATH + "/message/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // 在提示框显示消息
                $("#hintBody").text("发送成功！");
            } else {
                // 在提示框显示消息
                $("#hintBody").text(data.msg);
            }
            // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后自动隐藏提示框
            setTimeout(function () {
                $("#hintModal").modal("hide");
                // 成功后刷新页面
                window.location.reload();
            }, 2000)
            ;
        }
    )

}

function delete_msg() {
    var btn = this;
    var id = $(btn).prev().val();
    $.get(
        CONTEXT_PATH + "/message/delete",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).parents(".media").remove();
            } else {
                alert(data.msg);
            }
        }
    );
}