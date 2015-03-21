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
            'ajax_cache' => true,
            'ajax_cmd' => true,
            );

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
         * ajax_cmd
         *
         * @param array $content
         * @return mixed
         */
        public function ajax_cmd($content=array()) {

        }
    }