$("#parseUrl").on("submit", function(e){
    e.preventDefault();
    let urls = $("#exampleFormControlTextarea1").val();
    urls = urls.split("\n").join(",");
    $("#exampleFormControlTextarea1").val(urls);
    document.getElementByld("parseUrl").submit();
})
