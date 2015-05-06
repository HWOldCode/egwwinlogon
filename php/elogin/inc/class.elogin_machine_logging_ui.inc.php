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
         *
         * @param array $content
         */
        public function logging_list($content) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            $readonlys = array();

            if( !is_array($content) ) {
                if( !($content['nm'] = egw_session::appsession('elogin_machine_logging_list', 'elogin')) ) {
					$content['nm'] = array(		// I = value set by the app, 0 = value on return / output
						'get_rows'      =>	'elogin.elogin_machine_logging_ui.get_rows_logging',	// I  method/callback to request the data for the rows eg. 'notes.bo.get_rows'
						'no_filter'     => true,// I  disable the 1. filter
						'no_filter2'    => true,// I  disable the 2. filter (params are the same as for filter)
						'no_cat'        => false,// I  disable the cat-selectbox
						//'never_hide'    => true,// I  never hide the nextmatch-line if less then maxmatch entrie
						'row_id'        => 'el_unid',
						'actions'       => self::index_get_actions(),
                        'header_row'    => 'elogin.machine_logging_list.header_right',
                        'favorites'     => false
						);
				}
			}

            $tpl = new etemplate_new('elogin.machine_logging_list');
			$tpl->exec(
                'elogin.elogin_machine_logging_ui.logging_list',
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

        public function get_rows_logging(&$query, &$rows, &$readonlys) {
            egw_session::appsession('elogin_machine_logging_list', 'elogin', $query);

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
            error_log(__METHOD__.__LINE__.':'.  var_export($content, true));

            if( isset($content['uid']) ) {
                $machine = new elogin_machine_bo($content['uid']);

                if( $machine->getIsInDb() ) {
                    if( isset($content['loggings']) && is_array($content['loggings']) ) {
                        foreach( $content['loggings'] as $tlog ) {
                            $log        = $machine->createNewLogging();
                            $event      = '';
                            $msg        = '';
                            $logdate    = 0;

                            if( isset($tlog['event']) ) {
                                $event = $tlog['event'];
                            }

                            if( isset($tlog['message']) ) {
                                $msg = $tlog['message'];
                            }

                            if( isset($tlog['logdate']) ) {
                                $logdate = intval($tlog['logdate']);
                                //error_log(__METHOD__.__LINE__.':'.  var_export($logdate, true));
                            }

                            $log->setEvent($event);
                            $log->setMessage($msg);
                            $log->setLogDate($logdate);

                            $log->save();
                        }

                    }

                    return egw_json_response::get()->data(array('status' => 'ok'));
                }
            }

            return egw_json_response::get()->data(array('status' => 'error'));
        }
    }