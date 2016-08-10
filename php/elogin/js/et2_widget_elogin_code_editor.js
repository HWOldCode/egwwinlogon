
    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 *
	 */

"use strict";

/*egw:uses
    jquery.jquery;
    jquery.jquery-ui;
	et2_core_valueWidget;
    et2_core_inputWidget;
	phpgwapi.Resumable.resumable;
    /elogin/js/ace/ace.js;
 */

var et2_elogin_code_editor = et2_inputWidget.extend({
    attributes: {

    },

    /**
	 * Constructor
	 *
	 * @param _parent
	 * @param _attrs
	 * @memberOf et2_htmlarea
	 */
	init: function(_parent, _attrs) {
		// _super.apply is responsible for the actual setting of the params (some magic)
		this._super.apply(this, arguments);

        this.node = jQuery(document.createElement("span"));

        this.setDOMNode(this.node[0]);
    },

    /**
     * doLoadingFinished
     * @returns {undefined}
     */
    doLoadingFinished: function() {
        this._super.apply(this, arguments);

        var node = jQuery(this.node);
        node.empty();

        this._pre = jQuery('<pre/>');
        this._pre.appendTo(node);

        this._aceeditor = ace.edit(this._pre[0]);
        this._aceeditor.setTheme("ace/theme/eclipse");
        this._aceeditor.getSession().setMode("ace/mode/batchfile");

        if( this.options.value == '' ) {
            this.options.value = 'REM New ELogin-EgwWinlogon Batchfile';
        }

        this._aceeditor.setValue(this.options.value);

        node.css({
            height: '300px'
        });

        this._pre.css({
            height: '300px'
        });
    },

    /**
     * destroy
     * @returns {undefined}
     */
    destroy: function() {
        this._super.apply(this, arguments);
    },

    /**
     * set_value
     * @param {type} _value
     * @returns {undefined}
     */
    set_value: function(_value) {
        if( _value != '' ) {
            this.options.value = _value;
        }

        if( typeof this._aceeditor !== 'undefined' ) {
            this._aceeditor.setValue(_value);
        }
    },

    /**
     * getValue
     * @returns {et2_widget_ewawi_mask_receiverAnonym$0.getValue@pro;options@pro;value|String}
     */
    getValue: function() {
        var value = '';

        if( typeof this._aceeditor !== 'undefined' ) {
            value = this._aceeditor.getValue();
        }

		return value;
    }
});

et2_register_widget(et2_elogin_code_editor, ["elogin-code-editor"]);