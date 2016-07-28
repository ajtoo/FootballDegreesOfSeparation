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

  //  xhr('GET', 'http://localhost:4567/AllPlayers')
  //    .success(function (data) {
  //      
  //    });

  $.ajax({
    url: 'http://localhost:4567/AllPlayers',
    success: function (data) {
      var nameArray = JSON.parse(data);
      var playerNames = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
        queryTokenizer: queryTokenizer,
        local: $.map(nameArray, function (name) {
          // Normalize the name - use this for searching
          var normalized = normalize(name);
          return {
            value: normalized,
            // Include the original name - use this for display purposes
            displayValue: name
          };
        })
      });

      playerNames.initialize();
      $('#firstplayerinput').typeahead({
        minLength: 1,
        hint: false
      }, {
        displayKey: 'displayValue',
        source: playerNames.ttAdapter(),
        limit: 6,
        templates: {
          suggestion: function (data) {
            return "<li class='list-unstyled text-success'>" + data.displayValue + "</li>";
          }
        }
      });
      $('#secondplayerinput').typeahead({
        minLength: 1,
        hint: false
      }, {
        displayKey: 'displayValue',
        source: playerNames.ttAdapter(),
        limit: 6,
        templates: {
          suggestion: function (data) {
            return "<li class='list-unstyled text-success'>" + data.displayValue + "</li>";
          }
        }
      });

    },
    type: 'GET'
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
