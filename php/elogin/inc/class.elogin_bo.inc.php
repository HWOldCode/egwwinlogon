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

    /**
     * elogin_bo
     */
    class elogin_bo {

		// consts
        const SYSTEM_WIN    = 'win';

        const EVENT_WINDOWS_SESSION_CHANGE_LOGON    = 5;
        const EVENT_WINDOWS_SESSION_CHANGE_LOGOFF   = 6;

        const RECEIVER_WINDOWS_SERVICE_SYSTEM   = 0;
        const RECEIVER_WINDOWS_SERVICE_USER     = 1;
        const RECEIVER_WINDOWS_APP              = 2;

        /**
         * getEgroupwareAccounts
         * @return array of egroupware accounts
         */
        static public function getEgroupwareAccounts() {
            $db = $GLOBALS['egw']->db;

            $accountlist = array_values($GLOBALS['egw']->accounts->search(array(
                'type' => 'accounts',
                'start' => null,
                'order' => '',
                'sort' => '',
                'query' => '',
                'offset' => null,
                'query_type' => 'all',
                'active' => false
                )));

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

            return $accountlist;
        }

        /**
         * getEgroupwareAccountGroups
         * @param int $accountid
         * @return array
         */
        static public function getEgroupwareAccountGroups($accountid) {
            $list = $GLOBALS['egw']->accounts->memberships($accountid);
            return $list;
        }

        /**
		 * getPHPUuid
		 * return a unid
		 * @return string UUID aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee
		 */
		static public function getPHPUuid() {
			$randstr = md5(uniqid(mt_rand(), true));
			$uuid = substr($randstr,0,8) . '-';
			$uuid .= substr($randstr,8,4) . '-';
			$uuid .= substr($randstr,12,4) . '-';
			$uuid .= substr($randstr,16,4) . '-';
			$uuid .= substr($randstr,20,12);
			return $uuid;
		}
    }