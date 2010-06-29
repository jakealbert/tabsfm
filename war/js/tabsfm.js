$(function(){
	$('select').selectToUISlider({labels: 0 });
	$('div.ui-slider').before('<div class="span-16 prepend-1 last" id="calslider"></div>');
	$('div#calslider').append($('div.ui-slider')).height('70px');
	$('div#calslider').position('absolute');
	$('div#calslider').append(
				  $('<span class="sensor">'+$('select :selected').text()+'</span>'));
	$('select').width($('.sensor').outerWidth()+28);
	$('.sensor').remove();
	$('select').change(
			  function() {
			      $('div#calslider').append(
							$('<span class="sensor">'+$('select :selected').text()+'</span>'));
			      $('select').width($('.sensor').outerWidth()+28);
			      $('.sensor').remove();
			  });});