
/**
 * ELogin - Egroupware
 *
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
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
*/

/**
 * UI for ELogin
 *
 * @augments AppJS
 */

if( typeof app != 'undefined' ) {
    app.classes.elogin = AppJS.extend(
    {
        /**
         * application name
         */
        appname: 'elogin',

        /**
         * et2 widget container
         */
        et2: null,

        /**
         * Constructor
         *
         * @memberOf app.eworkflow
         */
        init: function() {
            // call parent
            this._super.apply(this, arguments);
        },

        /**
         * Destructor
         */
        destroy: function() {
            delete this.et2_obj;

            // call parent
            this._super.apply(this, arguments);
        },

        /**
         * This function is called when the etemplate2 object is loaded
         * and ready.  If you must store a reference to the et2 object,
         * make sure to clean it up in destroy().
         *
         * @param et2 etemplate2 Newly ready object
         */
        et2_ready: function(et2, menuaction) {
            this.et2_obj = et2;
        },

        /**
         * _openEgwWindow
         *
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
        },

        /**
         * elogin_machine_list_actions
         */
        elogin_machine_list_actions: function(_action, _senders) {
            var self = this;

			switch( _action.id ) {
                case 'settinglist':
                    for( var i=0; i<_senders.length; i++) {
                        var id = _senders[i].id;
                        var idparts = id.split('::');

                        var url = window.egw_webserverUrl +
                            '/index.php?menuaction=' +
                            'elogin.elogin_machine_ui.settings&machineid=' +
                            idparts[1];

						this._openEgwWindow(url, function(){
                            var nm = self.et2_obj.getWidgetById('nm');
                            nm.dataview.updateColumns();
                        }, 'elogin_setting');
                    }

                    break;
            }
        }
    });
}

