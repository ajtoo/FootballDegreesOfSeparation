/*jslint browser: true*/
/*global $, jQuery, alert, console, xhr, createBloodhound*/

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

  //  $.ajax({
  //    url: 'http://localhost:4567/Suggest?baseStr=W',
  //    success: function (data) {
  //      console.log(data)
  //    },
  //    error: function (d) {
  //      console.log(d);
  //    }
  //  });

  var playerNames1 = new Bloodhound({
    queryTokenizer: Bloodhound.tokenizers.whitespace,
    datumTokenizer: Bloodhound.tokenizers.whitespace,
    remote: {
      url: 'http://54.164.112.121:4567/Suggest',
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

  $('#resultpanel').collapse('hide');
  $('#errorpanel').collapse('hide');

  $this.button('loading');

  e.preventDefault();

  setTimeout(function () {
    onReceivePath("path text");
    //onServerBusy();
  }, 2000);

});
