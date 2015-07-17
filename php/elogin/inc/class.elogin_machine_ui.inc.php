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
            'settings'              => true,
            'get_rows_machine'      => true,
            'ajax_machine_info'     => true,
            );

        /**
         * share_provider_list
         *
         * @param array $content
         */
        public function machine_list($content=null) {
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
            $group = 2;

            $action = array(
                'loglist' => array(
                    'caption'	=> 'to Machine-logging List',
                    'group'		=> $group,
                    'default'	=> false,
                    'icon'		=> 'list',
                    'hint'		=> 'to Machine-logging List by Machine',
                    'enabled'	=> true,
                    'onExecute' => 'javaScript:app.elogin.elogin_machine_list_actions',
                    ),
                'usersharelist' => array(
                    'caption'	=> 'to User-Share List',
                    'group'		=> $group,
                    'default'	=> false,
                    'icon'		=> 'list',
                    'hint'		=> 'to User-Share List by Machine',
                    'enabled'	=> true,
                    'onExecute' => 'javaScript:app.elogin.elogin_machine_list_actions',
                ),
                'settinglist' => array(
                    'caption'	=> 'Setting',
                    'group'		=> ++$group,
                    'default'	=> false,
                    'icon'		=> 'systemsettings',
                    'hint'		=> 'Open Setting Dialog',
                    'enabled'	=> true,
                    'onExecute' => 'javaScript:app.elogin.elogin_machine_list_actions',
                    ),
                'delete' => array(
                    'caption'	=> 'Delete',
                    'group'		=> ++$group,
                    'default'	=> false,
                    'icon'		=> 'systemsettings',
                    'hint'		=> 'delete machine',
                    'enabled'	=> true,
                    'onExecute' => 'javaScript:app.elogin.elogin_machine_list_actions',
                    ),
            );

            return $action;
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

        /**
         * ajax_machine_info
         * @param array $content
         */
        public function ajax_machine_info($content=array()) {
            //error_log(__METHOD__.__LINE__.':'.  var_export($content, true));

            if( isset($content['uid']) ) {
                $machine = new elogin_machine_bo($content['uid']);

                if( isset($content['name']) ) {
                    $machine->setName($content['name']);
                }

                if( !$machine->getIsInDb() ) {
                    $machine->save();
                }

                // TODO
                // login logging
            }

            return egw_json_response::get()->data(array('status' => 'ok'));
        }

        /**
         * settings
         * @param array $contentsettings
         * @param array $content
         */
        public function settings($content=array()) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            if( $content == null ) {
                $content = array();
            }

            $preserv    = array();
            $option_sel = array();
            $readonlys  = array();

            $muid = ( isset($content['machineid']) ? $content['machineid'] : null);
			$muid = ( $muid == null ? (isset($_GET['machineid']) ? $_GET['machineid'] : null) : $muid);

            if( $muid == null ) {
                die("Unknow Machine id");
            }

            $machine = new elogin_machine_bo($muid);

            if( !$machine->getIsInDb() ) {
                die("Unknow Machine");
            }

            $content['muid']            = $machine->getId();
            $content['machine_name']    = $machine->getName();
            $content['pcname']          = $machine->getName();

            $content['commands']       = array(
                'unid' => $muid
                );
            /*
             * TODO

            $content['mountlist']       = array(
                'unid' => $uid
                );
            */

            $tpl = new etemplate_new('elogin.machine_setting.dialog');
			$tpl->exec(
                'elogin.elogin_machine_ui.settings',
                $content,
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }
    }