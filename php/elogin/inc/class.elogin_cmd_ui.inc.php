<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-15 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id$
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
			'cmd_delete'		=> true,
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
                    $cmds = $machine->getCmds();

                    foreach( $usersahres as $usersahre ) {
                        $tcmds = $usersahre->getCmds();

                        foreach( $tcmds as $tcmd ) {
                            $cmds[] = $tcmd;
                        }
                    }

                    $list = array();

                    foreach( $cmds as $tcmd ) {
                        if( $tcmd instanceof elogin_cmd_bo ) {
                            $list[] = $tcmd->toArray();
                        }
                    }

                    return egw_json_response::get()->data(array(
                        'status' => 'ok',
                        'cmds' => $list));
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

				$cmd->setName($content['commandname']);
                $cmd->setCommand($content['command']);
                $cmd->setAccountId($content['account']);
                $cmd->setMachineId($content['machine']);
                $cmd->setOrder(intval($content['order']));
                $cmd->setSystem($content['system']);
                $cmd->setType($content['type']);
                $cmd->setEvent($content['event']);
                $cmd->setCondition($content['condition']);
				$cmd->setScript($content['scriptcontent']);
				$cmd->setScriptType($content['script_type']);
				$cmd->setCatId($content['cat']);
				$cmd->setSchedulerTime($content['schedulertime']);
				$cmd->setMountPointCheck($content['mountpointcheck']);

				if( $content['options_trayer_show_contextmenu'] == '1' ) {
					$cmd->setOption('trayer_show_contextmenu', '1');
				}
				else {
					$cmd->setOption('trayer_show_contextmenu', '0');
				}

                $cmd->save();
            }

            if( $cmd != null ) {
				$content['commandname']		= $cmd->getName();
				$content['cat']				= $cmd->getCatId();
                $content['command']			= $cmd->getCommand();
                $content['machine']			= $cmd->getMachineId();
                $content['account']			= $cmd->getAccountId();
                $content['system']			= $cmd->getSystem();
                $content['order']			= $cmd->getOrder();
                $content['type']			= $cmd->getType();
                $content['event']			= $cmd->getEvent();
                $content['condition']		= $cmd->getCondition();
                $preserv['uid']				= $cmd->getId();
				$content['scriptcontent']	= $cmd->getScript();
				$content['script_type']		= $cmd->getScriptType();
				$content['schedulertime']	= $cmd->getSchedulerTime();
				$content['mountpointcheck']	= $cmd->getMountPointCheck();
				
				if( $cmd->getOption('trayer_show_contextmenu') == '1' ) {
					$content['options_trayer_show_contextmenu'] = true;
				}

				$content['link_to'] = array(
					'to_id'		=> $cmd->getId(),
					'to_app'	=> 'elogin-cmd',
					);
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
                '2' => '2',
				'3' => '3',
				'4' => '4',
				'5' => '5'
                );

            // type ------------------------------------------------------------
            $option_sel['type'] = array(
                elogin_cmd_bo::TYPE_USER		=> lang('As User'),
                elogin_cmd_bo::TYPE_SERVICE		=> lang('As System'),
				elogin_cmd_bo::TYPE_WIN_STA0	=> lang('As WinSTA0'),
                );

            // event -----------------------------------------------------------
            $option_sel['event'] = array(
				elogin_cmd_bo::EVENT_NONE			=> lang('None'),
                elogin_cmd_bo::EVENT_LOGIN_PRE      => lang('Login Pre (password accept)'),
                //elogin_cmd_bo::EVENT_LOGIN          => lang('Login (System session create)'),
                elogin_cmd_bo::EVENT_LOGIN_AFTER    => lang('Login After (Destop is show)'),
                elogin_cmd_bo::EVENT_LOCKT			=> lang('Lockt'),
                elogin_cmd_bo::EVENT_UNLOCKT		=> lang('Unlockt'),
				elogin_cmd_bo::EVENT_LOGOFF			=> lang('Logoff'),
                );

            // condition -------------------------------------------------------
            $option_sel['condition'] = array(
                elogin_cmd_bo::CONDITION_WITH_CONSOLE   => 'With console',
                elogin_cmd_bo::CONDITION_WAIT           => 'Wait process is end',
                elogin_cmd_bo::CONDITION_LOGGING        => 'Logging',
                );

			// script ----------------------------------------------------------
			$option_sel['script_type'] = array(
				'none'								=> lang('None'),
				elogin_cmd_bo::SCRIPT_BATCHFILE		=> lang('Batchfile'),
				elogin_cmd_bo::SCRIPT_VBS			=> lang('VBS'),
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
                elogin_cmd_bo::TYPE_USER		=> lang('As User'),
                elogin_cmd_bo::TYPE_SERVICE		=> lang('As System'),
				elogin_cmd_bo::TYPE_WIN_STA0	=> lang('As WinSTA0'),
                );

            $label_event = array(
                elogin_cmd_bo::EVENT_NONE			=> lang('None'),
                elogin_cmd_bo::EVENT_LOGIN_PRE      => lang('Login Pre (password accept)'),
                //elogin_cmd_bo::EVENT_LOGIN          => lang('Login (System session create)'),
                elogin_cmd_bo::EVENT_LOGIN_AFTER    => lang('Login After (Destop is show)'),
                elogin_cmd_bo::EVENT_LOCKT			=> lang('Lockt'),
                elogin_cmd_bo::EVENT_UNLOCKT		=> lang('Unlockt'),
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
                            'name'		=> $tcmd->getName(),
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

		/**
		 * cmd_delete
		 *
		 * @param array $content
		 */
		public function cmd_delete($content=null) {
			$uid = ( isset($content['uid']) ? $content['uid'] : null);
			$uid = ( $uid == null ? (isset($_GET['uid']) ? $_GET['uid'] : null) : $uid);

			if( $uid != null ) {
                $cmd = new elogin_cmd_bo($uid);
				$cmd->delete();
            }

			//egw_framework::refresh_opener('', 'elogin', $uid, 'delete');
			egw_framework::window_close();
			exit;
		}
    }