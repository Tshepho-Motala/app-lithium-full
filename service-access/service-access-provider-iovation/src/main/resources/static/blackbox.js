function loadBlackbox() {
    var globalObjectName = getGlobalObjectName();

    var config = window[globalObjectName];

    constructBlackbox(config);
}

function makeid(length) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
        result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }
    return result;
}

function getGlobalObjectName() {
    if (window.io_global_object_name) {
        return window.io_global_object_name;
    } else {
        return "IGLOO";
    }
}

function constructBlackbox(config) {
    //Defined in client script, element to populate with BB value
    if (config.bbout_element_id) {
        var element = window.document.getElementById(config.bbout_element_id);
        if (element) {
            element.value = makeid(20);
        }
    }
    config.bb_callback(makeid(20), false);

    if (config.bb_callback) {
        setInterval(function() {
            //call callback function with a complete flag true
            config.bb_callback(makeid(20), true);
        }, 20000);

    } else {
        alert("broken");
    }
}

function getBlackbox() {
    var bb = {};
    bb.blackbox = makeid(20);
    bb.finished = true;
    console.log(bb);
    return bb;
}

window.onpageshow = function (ev) {
    loadBlackbox();
}
