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
     * elogin_usershares_bo
     */
    class elogin_usershares_bo {

        /**
         * DESTINATION_SYNO
         */
        const DESTINATION_SYNO = 'syno';

        /**
         * TABLE
         */
        const USERSHARES_TABLE = 'egw_elogin_usershares';

        /**
         * Reference to global db object
         *
         * @var egw_db
         */
        static protected $_db;

        /**
         * id
         * @var string
         */
        protected $_id = null;

        /**
         * egroupware account id
         * @var int
         */
        protected $_egwaccountid = null;

        /**
         * sharepassword
         *
         * @var string
         */
        protected $_sharepassword = null;

        /**
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * constructor
         *
         * @param string $id
         * @param string $destination
         */
        public function __construct($id=null) {
            if( $id != null ) {
                $this->_id = $id;
            }

            $this->_read();
        }

        protected function _read() {
            /*$rs = self::$db->select(
                self::USERSHARES_TABLE,
                array(),

                );*/
        }

        /**
         * getUsername
         *
         * @return string
         */
        public function getUsername() {
            return accounts::id2name($this->_egwaccountid);
        }

        /**
         * setUser
         *
         * @param int $accountid
         */
        public function setUser($accountid=null) {
            if( $accountid == null ) {
                $accountid = $GLOBALS['egw_info']['user']['account_id'];
            }

            $this->_egwaccountid = $accountid;
        }

        /**
         * getSharePassword
         *
         * @return string
         */
        public function getSharePassword() {
            return $this->_sharepassword;
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            //$shareprovider = elogin_shareprovider_bo::getShareProviderByName();
            $shares = array();

            if( $shareprovider instanceof elogin_shareprovider_bo ) {
                $shareprovider->setUsername($this->getUsername());

                $shares = $shareprovider->getShares();

                // TODO Share Info and Setting
            }

            return $shares;
        }

        /**
         * getCmds
         * @param string $system
         */
        public function getCmds($system=null) {
            if( $system == null ) {
                $system = elogin_bo::SYSTEM_WIN;
            }

            $replace_str = null;
            $shares = $this->getShares();

            switch( $system ) {
                case elogin_bo::SYSTEM_WIN:
                    $replace_str = "net use <drivename>: \\<server>\<share> /user:<username> <password>";
                    break;
            }

            $cmds = array();

            if( $replace_str ) {
                $username = $this->getUsername();
                $sharepassword = $this->getSharePassword();

                foreach( $shares as $tshare ) {
                    $cmd = $replace_str;

                    $cmd = str_replace('<drivename>', '', $cmd);
                    $cmd = str_replace('<server>', '', $cmd);
                    $cmd = str_replace('<share>', $tshare['name'], $cmd);
                    $cmd = str_replace('<username>', $username, $cmd);
                    $cmd = str_replace('<password>', $sharepassword, $cmd);

                    $cmds[] = $cmd;
                }
            }

            return $cmds;
        }
    }

    /**
     * init_static
     */
    elogin_usershares_bo::init_static();