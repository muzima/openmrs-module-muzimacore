var formInstance = formInstance || { getModel: function () {
    return window.formModel;
}, getHTML: function () {
    return window.formHTML ? window.formHTML : "Could not find a valid form";
}};
var ziggyFileLoader = ziggyFileLoader || {loadAppData: function (file) {
    if (file === "entity_relationship.json") return "[]";
    return window.formJSON;
}};

var formDataRepositoryContext = formDataRepositoryContext || {
    getFormPayload: function () {
        return window.formJSON;
    }
};