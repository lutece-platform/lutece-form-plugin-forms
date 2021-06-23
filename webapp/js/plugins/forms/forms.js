$( function(){
    /* Check if enter key is pressed and force submit */
    $("#form-validate").on("keypress", "input", function(e){
        if(e.which == 13){
            e.preventDefault();
            $('button[name="action_doSaveStep"]').click();
        }
    });
});