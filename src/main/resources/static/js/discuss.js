function like(btn, entityType, entityId, entityUserId, discussPostId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "discussPostId": discussPostId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                if (data.likeStatus == 0) {
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