<?php

	/**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	/**
	 * elogin_sharemounts_ui
	 */
	class elogin_sharemounts_ui {

		/**
         * public methode
         * @var array
         */
        public $public_functions = array(
			'share_mount_list' => true,
			);

		/**
         * share_mount_list
         * @param array $content
         */
        public function share_mount_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_sharemount_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_sharemounts_ui.get_rows_shareuser',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'el_unid',
						'actions'       => self::index_get_actions(),
                        'header_row'    => 'elogin.sharemount_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new etemplate_new('elogin.sharemount_list');
			$tpl->exec(
                'elogin.elogin_sharemounts_ui.share_mount_list',
                $content,
                array(),
                $readonlys,
                array(),
                0);
        }

		/**
         * index_get_actions
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
			$group = 0;

			$actions = array(
				'edit' => array(
                    'caption'	=> 'Edit',
                    'group'		=> $group,
                    'default'	=> true,
                    'icon'		=> 'edit',
                    'hint'		=> 'Edit Mount',
                    'enabled'	=> true,
                    'url'       => 'menuaction=elogin.elogin_sharemounts_ui.share_mount_edit&uid=$id',
                    'popup'     => '600x425',//egw_link::get_registry('elogin', 'add_popup'),
                    ),
				);

			return $actions;
		}
	}