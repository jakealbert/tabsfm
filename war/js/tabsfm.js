$(document).ready(function(){

	$('li.track').click(function(){
		$('div.ae-expanded').slideUp("fast").removeClass("ae-expanded");
	    });
	$('div.track-actions a').click(function(event){
		$(this).parent().siblings('div.action-expand').addClass('expanding');
		$('div.expanding').append('<div class="action-expand-triangle"></div>');
		$('div.ae-expanded:not(.expanding) div.action-expand-triangle').remove();
		$('div.ae-expanded:not(.expanding)').slideUp("fast").removeClass('ae-expanded');
		$('div.action-expand-triangle').css({"left" : ($(this).offset().left + $(this).outerWidth()/2 - $(this).parent().offset().left - 10) + "px"});
		$(this).parent().siblings('div.action-expand').slideDown("fast").addClass('ae-expanded').removeClass('expanding');

		$('div.action-expand div:not(.'+$(this).attr('link')+'):not(.action-expand-triangle)').hide();
		$('div.action-expand div.'+$(this).attr('link')).show();
		event.stopPropagation();
		event.preventDefault();
	    });









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
			  });

});
