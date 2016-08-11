/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id:$
 */

/*egw:uses
    jquery.jquery;
    jquery.jquery-ui;
	et2_core_valueWidget;
    et2_core_inputWidget;
	phpgwapi.Resumable.resumable;
 */

/**
 * et2_elogin_link_open
 * @type Function|@call;call
 */
var et2_elogin_link_open = (function(){ "use strict"; return et2_inputWidget.extend({

	/**
	 * attributes
	 */
    attributes: {

	},

	/**
	 * init
	 * @param _parent
	 * @param _attrs
	 */
	init: function(_parent, _attrs) {
		
	}
});}).call(this);

/**
 * register
 */
et2_register_widget(et2_elogin_link_open, ["elogin-link-open"]);