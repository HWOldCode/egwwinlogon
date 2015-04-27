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
        const TABLE = 'egw_elogin_usershares';

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
         * provider id
         * @var string
         */
        protected $_providerid = null;

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
         * share infos
         * @var array
         */
        protected $_shareinfos = array();

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
                $data = self::read($id);

                if( $data ) {
                    $this->_providerid = $data['el_provider_id'];
                    $this->_egwaccountid = $data['el_egw_account'];
                    $this->_sharepassword = $data['el_sharepassword'];

                    if( $data['el_shareinfo'] != '' ) {
                        $this->_shareinfos = json_decode($data['el_shareinfo']);
                    }
                }

                $this->_id = $id;
            }
        }

        /**
         * getId
         *
         * @return string
         */
        public function getId() {
            return $this->_id;
        }

        /**
         * setProviderId
         *
         * @param string $id
         */
        public function setProviderId($id) {
            $this->_providerid =  $id;
        }

        /**
         * getProviderId
         *
         * @return string
         */
        public function getProviderId() {
            return $this->_providerid;
        }

        /**
         * getProvider
         *
         * @return elogin_shareprovider_bo|null
         */
        public function getProvider() {
            $tporvider = elogin_shareprovider_bo::i($this->_providerid);
            $tporvider->setUsername($this->getUsername());
            return $tporvider;
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
         * _getRandomPassword
         * @param int $len
         * @return string
         */
        protected function _getRandomPassword($len=12) {
            $alphabet       = "abcdefghijklmnopqrstuwxyzABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
            $pass           = array();
            $alphaLength    = strlen($alphabet) - 1;

            for( $i=0; $i<$len; $i++) {
                $n = rand(0, $alphaLength);
                $pass[] = $alphabet[$n];
            }

            return implode($pass);
        }

        /**
         * setSharePassword
         *
         * @param string $password
         */
        public function setSharePassword($password=null) {
            if( $password == null ) {
                $password = $this->_getRandomPassword();
            }

            $this->_sharepassword = $password;
        }

        /**
         * getShares
         *
         * @return array
         */
        public function getShares() {
            $shareprovider = $this->getProvider();
            $shares = array();

            if( $shareprovider instanceof elogin_shareprovider_bo ) {
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

                    $cmd = str_replace('<drivename>', $tshare['drivename'], $cmd);
                    $cmd = str_replace('<server>', $this->getProvider()->getAccountServer(), $cmd);
                    $cmd = str_replace('<share>', $tshare['name'], $cmd);
                    $cmd = str_replace('<username>', $username, $cmd);
                    $cmd = str_replace('<password>', $sharepassword, $cmd);

                    $cmds[] = $cmd;
                }
            }

            return $cmds;
        }

        /**
         * read
         *
         * @param string $id
         * @return boolean|array
         */
        static public function read($id=null) {
            $where = array(self::TABLE . '.el_unid=' . "'" . (string)$id . "'");
            $cols = array(self::TABLE . '.*');
            $join = array();

            if (!($data = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                '', '', 0, $join)->fetch()))
            {
                return false;
            }

            return $data;
        }

        /**
         * get_rows
         *
         * @param type $query
         * @param type $rows
         * @param type $readonlys
         * @return type
         */
        static public function get_rows(&$query, &$rows, &$readonlys) {
            $where = array();
            $cols = array(self::TABLE . '.*');
            $join = array();

            if (!($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                '', '', 0, $join)))
            {
                return array();
            }

            $rows = array();

            foreach( $rs as $row ) {
				$row = (array) $row;
                $rows[] = $row;
            }

            return count($rows);
        }
    }

    /**
     * init_static
     */
    elogin_usershares_bo::init_static();