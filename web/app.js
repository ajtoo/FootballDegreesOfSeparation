/*jslint browser: true*/
/*global $, jQuery, alert, console*/

var onReceivePath = function (data) {
  "use strict";

  // set result panel title
  $("#resultheader").html($("#firstplayerinput").val() + " To " + $("#secondplayerinput").val());

  $("#pathtext").html(data);

  $('#calculatebutton').button('reset');
  $('#resultpanel').collapse('show');
};

var onServerBusy = function () {
  "use strict";
  $('#calculatebutton').button('reset');
  $('#errorpanel').collapse('show');
};

$(document).ready(function () {
  "use strict";

  $("#firstplayerinput").typeahead({
    source: ["asdf", "ahgh", "aee", "avc", "qwerty", "zxcv"]
  });

  $('#calculatebutton').on('click', function (e) {
    var $this = $(this),
      $form = $('#playerinputform');

    $('#resultpanel').collapse('hide');
    $('#errorpanel').collapse('hide');

    $this.button('loading');

    e.preventDefault();

    setTimeout(function () {
      onReceivePath("path text");
      //onServerBusy();
    }, 2000);

  });

});
