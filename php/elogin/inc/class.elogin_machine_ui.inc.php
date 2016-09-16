<?php

    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-16 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
	 */

	use EGroupware\Api;
	use EGroupware\Api\Etemplate;

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
         * machine_list
         * @param array $content
         */
        public function machine_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

			$msg = '';

			if( $content['nm']['action'] ) {
				if( !count($content['nm']['selected']) && !$content['nm']['select_all'] ) {
					$msg = lang('You need to select some entries first!');
				}
				else {
					$success	= null;
					$failed		= null;
					$action_msg = null;

					if( $this->_action(
						$content['nm']['action'],
						$content['nm']['selected'],
						$content['nm']['select_all'],
						$success,
						$failed,
						$action_msg,
						'index',
						$msg) )
					{
						$msg .= lang('%1 machine(s) %2', $success, $action_msg);
					}
					elseif( empty($msg) ) {
						$msg .= lang(
							'%1 machine(s) %2, %3 failed because of insufficent rights !!!',
							$success,
							$action_msg,
							$failed);
					}
				}
			}

            $readonlys = array();

			$content = array(
				'nm' => Api\Cache::getSession('elogin_machine_list', 'elogin'),
				'msg' => $msg,
				);

			if( !($content['nm'] = Api\Cache::getSession('elogin_machine_list', 'elogin')) ) {
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

            $tpl = new Etemplate('elogin.machine_list');
			$tpl->exec(
                'elogin.elogin_machine_ui.machine_list',
                $content,
                array(),
                $readonlys,
                array(),
                0);
        }

		/**
		 * _action
		 * @param type $action
		 * @param type $checked
		 * @param type $use_all
		 * @param int $success
		 * @param int $failed
		 * @param type $action_msg
		 * @param type $session_name
		 * @param type $msg
		 * @return type
		 */
		protected function _action($action, $checked,
			$use_all, &$success, &$failed, &$action_msg, $session_name, &$msg)
		{
			$success	= 0;
			$failed		= 0;

			switch( $action ) {
				case 'settinglist':
					$action_msg = lang('open setting');

					if( is_array($checked) ) {
						$checked = $checked[0];
					}

					$ma = new elogin_machine_bo($checked);

					if( $ma->getIsInDb() ) {
						Api\Framework::popup(
							Api\Egw::link('/index.php', 'menuaction=' .
                                'elogin.elogin_machine_ui.settings&machineid=' .
                                $ma->getId()));

						$success++;
					}
					else {
						$failed++;
					}

					break;

				case 'delete':
					if( is_array($checked) ) {
						foreach( $checked as $checkid ) {
							$ma = new elogin_machine_bo($checkid);

							if( $ma->getIsInDb() ) {
								$action_msg = lang('deleted');

								$ma->delete();

								$success++;
							}
							else {
								$failed++;
							}
						}
					}
				break;
			}

			return !$failed;
		}

        /**
         * index_get_actions
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
                    ),
                'usersharelist' => array(
                    'caption'	=> 'to User-Share List',
                    'group'		=> $group,
                    'default'	=> false,
                    'icon'		=> 'list',
                    'hint'		=> 'to User-Share List by Machine',
                    'enabled'	=> true,
                ),
                'settinglist' => array(
                    'caption'	=> 'Setting',
                    'group'		=> ++$group,
                    'default'	=> false,
                    'icon'		=> 'systemsettings',
                    'hint'		=> 'Open Setting Dialog',
                    'enabled'	=> true,
                    ),
				'delete' => array(
					'caption'			=> 'Delete',
					'confirm'			=> 'Delete this entry',
					'confirm_multiple'	=> 'Delete these entries',
					'icon'				=> 'systemsettings',
                    'hint'				=> 'delete machine',
					'group'				=> ++$group,
					'disableClass'		=> 'rowNoDelete',
				),
            );

            return $action;
        }

        /**
         * get_rows_machine
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        public function get_rows_machine(&$query, &$rows, &$readonlys) {
            Api\Cache::setSession('elogin_machine_list', 'elogin', $query);

            $count = elogin_machine_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$row ) {
                $row['icon']            = 'machine.png';
                $row['el_machine_name'] = $row['el_name'];

				/*$lastlog = elogin_machine_logging_bo::getLastLogByMachineId($row['el_unid']);

				if( $lastlog !== null ) {
					$row['el_loginuser']	= $lastlog->getAccountName();
					$row['el_logindate']	= $lastlog->getLogDate();
				}*/
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

            return Api\Json\Response::get()->data(array('status' => 'ok'));
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

            $tpl = new Etemplate('elogin.machine_setting.dialog');
			$tpl->exec(
                'elogin.elogin_machine_ui.settings',
                $content,
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }
    }