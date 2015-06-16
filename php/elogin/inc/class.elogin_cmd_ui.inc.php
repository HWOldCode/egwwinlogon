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
            'cmd_edit'          => true,
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

        /**
         * cmd_edit
         *
         * @param array $content
         */
        public function cmd_edit($content=null) {
            if( !$GLOBALS['egw_info']['user']['apps']['admin'] ) {
                die("Only for Admins!");
            }

            if( $content == null ) {
                $content = array();
            }

            $preserv    = array();
            $option_sel = array();
            $readonlys  = array();

            $uid = ( isset($content['uid']) ? $content['uid'] : null);
			$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

            $muid = ( isset($content['muid']) ? $content['muid'] : null);
			$muid = ( $muid == null ? (isset($_GET['muid']) ? $_GET['muid'] : null) : $muid);

            $cmd = null;

            if( $uid != null ) {
                $cmd = new elogin_cmd_bo($uid);
            }

            if( isset($content['button']) && isset($content['button']['save']) ) {
                if( $cmd == null ) {
                    $cmd = new elogin_cmd_bo();
                }

                $cmd->setCommand($content['command']);
                $cmd->setAccountId($content['account']);
                $cmd->setMachineId($content['machine']);
                $cmd->setOrder(intval($content['order']));
                $cmd->setSystem($content['system']);
                $cmd->setType($content['type']);
                $cmd->setEvent($content['event']);
                $cmd->save();
            }

            if( $cmd != null ) {
                $content['command'] = $cmd->getCommand();
                $content['machine'] = $cmd->getMachineId();
                $content['account'] = $cmd->getAccountId();
                $content['system']  = $cmd->getSystem();
                $content['order']   = $cmd->getOrder();
                $content['type']    = $cmd->getType();
                $content['event']   = $cmd->getEvent();
                $preserv['uid']     = $cmd->getId();
            }

            // machine ---------------------------------------------------------
            $option_sel['machine'] = array(
                'all' => lang('Global (All machine)'),
                );

            if( $muid != null ) {
                $preserv['muid'] = $muid;

                $machine = new elogin_machine_bo($muid);

                if( $machine->getIsInDb() ) {
                    $option_sel['machine'][$machine->getId()] = $machine->getName();
                }
            }

            // system ----------------------------------------------------------
            $option_sel['system'] = array(
                elogin_bo::SYSTEM_WIN => lang('Windows 7+')
                );

            // order -----------------------------------------------------------
            $option_sel['order'] = array(
                '0' => '0',
                '1' => '1',
                '2' => '2'
                );

            // type ------------------------------------------------------------
            $option_sel['type'] = array(
                elogin_cmd_bo::TYPE_USER    => lang('As User'),
                elogin_cmd_bo::TYPE_SERVICE => lang('As System'),
                );

            // event -----------------------------------------------------------
            $option_sel['event'] = array(
                elogin_cmd_bo::EVENT_LOGIN_PRE      => lang('Login Pre (password accept)'),
                elogin_cmd_bo::EVENT_LOGIN          => lang('Login (System session create)'),
                elogin_cmd_bo::EVENT_LOGIN_AFTER    => lang('Login After (Destop is show)'),
                );


            $tpl = new etemplate_new('elogin.cmd.dialog');
			$tpl->exec(
                'elogin.elogin_cmd_ui.cmd_edit',
                array_merge($content, $preserv),
                $option_sel,
                $readonlys,
                $preserv,
                2);
        }

        /**
         * get_rows_commands
         *
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         */
        public function get_rows_commands(&$query, &$rows, &$readonlys) {
            if( !is_array($query) && !isset($query['unid']) ) {
                return;
            }

            $label_system = array(
                elogin_bo::SYSTEM_WIN => lang('Windows 7+')
                );

            $label_type = array(
                elogin_cmd_bo::TYPE_USER    => lang('As User'),
                elogin_cmd_bo::TYPE_SERVICE => lang('As System'),
                );

            $label_event = array(
                elogin_cmd_bo::EVENT_LOGIN_PRE      => lang('Login Pre (password accept)'),
                elogin_cmd_bo::EVENT_LOGIN          => lang('Login (System session create)'),
                elogin_cmd_bo::EVENT_LOGIN_AFTER    => lang('Login After (Destop is show)'),
                );

            $machine = new elogin_machine_bo($query['unid']);

            if( $machine->getIsInDb() ) {
                $cmds = $machine->getCmds();

                foreach( $cmds as $tcmd ) {
                    if( $tcmd instanceof elogin_cmd_bo ) {
                        $machinename = 'all';

                        if( $tcmd->getMachineId() != 'all' ) {
                            $machinename = $machine->getName();
                        }

                        $accountname = 'all';

                        if( $tcmd->getAccountId() != '0' ) {
                            $accountname = accounts::id2name($tcmd->getAccountId());
                        }

                        $rows[] = array(
                            'el_unid'   => $tcmd->getId(),
                            'machine'   => $machinename,
                            'account'   => $accountname,
                            'command'   => $tcmd->getCommand(),
                            'system'    => $label_system[$tcmd->getSystem()],
                            'order'     => $tcmd->getOrder(),
                            'type'      => $label_type[$tcmd->getType()],
                            'event'     => $label_event[$tcmd->getEvent()],
                            );
                    }
                }
            }

            return count($rows);
        }
    }