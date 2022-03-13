function like(btn, entityType, entityId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                if (data.likeCount == 0) {
                    $(btn).children("b").text("赞");
                } else {
                    $(btn).children("b").text("已赞");
                }
            } else {
                alert(data.msg);
            }
        }
    )
}