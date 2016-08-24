/*jslint browser: true*/
/*global $, jQuery, alert, console, xhr, createBloodhound*/
var SERVER_URL = 'http://54.164.112.121:4567';

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

  var playerNames1 = new Bloodhound({
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
      url: SERVER_URL + "/Suggest",
      prepare: function (query, settings) {
        settings.url += "?baseStr=" + query;
        return settings;
      }
    }
  });

  playerNames1.initialize();
  var adapter = playerNames1.ttAdapter();
  $('#firstplayerinput').typeahead({
    minLength: 1,
    hint: false
  }, {
    source: adapter,
    limit: 6,
    templates: {
      suggestion: function (data) {
        return "<li class='list-unstyled text-success'>" + data + "</li>";
      }
    }
  });

  $('#secondplayerinput').typeahead({
    minLength: 1,
    hint: false
  }, {
    source: adapter,
    limit: 6,
    templates: {
      suggestion: function (data) {
        return "<li class='list-unstyled text-success'>" + data + "</li>";
      }
    }
  });

});

$('#calculatebutton').on('click', function (e) {
  var $this = $(this),
    $form = $('#playerinputform');

  $this.button('loading');

  e.preventDefault();

  //ping server for path
  var queryParams = {
    p1: $("#firstplayerinput").val(),
    p2: $("#secondplayerinput").val(),
  };

  $.get(SERVER_URL + "/DegreeOfSeparation", queryParams, onReceivePath).fail(function () {
    "use strict";

    // set result panel title
    $("#resultheader").html($("#firstplayerinput").val() + " To " + $("#secondplayerinput").val());

    $("#pathtext").html("Error: One or more invalid players requested");

    $('#resultpanel').collapse('show');
    $('#calculatebutton').button('reset');
  });

  //arbitrary wait time to close text
  setTimeout(function () {
    $('#resultpanel').collapse('hide');
    $('#errorpanel').collapse('hide');
  }, 60000);
});
