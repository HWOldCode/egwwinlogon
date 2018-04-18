/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
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
		"options": {
			"name": "Options",
			"type": "any",
			"description": ""
		},
		"value": {
			"name": "Value",
			"type": "any",
			"description": ""
		}
	},

	/**
	 * init
	 * @param _parent
	 * @param _attrs
	 */
	init: function(_parent, _attrs) {
		this._super.apply(this, arguments);

		this.node = jQuery(document.createElement("span"));

        this.setDOMNode(this.node[0]);
		//<a href="egwwinlogon://<dialog>Hallo Welt!">Test 1</a>
		//<a href="egwwinlogon://<explorer>C:\windows\system32">Test 2</a>
		//<a href="egwwinlogon://<explorer-select>C:\windows\system32\cmd.exe">Test 3</a>
	},

	/**
     * doLoadingFinished
     * @returns {undefined}
     */
    doLoadingFinished: function() {
		this._super.apply(this, arguments);

		var node = jQuery(this.node);
		jQuery('<iframe id="openUriWidget" src="egwwinlogon://<explorer>' +
			this.options.value + '">').appendTo(node).ready(function() {
				setTimeout(function() {
					window.close();
				}, 50);
			});
	}
});}).call(this);

/**
 * register
 */
et2_register_widget(et2_elogin_link_open, ["elogin-link-open"]);