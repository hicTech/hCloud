var CLUSTER_TEST = {
	version: '1.0',
	info: {},
	tests: [],
	
	tests_tree: {},
	
	jsValue: function(selector, bool){
		return $(selector).val();
	},
	
	jsInt: function(id_or_dom, bool){
		return parseInt(this.jsValue(id_or_dom), 10);
	},
	
	random: function(n) {
	    var text = "";
	    var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	    for( var i=0; i < n; i++ )
	        text += chars.charAt(Math.floor(Math.random() * chars.length));

	    return text;
	},
	
	view: undefined,
	render: function(selector, model, controller) {
		var template = $(selector).html();
		
		$('.render').html(template);
		
		if( !!this.view ) {
			this.view.unbind();
		}
		
		this.view = rivets.bind($('.render'), {
			model: model,
			info: this.info
		})
	},
	

	addTestSet: function(){
		var objects_number = this.jsInt("#objects_number");
		var starting_object = this.jsInt("#starting_object");

		var set_id = this.random(10);

		this.tests_tree[set_id] = {}
		for( var i = 0; i < objects_number; i++ ) {
			var obj_id = (starting_object + i).toString();

			var values = {
				set_id: set_id,
				obj_id: obj_id,
				
				cluster_mode: this.jsValue('[name="cluster_mode"]:checked'),
				time_sec: this.jsValue("#time_sec"),
				time_millis: this.jsValue("#time_sec")*1000,
				delay_min: this.jsInt("#delay_min"),
				size_mb: this.jsValue("#size_mb"),
				run_mode: this.jsValue("#run_mode"),
			};
			
			this.tests.push(values);
			this.tests_tree[set_id][values.obj_id] = values;
		}
		
		//this.render('#test-list', {tests: this.tests});
	},
	
	doTestAll: function(event) {
		$('[data-run]').trigger('click')
	},
	
	doTestSet: function(event) {
		var $elem = $(event.target);
		var set_id = $elem.closest('[data-set_id]').data('set_id');
		
		$('[data-set_id='+set_id+']').each(function(i, e) {
			$('[data-run]', this).trigger('click')
		});
	},
	
	doTestObj: function(event) {
		var $elem = $(event.target);
		
		var set_id = $elem.closest('[data-set_id]').data('set_id');
		var obj_id = $elem.closest('[data-obj_id]').data('obj_id');
		var values = this.tests_tree[set_id][obj_id];
		
		var delay = values.delay_min;
		if( delay > 0 ) {
			$('[data-state]', $elem.closest('[data-obj_id]')).html('SLEEPING');
		}

		var url = "test";
		setTimeout(function() {

			$.ajax(url, {
				data: {
					set: set_id,
					object: obj_id,
					millis: values.time_millis,
					mbytes: values.size_mb,
					run_mode: values.run_mode,
					cluster_mode: values.cluster_mode
				},
				dataType: 'json',
				context: $elem.closest('[data-obj_id]'),
				beforeSend: function() {
					$('[data-result]', this).hide().empty();
					$('[data-state]', this).html('WAITING');
				},
				success: function() {
				},
				error:function() {
				},
				complete: function(jqXHR, status) {
					var json = jqXHR.responseJSON;
					
					var $result = $('[data-result]', this);
					$result.JSONView(json, {collapsed: true});
					
					var $state = $('[data-state]', this);
					$state.html(status.toUpperCase());
					$state.off();
					$state.on('click', function() {
						$result.toggle('hidden');
					});
				}
			});
		}, delay*1000);
	},
	
	start: function() {
		$.getJSON('info').done(function(data) {
			_.extend(CLUSTER_TEST.info, data);
		});
	} 
};