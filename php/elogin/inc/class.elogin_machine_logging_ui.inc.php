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
     * elogin_machine_logging_ui
     */
    class elogin_machine_logging_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'logging_list'          => true,
            'get_rows_logging'      => true,
            'ajax_logging'          => true,
            );

        /**
         * logging_list
         * @param array $content
         */
        public function logging_list($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys		= array();
			$sel_options	= array();

			// -----------------------------------------------------------------

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
						$msg .= lang('%1 Log(s) %2', $success, $action_msg);
					}
					elseif( empty($msg) ) {
						$msg .= lang(
							'%1 Log(s) %2, %3 failed because of insufficent rights !!!',
							$success,
							$action_msg,
							$failed);
					}
				}
			}

			// -----------------------------------------------------------------

			if( !isset($sel_options['el_machine_name_filter']) ) {
				$sel_options['el_machine_name_filter'] = array();

				$m_query		= array();
				$m_rows			= array();
				$m_readonlys	= array();

				elogin_machine_bo::get_rows($m_query, $m_rows, $m_readonlys);

				foreach( $m_rows as $m_row ) {
					$sel_options['el_machine_name_filter'][$m_row['el_unid']] = $m_row['el_name'];
				}
			}

			if( !isset($sel_options['el_username_filter']) ) {
				$sel_options['el_username_filter'] = array();

				$taccount = new Api\Accounts();
				$accounts = $taccount->search(array('type' => 'accounts'));

				foreach( $accounts as $account ) {
					$sel_options['el_username_filter'][$account['account_id']] = $account['account_lid'];
				}
			}

			// -----------------------------------------------------------------

			$content['msg'] = $msg;

			if( !($content['nm'] = Api\Cache::getSession('elogin_machine_logging_list', 'elogin')) ) {
				$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
					'get_rows'      =>	'elogin.elogin_machine_logging_ui.get_rows_logging',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
					'no_filter'     => true,// I  disable the 1. filter
					'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
					'no_cat'        => false,// I  disable the cat-selectbox
					'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
					'row_id'        => 'el_unid',
					'actions'       => static::index_get_actions(),
					'header_row'    => 'elogin.machine_logging_list.header_right',
					'favorites'     => false
					);
			}

            $tpl = new Etemplate('elogin.machine_logging_list');
			$tpl->exec(
                'elogin.elogin_machine_logging_ui.logging_list',
                $content,
                $sel_options,
                $readonlys);
        }

        /**
         * index_get_actions
         * @param array $query
         * @return array
         */
        static public function index_get_actions($query=array()) {
            $group = 0;

			$actions = array(
				'delete' => array(
					'caption'			=> 'Delete',
                    'group'				=> ++$group,
                    'default'			=> false,
                    'icon'				=> 'delete',
                    'hint'				=> 'Delete Log',
					'confirm'			=> 'Delete this Log',
					'confirm_multiple'	=> 'Delete these Logs',
                    'enabled'			=> true,
					),
				);

            return $actions;
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
				case 'delete':
					if( is_array($checked) ) {
						foreach( $checked as $checkid ) {
							$log = new elogin_machine_logging_bo($checkid);
							$log->delete();

							$success++;
						}
					}
					break;
			}
		}

		/**
		 * get_rows_logging
		 * @param type $query
		 * @param type $rows
		 * @param type $readonlys
		 * @return type
		 */
        public function get_rows_logging(&$query, &$rows, &$readonlys) {
            Api\Cache::setSession('elogin_machine_logging_list', 'elogin', $query);

			if( !isset($query['col_filter']) ) {
				$query['colfilter'] = array();
			}

			$colfilter = array();

			if( isset($query['col_filter']['el_machine_name_filter']) ) {
				if( $query['col_filter']['el_machine_name_filter'] != '' ) {
					$colfilter[] = elogin_machine_logging_bo::expression(
						elogin_machine_logging_bo::TABLE,
						array(
							'el_machine_id' => $query['col_filter']['el_machine_name_filter'],
						));
				}
			}

			if( isset($query['col_filter']['el_username_filter']) ) {
				if( $query['col_filter']['el_username_filter'] != '' ) {
					$colfilter[] = elogin_machine_logging_bo::expression(
						elogin_machine_logging_bo::TABLE,
						array(
							'el_account_id' => $query['col_filter']['el_username_filter'],
						));
				}
			}

			$query['col_filter'] = $colfilter;

			if( isset($query['order']) ) {
				$order = 'el_logdate';

				if( $query['order'] == 'el_date_sort') {
					$order = 'el_logdate';
				}

				$query['order'] = $order;
			}

            $count = elogin_machine_logging_bo::get_rows($query, $rows, $readonlys);

            foreach( $rows as &$row ) {
                $logging = new elogin_machine_logging_bo($row['el_unid']);
                $machine = new elogin_machine_bo($row['el_machine_id']);

                $row['icon']            = 'logging.png';
                $row['el_machine_name'] = $machine->getName();
                $row['el_username']     = $logging->getAccountName();
            }

            return $count;
        }

        /**
         * ajax_logging
         * @param array $content
         */
        public function ajax_logging($content=array()) {
            //error_log(__METHOD__.__LINE__.':'.  var_export($content, true));

            if( isset($content['uid']) ) {
                $machine = new elogin_machine_bo($content['uid']);

                if( $machine->getIsInDb() ) {
                    if( isset($content['loggings']) && is_array($content['loggings']) ) {
                        foreach( $content['loggings'] as $tlog ) {
                            $log        = $machine->createNewLogging();
                            $event      = '';
                            $msg        = '';
                            $logdate    = 0;
                            $index      = '';

                            if( isset($tlog['event']) ) {
                                $event = $tlog['event'];
                            }

                            if( isset($tlog['message']) ) {
                                $msg = base64_decode($tlog['message']);
                            }

                            if( isset($tlog['logdate']) ) {
                                $logdate = intval($tlog['logdate']);
                                //error_log(__METHOD__.__LINE__.':'.  var_export($logdate, true));
                            }

                            if( isset($tlog['index']) ) {
                                $index = $tlog['index'];
                            }

                            $log->setEvent($event);
                            $log->setMessage($msg);
                            $log->setLogDate($logdate);
                            $log->setIndex($index);

                            $log->save();
                        }

                    }

                    return Api\Json\Response::get()->data(
						array('status' => 'ok'));
                }
            }

            return Api\Json\Response::get()->data(
				array('status' => 'error'));
        }
    }