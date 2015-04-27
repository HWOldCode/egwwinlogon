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
     * elogin_bo
     */
    class elogin_bo {

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
        public function getEgroupwareAccounts() {
            $db = $GLOBALS['egw']->db;

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

            return $accountlist;
        }
    }