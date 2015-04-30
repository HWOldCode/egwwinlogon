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