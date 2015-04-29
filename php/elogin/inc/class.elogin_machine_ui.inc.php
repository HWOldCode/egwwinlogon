<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

    /**
     * elogin_machine_ui
     */
    class elogin_machine_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'machine_list'          => true,
            'get_rows_machine'      => true,
            );

        /**
         * share_provider_list
         *
         * @param array $content
         */
        public function machine_list($content) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_machine_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_machine_ui.get_rows_machine',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'el_unid',
						'actions'       => self::index_get_actions(),
                        'header_row'    => 'elogin.machine_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new etemplate_new('elogin.machine_list');
			$tpl->exec(
                'elogin.elogin_machine_ui.machine_list',
                $content,
                array(),
                $readonlys,
                array(),
                0);
        }

        /**
         * index_get_actions
         *
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
            $group = 1;

            return array();
        }

        /**
         * get_rows_machine
         *
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        public function get_rows_machine(&$query, &$rows, &$readonlys) {
            egw_session::appsession('elogin_machine_list', 'elogin', $query);

            $count = elogin_machine_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$row ) {
                $row['icon']            = 'machine.png';
                $row['el_machine_name'] = $row['el_name'];
            }

            return $count;
        }
    }