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
     * elogin_cmd_ui
     */
    class elogin_cmd_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'ajax_cmd_list'     => true,
            );

        /**
         * ajax_machine_info
         * @param array $content
         */
        public function ajax_cmd_list($content=array()) {
            if( isset($content['uid']) ) {
                $machine = new elogin_machine_bo($content['uid']);

                if( $machine->getIsInDb() ) {
                    $usersahres = $machine->getCurrentUserShares();
                    $cmds = array();

                    foreach( $usersahres as $usersahre ) {
                        $tcmds = $usersahre->getCmds();

                        foreach( $tcmds as $tcmd ) {
                            $cmds[] = $tcmd;
                        }
                    }

                    return egw_json_response::get()->data(array(
                        'status' => 'ok',
                        'cmds' => $cmds));
                }
            }

            return egw_json_response::get()->data(array('status' => 'error'));
        }
    }