<?php

    /**
	 * ELogin - Egroupware
	 *
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-14 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @license by Huettner und Werfling Softwareentwicklung GbR <www.hw-softwareentwicklung.de>
	 * @version $Id:$
	 */

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
            'ajax_treelist'         => true,
            'ajax_cache'            => true,
            'ajax_machine_info'     => true,
            'ajax_cmd'              => true,
            'ajax_logging'          => true,
            'cronjob_hand'          => true,
            );

        public function ajax_treelist($content=array()) {

        }

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
                $syn->getFileSharesList('/group Admins');
            }
            echo "Hello World";

            //$t = new elogin_usershares_bo('test');
            //var_dump($t->getCmds());
exit;*/
            elogin_sharehandler_bo::set_async_job(false);
            elogin_sharehandler_bo::set_async_job(true);

            $tpl = new etemplate_new('elogin.index');
			$tpl->exec(
                'elogin.elogin_ui.index',
                $content,
                array(),
                array());
        }

        public function cronjob_hand($content=array()) {
            elogin_sharehandler_bo::handle();

            $tpl = new etemplate_new('elogin.index');
			$tpl->exec(
                'elogin.elogin_ui.index',
                $content,
                array(),
                array());
        }

        /**
		 * ajax_cache
		 *
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
                'egw_accounts' => $accountlist,
                'encryption_type' => $type
                );

            return egw_json_response::get()->data($cacheData);
        }

        

        /**
         * ajax_cmd
         *
         * @param array $content
         * @return mixed
         */
        public function ajax_cmd($content=array()) {
            error_log(__METHOD__.__LINE__.':'.  var_export($content, true));
        }


    }