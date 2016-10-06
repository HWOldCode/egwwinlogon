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
     * elogin_shareprovider_bo
     */
    class elogin_shareprovider_bo {

        /**
         * TABLE
         */
        const TABLE = 'egw_elogin_shareproviders';

        /**
		 * db
         * Reference to global db object
         * @var egw_db
         */
        static protected $_db;

        /**
         * id
         * @var string
         */
        protected $_id = null;

        /**
         * provider name
         * @var string
         */
        protected $_provider_name = '';

		/**
		 * activ
		 * @var boolean
		 */
		protected $_activ = false;

        /**
         * username
         * @var string
         */
        protected $_username = null;

        /**
         * account server
         * @var string
         */
        protected $_account_server = '';

        /**
         * account port
         * @var int
         */
        protected $_account_port = 0;

        /**
         * account user
         * @var string
         */
        protected $_account_user = '';

        /**
         * account password
         * @var string
         */
        protected $_account_password = '';

        /**
         * mount address
         * @var string
         */
        protected $_mount_address = '';

        /**
		 * init_static
         * Init our static properties
         */
        static public function init_static() {
            self::$_db = $GLOBALS['egw']->db;
        }

        /**
         * i
         * return a instance by db
         * @param string $id
         * @return elogin_shareprovider_bo
         */
        static public function i($id) {
            $provider = new elogin_shareprovider_bo($id);
            return $provider->_cast();
        }

        /**
         * __construct
         */
        public function __construct($id=null) {
            $this->_id = $id;

            $data = self::read($id);

            if( $data ) {
                $this->_provider_name       = $data['el_provider_name'];
                $this->_account_server      = $data['el_account_server'];
                $this->_account_port        = intval($data['el_account_port']);
                $this->_account_user        = $data['el_account_user'];
                $this->_account_password    = $data['el_account_password'];
                $this->_mount_address       = $data['el_mount_address'];
				$this->_activ				= ($data['el_activ'] == '1' ? true : false);
            }
        }

        /**
         * _construct2
         */
        protected function _construct2() {
            // overriding
        }

        /**
         * _cast
         * @return elogin_shareprovider_bo
         */
        private function _cast() {
            $provider = elogin_shareprovider_bo::getShareProviderByName(
				$this->_provider_name
				);

            if( $provider instanceof elogin_shareprovider_bo ) {
                $provider->_id                  = $this->_id;
                $provider->_provider_name       = $this->_provider_name;
                $provider->_account_server      = $this->_account_server;
                $provider->_account_port        = $this->_account_port;
                $provider->_account_user        = $this->_account_user;
                $provider->_account_password    = $this->_account_password;
                $provider->_username            = $this->_username;
                $provider->_mount_address       = $this->_mount_address;
				$provider->_activ				= $this->_activ;

                $provider->_construct2();

                return $provider;
            }

            return null;
        }

        /**
         * getId
         * @return string
         */
        public function getId() {
            return $this->_id;
        }

        /**
         * setAccount
         * @param string $server
         * @param int $port
         * @param string $user
         * @param string $password
         */
        public function setAccount($server, $port, $user, $password) {
            $this->_account_server		= $server;
            $this->_account_port		= $port;
            $this->_account_user		= $user;
            $this->_account_password	= $password;
        }

		/**
		 * setIsActiv
		 * @param boolean $activ
		 */
		public function setIsActiv($activ) {
			$this->_activ = $activ;
		}

		/**
		 * isActiv
		 * @return boolean
		 */
		public function isActiv() {
			return $this->_activ;
		}

        /**
         * setUsername
         * @param string $username
         */
        public function setUsername($username) {
            $this->_username = $username;
        }

        /**
         * getProviderName
         * @return string
         */
        public function getProviderName() {
            return $this->_provider_name;
        }

        /**
         * setProviderName
         * @param string $name
         */
        public function setProviderName($name) {
            $this->_provider_name = $name;
        }

        /**
         * getAccountServer
         * @return string
         */
        public function getAccountServer() {
            return $this->_account_server;
        }

        /**
         * getAccountPort
         * @return int
         */
        public function getAccountPort() {
            return intval($this->_account_port);
        }

        /**
         * getAccountUser
         * @return string
         */
        public function getAccountUser() {
            return $this->_account_user;
        }

        /**
         * getAccountPassword
         * @return string
         */
        public function getAccountPassword() {
            return $this->_account_password;
        }

        /**
         * setMountAddress
         * @param string $address
         */
        public function setMountAddress($address) {
            $this->_mount_address = $address;
        }

        /**
         * getMountAddress
         * @return string
         */
        public function getMountAddress() {
            return $this->_mount_address;
        }

        /**
         * getShares
         * @return array
         */
        public function getShares() {
            return array();
        }

        /**
         * getSharesByUser
         * @param string|elogin_usershares_bo $account
         * @return array
         */
        public function getSharesByUser($account) {
            return array();
        }

        /**
         * isUsernameExist
         * @param string $username
         * @return boolean
         */
        public function isUsernameExist($username=null) {
            return false;
        }

        /**
         * createUserShares
         * @param int|elogin_usershares_bo $accountid
		 * @return null|elogin_usershares_bo
         */
        public function createUserShares($account) {
            if( is_int($account) ) {
                $usershares = new elogin_usershares_bo();
                $usershares->setUser($account);
                $usershares->setSharePassword();
                $usershares->setProviderId($this->_id);

                $usershares->save();

                return $usershares;
            }
            elseif( $account instanceof elogin_usershares_bo ) {
                return $account;
            }

            return null;
        }

        /**
         * disableUserShares
         * @param string|elogin_usershares_bo $account
         */
        public function disableUserShares($account) {
            return false;
        }

        /**
         * enableUserShares
         * @param string|elogin_usershares_bo $account
         */
        public function enableUserShares($account) {
            return false;
        }

        /**
         * disableUserShares
         * @param string|elogin_usershares_bo $account
         */
        public function isUserSharesDisabled($account) {
            return false;
        }

        /**
         * updatePassword
         * @param elogin_usershares_bo $account
         */
        public function updatePassword($account) {
            return false;
        }

        /**
         * getAllShares
         * @param elogin_usershares_bo $account
         * @return array
         */
        public function getAllShares($account) {
            return array();
        }

        /**
         * createShare
         * @param elogin_usershares_bo $account
         * @param string $sharename
         * @return boolean
         */
        public function createShare($account, $sharename) {
            return false;
        }

        /**
         * setSharePermission
         * @param elogin_usershares_bo $account
         * @param string $sharename
         * @param string $premission
         */
        public function setSharePermission($account, $sharename, $premission="rw") {
            return false;
        }

        /**
         * isLogin
         * @return boolean
         */
        public function isLogin() {
            return false;
        }

        /**
         * login
         * @return boolean
         */
        public function login() {
            return false;
        }

        /**
         * logout
         * @return boolean
         */
        public function logout() {
            return false;
        }

        /**
         * existShareDir
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function existShareDir($usersharename, $dir) {
            return false;
        }

        /**
         * createShareDir
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function createShareDir($usersharename, $dir) {
            return false;
        }

        /**
         * getShareDirList
         * @param string $usersharename
         * @param string $dir
         * @return array
         */
        public function getShareDirList($usersharename, $dir="") {
            return array();
        }

        /**
         * removeAllPermissionDir
         * @param string $usersharename
         * @param string $dir
         * @return boolean
         */
        public function removeAllPermissionDir($usersharename, $dir) {
            return false;
        }

        /**
         * addPermissionDir
         * @param string $usersharename
         * @param string $dir
         * @param string $username
         * @param boolean $read
         * @param boolean $write
         * @return boolean
         */
        public function addPermissionDir($usersharename, $dir, $username, $read=false, $write=false) {
            return false;
        }

        /**
         * addPermissionDir
         * @param string $usersharename
         * @param string $dir
         * @param array $usernames
         * @param boolean $read
         * @param boolean $write
		 * @param boolean $ignorUser
         * @return boolean
         */
        public function addPermissionDirMulti($usersharename, $dir, $usernames, $read=false, $write=false, $ignorUser=true) {
            return false;
        }

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

            $data['el_provider_name']       = $this->_provider_name;
            $data['el_account_server']      = $this->_account_server;
            $data['el_account_port']        = $this->_account_port;
            $data['el_account_user']        = $this->_account_user;
            $data['el_account_password']    = $this->_account_password;
            $data['el_mount_address']       = $this->_mount_address;
			$data['el_activ']				= ($this->_activ == true ? '1' : '0');

            $return = self::_write($data);

            if( $return ) {
                if( !($this->_id) ) {
                    $this->_id = $return;
                }
            }
        }

        /**
         * getShareProviderByName
         * @param string $name
         * @return elogin_shareprovider_bo
         */
        static public function getShareProviderByName($name) {
            $classname  = 'elogin_' . strtolower($name) . '_shareprovider_bo';
            $tclass     = null;

            if( class_exists($classname) ) {
                $tclass = new $classname();
            }

            $lname = strtolower($name);
            $inc = __DIR__ . '/plugins/shares/' . $lname .
                '/class.elogin_' . $lname . '_shareprovider_bo.inc.php';

            if( file_exists($inc) ) {
                require_once($inc);

                if( class_exists($classname) ) {
                    $tclass = new $classname();
                }
            }

            if( $tclass instanceof elogin_shareprovider_bo ) {
                return $tclass;
            }

            return null;
        }

        /**
         * getShareProviderNames
         * @return array
         */
        static public function getShareProviderNames() {
            // TODO
            return array(
                'syno' => 'Synology DSM 5.1'
            );
        }

        /**
         * read
         * @param string $id
         * @return boolean|array
         */
        static public function read($id=null) {
            $where = array(self::TABLE . '.el_unid=' . "'" . (string)$id . "'");
            $cols = array(self::TABLE . '.*');
            $join = '';

            if (!($data = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                false, '', false, -1, $join)->fetch()))
            {
                return false;
            }

            return $data;
        }

        /**
         * _write
         * @param array $data
         */
        static protected function _write(array $data) {
            if( isset($data['el_unid']) ) {
                $unid = $data['el_unid'];
                unset($data['el_unid']);

                self::$_db->update(
                    self::TABLE,
                    $data,
                    array(
                        'el_unid' => $unid,
                        ),
                    __LINE__,
                    __FILE__,
                    'elogin'
                    );
            }
            else {
                $data['el_unid'] = elogin_bo::getPHPUuid();

                self::$_db->insert(
                    self::TABLE,
                    $data,
                    false,
                    __LINE__,
                    __FILE__,
                    'elogin'
                    );
            }

            return $data['el_unid'];
        }

        /**
         * get_rows
         * @param array $query
         * @param array $rows
         * @param array $readonlys
         * @return int
         */
        static public function get_rows(&$query, &$rows, &$readonlys) {
            $where = array();
            $cols = array(self::TABLE . '.*');
            $join = '';

            if (!($rs = self::$_db->select(self::TABLE, $cols, $where, __LINE__, __FILE__,
                false, '', false, -1, $join)))
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

        /**
         * getShareProviders
         * @return array of elogin_shareprovider_bo
         */
        static public function getShareProviders() {
            $query      = array();
            $rows       = array();
            $readonlys  = array();

            self::get_rows($query, $rows, $readonlys);

            $list = array();

            foreach( $rows as $row ) {
                $list[] = new elogin_shareprovider_bo($row['el_unid']);
            }

            return $list;
        }
    }

    /**
     * elogin_shareprovider_bo
     */
    elogin_shareprovider_bo::init_static();