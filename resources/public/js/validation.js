function validateForm() {
    var id = document.forms["qForm"]["user-id"].value;
    if (id == null || id == "" || !(isNaN(id))) {
        alert("Please enter valid user id.");
        return false;
    }
    var text = document.forms["qForm"]["text"].value;
    if (text == null || text == "") {
        alert("Please enter question text.");
        return false;
    }
}
