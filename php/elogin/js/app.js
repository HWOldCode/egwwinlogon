
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
        }
    });
}

