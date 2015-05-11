
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
    jquery.jquery;
    jquery.jquery-ui;
	et2_core_valueWidget;

    // Include the grid classes
    et2_dataview;
    // Include menu system for list context menu
	egw_action.egw_menu_dhtmlx;
*/


/**
 * UI widget for a single (read-only) link
 *
 * @augments et2_valueWidget
 */
var elogin_mountlist = et2_valueWidget.extend([et2_IDataProvider], {

    attributes: {
		"value": {
			"name": "Value",
			"type": "any",
			"description": "Object {app: ..., id: ..., status-widgets: {}} where status-widgets is a map of fields to widgets used to display those fields"
		}
    },

    columns: [
      {'id': 'el_mount_name', caption: 'Mountname', 'width': '100px'},
      {'id': 'el_share_source', caption: 'Share', 'width': 'auto'},
    ],

    FIELD: 2,

    /**
	 * Constructor
	 *
	 * @memberOf et2_link
	 */
	init: function() {
        this._super.apply(this, arguments);

        this.div = $j(document.createElement("div"))
			.addClass("et2_elogin_mountlist");

		this.innerDiv = $j(document.createElement("div"))
			.appendTo(this.div);

        // Set up context menu
		var self = this;


        var menu_mount_dialog = function(menu_item) {

            var content = {
                id: self.options.value.unid,
                action: 'add'
            };

            var title = self.egw().lang("Add Mount");

            if( menu_item.id === 'editmount' ) {
                if( typeof self.context.data !== 'undefined' ) {
                    title = self.egw().lang("Edit Mount");
                    content = {
                        mountid: self.context.data.el_unid,
                        id: self.options.value.unid,
                        mount_name: self.context.data.el_mount_name,
                        action: 'edit'
                    };
                }
            }
            else if( menu_item.id === 'deleteparam' ) {
                content = {
                    mountid: self.options.value.unid,
					id: self.context.data.id,
                    action: 'delete'
                };
            }

            self.show_prompt_mount(
				function(button, _value) {
                    if(button != et2_dialog.OK_BUTTON) return;

                    _value = $j.extend({}, content, _value);

                    egw.jsonq("elogin.elogin_usershares_ui.ajax_usershare_mount",
						[_value],
						function() {
                            self.controller.update();
						},
						this, true
					);
                },
                title,
                content
                );
        };

        this.context = new egwMenu();

        this.context.addItem(
            "editmount",
            this.egw().lang("Edit Mount"),
            this.egw().image("edit"),
            menu_mount_dialog
            );
    },

    /**
	 * Destroys all
	 */
	destroy: function() {
		// Unbind, if bound
		if( this.options.value && !this.options.value.unid ) {
			$j(window).off('.' + 'elogin' + this.options.value.unid);
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
						e.data.div.unbind("click.eloginmountlist");
						e.data.eloginmountlist.finishInit();
						e.data.eloginmountlist.dynheight.update(function(_w, _h) {
							e.data.eloginmountlist.dataview.resize(_w, _h);
						});
					};

					tabs.tabData[i].flagDiv.bind("click.eloginmountlist", {
                        "eloginmountlist": this,
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
                get_rows: 'elogin_usershares_ui::get_rows_shareuser_mount'
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
			$j(this.dataview.getHeaderContainerNode(i)).text(this.columns[i].caption);
		}

		// Register a resize callback
		var self = this;

		$j(window).on('resize.' + 'elogin' + this.options.value.unid, function() {
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

        $j("div", row).each(function (i) {
            var nodes = [];

            nodes = '<span>'+_data[self.columns[i].id] + '</span>';

            $j(this).append(nodes);
        });


        $j(tr).append(row.children());

        $j(tr).bind("contextmenu", function(e) {

                $j($j(tr).parent()).find('.selected').each(function () {
                    $j(this).removeClass("selected focused");
                });

                $j(tr).addClass("selected focused");

                self.context.data = _data;
                self.context.showAt(e.pageX, e.pageY, true);
                e.preventDefault();
            });

		return tr;
	},

    show_prompt_mount: function(_callback, _title, _content, _buttons, _egw_or_appname) {

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
			template: egw.webserverUrl + '/elogin/templates/default/prompt.usershare_mount.xet',
			class: "et2_prompt"
		}, et2_dialog._create_parent(_egw_or_appname));
    }
});

/**
 * register
 */
et2_register_widget(elogin_mountlist, ["elogin-mountlist"]);