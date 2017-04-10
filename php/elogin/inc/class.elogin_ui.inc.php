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
     * eline_ui
     */
    class elogin_ui {

        /**
         * public methode
         * @var array
         */
        public $public_functions = array(
            'index'                 => true,
            'cronjob_hand'          => true,
            );

        /**
         * index
         * @param array $content
         */
        public function index($content=array()) {
/*
            require_once('plugins/shares/syno/lib/syndms.client.php');

            $syn = new SyndmsClient('192.168.11.4');
            if( $syn->login('admin', '1234') ) {
                //var_dump($syn->getUsers());
                //var_dump($syn->getUserGroups('test'));
                //var_dump($syn->getGroups());
                //var_dump($syn->getShares());
                //var_dump($syn->createUser('test3', '1234'));
                //var_dump($syn->removeUserByGroup('administrators', 'test3'));
                //var_dump($syn->addUserToGroup('administrators', 'test3'));
                //var_dump($syn->createShare('test3435', '/volume1'));
                //var_dump($syn->getUser('test3'));
                //var_dump($syn->disableUser('test3'));
                //var_dump($syn->setSharePermission('test3435', 'test2', 'rw'));
                //var_dump($syn->getFileSharesList('/group Admins', 1000, array('only_dir' => true)));
                //var_dump($syn->getFileShareACLs('/volume1/group Admins/test'));
                //var_dump($syn->createDirShare('/group Admins', 'papa23'));
                var_dump($syn->setFileShareACLs('/volume1', '/group Admins/test', array()));

            }
            echo "Hello World";

            //$t = new elogin_usershares_bo('test');
            //var_dump($t->getCmds());
exit;*/
            elogin_sharehandler_bo::set_async_job(false);
            //elogin_sharehandler_bo::set_async_job(true);

            $tpl = new Etemplate('elogin.index');
			$tpl->exec(
                'elogin.elogin_ui.index',
                $content,
                array(),
                array());
        }

		/**
		 * cronjob_hand
		 * @param array $content
		 */
        public function cronjob_hand($content=array()) {
			//$GLOBALS['egw']->session->commit_session();

            //elogin_sharehandler_bo::handle();

			try {
				elogin_handler_singleshare_bo::handle();
			}
			catch( Exception $ex ) {
				echo $ex;
			}

            $tpl = new Etemplate('elogin.index');
			$tpl->exec(
                'elogin.elogin_ui.index',
                $content,
                array(),
                array());
        }

        /**
		 * ajax_cache
		 * @param array $content
		 * @return mixed
		 */
		public function ajax_cache($content=array()) {
            $elbo = new elogin_bo();
            $accountlist = $elbo->getEgroupwareAccounts();

            $type = @$GLOBALS['egw_info']['server']['sql_encryption_type'] ?
                strtolower($GLOBALS['egw_info']['server']['sql_encryption_type']) : 'md5';

            // return data for cache
            $cacheData = array(
                'egw_accounts'		=> $accountlist,
                'encryption_type'	=> $type
                );

            return Api\Json\Response::get()->data($cacheData);
        }

		/**
		 * ajax_eworkflow_request
		 * @param array $content
		 */
		public function ajax_eworkflow_request($content=array()) {
			$erro_msg = null;

			if( $GLOBALS['egw_info']['user']['apps']['eworkflow'] ) {
				$fac	= eworkflow_factory_bo::i();
				$wm		= $fac->getWorkflowManager();
				$pl		= $wm->getProcessList();

				if( isset($content['trigger']) ) {
					$trigger_entry = eworkflow_entrys_bo::loadEntry($content['trigger']);

					if( $trigger_entry instanceof elogin_action_machine_trigger ) {
						$process	= null;

						$data = array();

						if( isset($content['data']) ) {
							if( is_array($content['data']) ) {
								$data = $content['data'];
							}
							elseif( is_string($content['data']) ) {
								if( strpos($content['data'], "%7B") !== false ) {
									$content['data'] = urldecode($content['data']);
								}

								$data = json_decode($content['data'], true);

								if( $data == null ) {
									return Api\Json\Response::get()->data(array(
										'status'	=> 'error',
										'msg'		=> 'json decode: ' . $content['data']
										));
								}
							}
						}

						if( isset($content['uid']) ) {
							$data['elogin_machineid'] = $content['uid'];
						}

						$start_entry = eworkflow_entrys_bo::loadEntry(
							$trigger_entry->getGroupEntryId());

						// Process
						// -----------------------------------------------------
						if( isset($content['processid']) ) {
							$tprocess = $pl->getProcessById($content['processid']);

							if( $tprocess != null ) {
								if( $tprocess->getWorkflowStart()->getId() == $start_entry->getId() ) {
									if( !$tprocess->isEnd() ) {
										$process = $tprocess;
									}
								}
							}
						}
						else {
							$process = $wm->createProcess($start_entry);
							$process->setCurrentWorkflowEntry($trigger_entry);

							$pl->addProcess($process);
						}

						if( $process != null ) {
							// execute
							// -------------------------------------------------

							$process->execute($data);

							// return
							// -------------------------------------------------

							if( $process->isEnd() ) {
								$return_data = array();

								if( $trigger_entry->getReturnParams() == '1' ) {
									$return_data = $process->getParamList()->getParamsArray();
								}

								return Api\Json\Response::get()->data(array(
									'status'	=> 'ok',
									'data'		=> $return_data,
									));
							}
							else {
								return Api\Json\Response::get()->data(array(
									'status'	=> 'stop',
									'processid'	=> $process->getId(),
									'msg'		=> 'Process issnt end'
									));
							}
						}

						$erro_msg = 'Process not found';
					}

					$erro_msg = 'Trigger Id not set';
				}

				$erro_msg = 'Trigger Id not set';
			}

			if( $erro_msg == null ) {
				$erro_msg = 'Access denied by app EWorkflow';
			}

			return Api\Json\Response::get()->data(array(
				'status'	=> 'error',
				'msg'		=> $erro_msg
				));
		}
    }