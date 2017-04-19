/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id:$
 */

"use strict";

/*egw:uses
    et2_core_DOMWidget
	jquery.jquery;
    jquery.jquery-ui;
    /elogin/js/et2_widget_elogin_mountlist.js;
    /elogin/js/et2_widget_elogin_commands.js;
    /elogin/js/et2_widget_elogin_code_editor.js;
	/elogin/js/et2_widget_elogin_link_open.js;
*/

/**
 * UI for ELogin
 * @augments AppJS
 */
if( typeof app != 'undefined' ) {
    app.classes.elogin = AppJS.extend({
        /**
         * application name
         */
        appname: 'elogin',

        /**
         * et2 widget container
         */
        et2: null,

        /**
         * init
         * @memberOf app.eworkflow
         */
        init: function() {
            // call parent
            this._super.apply(this, arguments);
        },

        /**
         * destroy
         */
        destroy: function() {
            delete this.et2_obj;

            // call parent
            this._super.apply(this, arguments);
        },

        /**
		 * et2_ready
         * This function is called when the etemplate2 object is loaded
         * and ready.  If you must store a reference to the et2 object,
         * make sure to clean it up in destroy().
         * @param et2 etemplate2 Newly ready object
         */
        et2_ready: function(et2, menuaction) {
            this.et2_obj = et2;
        },

        /**
         * _openEgwWindow
         * @param string url
         * @param function onCloseFunction
         * @param string windowname
         * @returns
         */
        _openEgwWindow: function(url, onCloseFunction, windowname) {
            var self = this;

            if( typeof windowname === 'undefined' ) {
                windowname = 'elogin';
            }

            var self = this;
            var dialog = egw.openPopup(
                url,
                750,
                600,
                windowname,
                'elogin',
                true,
                'yes'
                );

            var onClose = function() {
                if( typeof onCloseFunction === 'function' ) {
                    onCloseFunction();
                }
                else {

                }
            };

            var windowcheck = function() {
                if( dialog === null ) {
                    if( onClose !== null ) {
                        onClose();
                    }

                    return;
                }

                if( dialog.closed === true ) {
                    if( onClose !== null ) {
                        onClose();
                    }

                    return;
                }

                setTimeout(windowcheck, 50);
            };

            setTimeout(windowcheck, 50);

            return dialog;
        }
    });
}

