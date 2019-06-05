if (document.location.protocol == 'file:') {

	Ext.override(Ext.data.Connection, {
		parseStatus: function() {
			return {
				success: true,
				isException: false
			};
		}
	});

}