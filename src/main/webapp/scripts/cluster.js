var CLUSTER_TEST = {
	version: '1.0',
	url: 'test',
	info: {},
	tests: [],
	tests_tree: {},
	tests_order: [],
	
	add: function(options) {
		var set_id = options.set_id;
		
		this.tests_order.push(set_id);
		this.tests_tree[set_id] = {};
		for( var i = 0; i < options.objects_number; i++ ) {
			var obj_id = (options.starting_object + i).toString();

			var values = {
				obj_id: obj_id,
				set_id: set_id,
				
				cluster_mode: options.cluster_mode,
				time_sec: options.time_sec,
				time_millis: options.time_sec*1000,
				delay_min: options.delay_min,
				size_mb: options.size_mb,
				run_mode: options.run_mode
			};
			
			this.tests.push(values);
			this.tests_tree[set_id][obj_id] = values;
		}
	},
	
	delay: function(set_id, obj_id) {
		return this.tests_tree[set_id][obj_id].delay_min;
	},
	
	run: function(set_id, obj_id) {
		var values = this.tests_tree[set_id][obj_id];

		return $.ajax({
			url: this.url,
			dataType: 'json',
			data: {
				set: set_id,
				object: obj_id,
				millis: values.time_millis,
				mbytes: values.size_mb,
				run_mode: values.run_mode,
				cluster_mode: values.cluster_mode
			}
		});
	}

};