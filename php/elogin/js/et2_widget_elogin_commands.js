/**
 * ELogin - Egroupware
 * @link http://www.hw-softwareentwicklung.de
 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @package elogin
 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
 * @version $Id:$
 */

"use strict";

/*egw:uses
    jquery.jquery;
    jquery.jquery-ui;
	et2_core_valueWidget;

    // Include the grid classes
    et2_dataview;
    // Include menu system for list context menu
	egw_action.egw_menu_dhtmlx;
*/


/**
 * elogin_commands
 * UI widget for a single (read-only) link
 * @augments et2_valueWidget
 */
var elogin_commands = et2_valueWidget.extend([et2_IDataProvider], {

    attributes: {
		"value": {
			"name": "Value",
			"type": "any",
			"description": "Object {app: ..., id: ..., status-widgets: {}} where status-widgets is a map of fields to widgets used to display those fields"
		}
    },

    columns: [
      {'id': 'machine', caption: 'Machine', 'width': '100px'},
      {'id': 'account', caption: 'Account', 'width': '100px'},
      {'id': 'name', caption: 'Name', 'width': '100px'},
      {'id': 'system', caption: 'System', 'width': '100px'},
      {'id': 'order', caption: 'Order', 'width': '100px'},
      {'id': 'type', caption: 'Type', 'width': '100px'},
      {'id': 'event', caption: 'Event', 'width': 'auto'},
    ],

    FIELD: 2,

    /**
	 * init
	 * @memberOf et2_link
	 */
	init: function() {
        this._super.apply(this, arguments);

        this.div = jQuery(document.createElement("div"))
			.addClass("et2_elogin_mountlist");

		this.innerDiv = jQuery(document.createElement("div"))
			.appendTo(this.div);

        // Set up context menu
		var self = this;

        this.context = new egwMenu();
        this.context.addItem(
            "editcmd",
            this.egw().lang("Edit Command"),
            this.egw().image("edit"),
            function(menu_item) {
				app.elogin._openEgwWindow(
					egw.link(
						'/index.php',
						'menuaction=elogin.elogin_cmd_ui.cmd_edit&muid=' +
							self.options.value.unid + "&uid=" +
							self.context.data.el_unid),
					function(){
						if( typeof self.controller !== 'undefined' ) {
							self.controller.update();
						}
					},
					'_blank'
					);

				if( typeof self.controller !== 'undefined' ) {
					this.controller.update();
				}
			});

		this.context.addItem(
            "deletecmd",
            this.egw().lang("Delete Command"),
            this.egw().image("delete"),
            function(menu_item) {
				app.elogin._openEgwWindow(
					egw.link(
						'/index.php',
						'menuaction=elogin.elogin_cmd_ui.cmd_delete&muid=' +
							self.options.value.unid + "&uid=" +
							self.context.data.el_unid),
					function(){
						if( typeof self.controller !== 'undefined' ) {
							self.controller.update();
						}
					},
					'_blank'
					);
			});
    },

    /**
	 * Destroys all
	 */
	destroy: function() {
		// Unbind, if bound
		if( this.options.value && !this.options.value.unid ) {
			jQuery(window).off('.' + 'elogin' + this.options.value.unid);
		}

		// Free the widgets
		for(var i = 0; i < this.columns.length; i++) {
			if(this.columns[i].widget) this.columns[i].widget.destroy();
		}

		for(var key in this.fields) {
			this.fields[key].widget.destroy();
		}

		if(this.diff) this.diff.widget.destroy();

		// Free the grid components
		if(this.dataview) this.dataview.free();
		if(this.rowProvider) this.rowProvider.free();
		if(this.controller) this.controller.free();
		if(this.dynheight) this.dynheight.free();

		this._super.apply(this, arguments);
	},

    /**
     * doLoadingFinished
     *
     * @returns {undefined}
     */
    doLoadingFinished: function() {
		this._super.apply(this, arguments);

		// Find the tab widget, if there is one
		var tabs = this;

		do {
			tabs = tabs._parent;
		} while (tabs != this.getRoot() && tabs._type != 'tabbox');

        var roottab = this.getRoot();

		if( tabs != roottab ) {

            var tindex = 0;

			// Find the tab index
			for(var i = 0; i < tabs.tabData.length; i++) {
				// Find the tab
				if( tabs.tabData[i].contentDiv.has(this.div).length ) {
					// Bind the action to when the tab is selected
					var handler = function(e) {
						e.data.div.unbind("click.elogincommands");
						e.data.elogincommands.finishInit();
						e.data.elogincommands.dynheight.update(function(_w, _h) {
							e.data.elogincommands.dataview.resize(_w, _h);
						});
					};

					tabs.tabData[i].flagDiv.bind("click.elogincommands", {
                        "elogincommands": this,
                        div: tabs.tabData[i].flagDiv
                        }, handler);

                    tindex = i;
					break;
				}
			}

            if( tindex === tabs.selected_index ) {
                this.finishInit();
            }
		}
		else
		{
            this.finishInit();
        }
	},

    /**
	 * Finish initialization which was skipped until tab was selected
	 */
	finishInit: function() {
        // No point with no ID
		if( !this.options.value || (!this.options.value.unid) ) {
			return;
		}

        if( typeof this.options.value.unid !== "undefined") {
            this._filters = {
                userschareunid: this.options.value.unid,
                appname: 'elogin',
                get_rows: 'elogin_cmd_ui::get_rows_commands'
                };
        }

        // Create the dynheight component which dynamically scales the inner
		// container.
		this.dynheight = new et2_dynheight(
            this.div.parent(),
            this.innerDiv,
            250
            );

        // Create the outer grid container
		this.dataview = new et2_dataview(this.innerDiv, this.egw());
		var dataview_columns = [];

		for( var i = 0; i < this.columns.length; i++ ) {
			dataview_columns[i] = {"id": this.columns[i].id, "caption": this.columns[i].caption, "width":this.columns[i].width};
		}

		this.dataview.setColumns(dataview_columns);

		// Create widgets for columns that stay the same, and set up varying widgets
		this.createWidgets();

		// Create the gridview controller
		var linkCallback = function() {};

		this.controller = new et2_dataview_controller(null, this.dataview.grid,
			this, this.rowCallback, linkCallback, this,
			null
		);

		// Trigger the initial update
		this.controller.update();

		// Write something inside the column headers
		for (var i = 0; i < this.columns.length; i++) {
			jQuery(this.dataview.getHeaderContainerNode(i)).text(this.columns[i].caption);
		}

		// Register a resize callback
		var self = this;

		jQuery(window).on('resize.' + 'elogin' + this.options.value.unid, function() {
			self.dynheight.update(function(_w, _h) {
				self.dataview.resize(_w, _h);
			});
		});
    },

    createWidgets: function() {

        // Per-field widgets - new value & old value
		this.fields = {};

        // Widget for text diffs
		var diff = et2_createWidget('diff', {}, this);

		this.diff = {
			widget: diff,
			nodes: jQuery(diff.getDetachedNodes())
		};
    },

    getDOMNode: function(_sender) {
        if( _sender == this ) {
            return this.div[0];
        }

        for( var i = 0; i < this.columns.length; i++ ) {
            if( _sender == this.columns[i].widget ) {
                return this.dataview.getHeaderContainerNode(i);
            }
        }

		return null;
	},

    dataFetch: function (_queriedRange, _callback, _context) {
		// Skip getting data if there's no ID
		if(!this.value) return;

		// Pass the fetch call to the API
		this.egw().dataFetch(
			this.getInstanceManager().etemplate_exec_id,
			_queriedRange,
			this._filters,
			this.id,
			_callback,
			_context
		);
	},


	// Needed by interface
	dataRegisterUID: function (_uid, _callback, _context) {
		this.egw().dataRegisterUID(
            _uid,
            _callback,
            _context,
            this.getInstanceManager().etemplate_exec_id,
            this.id
            );
	},

    /**
     * dataUnregisterUID
     *
     * @param {type} _uid
     * @param {type} _callback
     * @param {type} _context
     * @returns {undefined}
     */
	dataUnregisterUID: function (_uid, _callback, _context) {
		// Needed by interface
	},

    /**
	 * How to tell if the row needs a diff widget or not
	 */
	_needsDiffWidget: function(columnName, value) {
		if(typeof value !== "string")
		{
			this.egw().debug("warn", "Crazy diff value", value);
			return false;
		}
		return columnName == 'note' || columnName == 'description' || (value && (value.length > 50 || value.match(/\n/g)));
	},

    /**
	 * The row callback gets called by the gridview controller whenever
	 * the actual DOM-Nodes for a node with the given data have to be
	 * created.
	 */
	rowCallback: function(_data, _row, _idx, _entry) {
        var tr = _row.getDOMNode();

		jQuery(tr).attr("valign", "top");
		jQuery(tr).addClass("row_category row");

		var row = this.dataview.rowProvider.getPrototype("default");
		var self = this;

        jQuery("div", row).each(function (i) {
            var nodes = [];

            nodes = '<span>'+_data[self.columns[i].id] + '</span>';

            jQuery(this).append(nodes);
        });


        jQuery(tr).append(row.children());

        jQuery(tr).bind("contextmenu", function(e) {
                jQuery(jQuery(tr).parent()).find('.selected').each(function () {
                    jQuery(this).removeClass("selected focused");
                });

                jQuery(tr).addClass("selected focused");

                self.context.data = _data;
                self.context.showAt(e.pageX, e.pageY, true);
                e.preventDefault();
            });

		return tr;
	},

    /**
     * show_prompt_cmd
     *
     * @param {type} _callback
     * @param {type} _title
     * @param {type} _content
     * @param {type} _buttons
     * @param {type} _egw_or_appname
     * @returns {unresolved}
     */
    show_prompt_cmd: function(_callback, _title, _content, _buttons, _egw_or_appname) {

        var callback = _callback;

		// Just pass them along, widget handles defaults & missing
		return et2_createWidget("dialog", {
			callback: function(_button_id, _value) {
				if (typeof callback == "function")
				{
					callback.call(this, _button_id, _value);
				}
			},
			title: _title||egw.lang('Input required'),
			buttons: _buttons||et2_dialog.BUTTONS_OK_CANCEL,
			value: {
				content: _content
			},
			template: egw.webserverUrl + '/elogin/templates/default/cmd.dialog.xet',
			class: "et2_prompt"
		}, et2_dialog._create_parent(_egw_or_appname));
    }
});

/**
 * register
 */
et2_register_widget(elogin_commands, ["elogin-commands"]);