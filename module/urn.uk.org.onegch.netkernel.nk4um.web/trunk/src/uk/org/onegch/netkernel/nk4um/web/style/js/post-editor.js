$(function() {
  $("#post-content").keyup(function () {
    updatePreview();
  });

  $(".edit-bold").click(function() {
    range= $("#post-content").getSelection();

    var text;
    if (range.length == 0) {
      text= "Bold Text";
    } else {
      text= range.text;
    }

    $("#post-content").replaceSelection("'''" + text + "'''");
    updatePreview();
  });
});

function updatePreview() {
  $.post("/nk4um/post/preview", { post : $("#post-content").val() }, function(data) {
    $("#contentPreview").html(data);
  }, "html");
}