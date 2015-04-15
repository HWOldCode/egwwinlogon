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
            'index'             => true,
            'ajax_cache'        => true,
            'ajax_machine_info' => true,
            'ajax_cmd'          => true,
            'ajax_loggin'       => true,
            );

        /**
         * index
         * @param array $content
         */
        public function index($content=array()) {
            require_once('plugins/syndms/lib/syndms.client.php');

            $syn = new SyndmsClient('192.168.11.4');
            if( $syn->login('admin', '1234') ) {
                $syn->getUsers();
            }
            echo "Hello World";
        }

        /**
		 * ajax_cache
		 *
		 * @param array $content
		 * @return mixed
		 */
		public function ajax_cache($content=array()) {
            $db = $GLOBALS['egw']->db;
            $type = @$GLOBALS['egw_info']['server']['sql_encryption_type'] ?
                strtolower($GLOBALS['egw_info']['server']['sql_encryption_type']) : 'md5';

            $accountlist = $GLOBALS['egw']->accounts->get_list(
                'accounts',
                null,
                '',
                '',
                '',
                null,
                'all');

            foreach( $accountlist as &$account ) {
                $where = array(
                    'account_id' => $account['account_id'],
                    );

                if( ($row = $db->select('egw_accounts',
                    'account_pwd', $where,__LINE__,__FILE__)->fetch() ) )
                {
                    $account['account_pwd'] = $row['account_pwd'];
                }
            }

            // return data for cache
            $cacheData = array(
                'egw_accounts' => $accountlist,
                'encryption_type' => $type
                );

            return egw_json_response::get()->data($cacheData);
        }

        /**
         * ajax_machine_info
         * @param array $content
         */
        public function ajax_machine_info($content=array()) {

            return egw_json_response::get()->data(array('status' => 'ok'));
        }

        /**
         * ajax_cmd
         *
         * @param array $content
         * @return mixed
         */
        public function ajax_cmd($content=array()) {

        }

        /**
         * ajax_loggin
         * @param array $content
         */
        public function ajax_loggin($content=array()) {

        }
    }