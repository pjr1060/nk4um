$(function() {
  $("#post-content").keyup(function () {
    updatePreview();
  });

  $(".edit-heading").change(function() {
    if ($(this).val() != "default") {
      doWrap("\n" + $(this).val() + " ", " " + $(this).val() + "\n", "Heading Text")
      $(this).val("default");
    }
  });

  $(".edit-bold").click(function() {
    doWrap("'''", "'''", "Bold text")
  });

  $(".edit-italic").click(function() {
    doWrap("''", "''", "Italic text")
  });

  $(".edit-bulleted").click(function() {
    doWrap("\n* ", "\n", "Bulleted list item")
  });

  $(".edit-numbered").click(function() {
    doWrap("\n# ", "\n", "Numbered list item")
  });

  $(".edit-indented").click(function() {
    doWrap("\n: ", "\n", "Indented line")
  });

  $(".edit-xml").click(function() {
    doWrap("\n{xml}", "{/xml}\n", "<some><xml/></some>")
  });

  $(".edit-java").click(function() {
    doWrap("\n{java}", "{/java}\n", "new Hello(\"World\"); // Java");
  });

  $(".edit-literal").click(function() {
    doWrap("\n{literal}", "{/literal}\n", "Literal layout");
  });

  $(".edit-link").click(function() {
    $("#linkDialog-url").val("");
    $("#linkDialog-displayText").val($("#post-content").getSelection().text);

    $("#linkDialog").dialog({
      modal: true,
      resizable: false,
      width: 350,
      title: "Insert Link"
    });
  });

  $("#edit-doLinkAdd").click(function() {
    $("#post-content").replaceSelection("[" + $("#linkDialog-url").val() + " " + $("#linkDialog-displayText").val() + "]");
    updatePreview();

    $("#linkDialog").dialog("destroy");
  });

  $(".edit-image").click(function() {
    $("#imageDialog-url").val("");
    $("#imageDialog-altText").val($("#post-content").getSelection().text);
    $("#imageDialog-caption").val($("#post-content").getSelection().text);

    $("#imageDialog").dialog({
      modal: true,
      resizable: false,
      width: 350,
      title: "Insert Image"
    });
  });

  $("#edit-doImageAdd").click(function() {
    imageText= "[[Image:" + $("#imageDialog-url").val();

    if ($.trim($("#imageDialog-altText").val()).length > 0) {
      imageText+= "|alt=" +$("#imageDialog-altText").val();
    }

    if($.trim($("#imageDialog-caption").val()).length > 0) {
      imageText+= "|thumb|" + $.trim($("#imageDialog-caption").val());
    }

    imageText+= "]]"

    $("#post-content").replaceSelection(imageText);
    updatePreview();

    $("#imageDialog").dialog("destroy");
  });

  $(".edit-quote").click(function() {
    $("#quoteDialog-url").val("");
    $("#quoteDialog-author").val("");
    $("#quoteDialog-quote").val($("#post-content").getSelection().text);

    $("#quoteDialog").dialog({
      modal: true,
      resizable: false,
      width: 350,
      title: "Insert Quote"
    });
  });

  $("#edit-doQuoteAdd").click(function() {
    insertQuote($("#quoteDialog-url").val(), $("#quoteDialog-caption").val(), $("#quoteDialog-quote").val());

    $("#quoteDialog").dialog("destroy");
  });
});

function doWrap(startText, endText, defaultText) {
  range= $("#post-content").getSelection();

  var text;
  if (range.length == 0) {
    text= defaultText;
  } else {
    text= range.text;
  }

  $("#post-content").replaceSelection(startText + text + endText);
  updatePreview();
}

function updatePreview() {
  $.post("/nk4um/post/preview", { post : $("#post-content").val() }, function(data) {
    $("#contentPreview").html(data);
  }, "html");
}

function insertQuote(url, caption, quote) {
  quoteString= "\n<div class=\"reference\">\n" +
               "<div class=\"reference-author\">\n" +
               "" + caption + " ([" + url + " View])\n" +
               "</div>\n" +
               "<div class=\"reference-quote\">\n" +
                    quote + "\n" +
               "</div>\n" +
               "</div>\n";

  $("#post-content").replaceSelection(quoteString);
  updatePreview();
}