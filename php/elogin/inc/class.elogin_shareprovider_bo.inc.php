<?php

    /**
	 * ELogin - Egroupware
	 * @link http://www.hw-softwareentwicklung.de
	 * @author Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
	 * @package elogin
	 * @copyright (c) 2012-17 by Stefan Werfling <stefan.werfling-AT-hw-softwareentwicklung.de>
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
		 * description
		 * @var string
		 */
		protected $_description = '';

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
		 * protocol
		 * @var string
		 */
		protected $_protocol = '';

		/**
		 * api version
		 * @var string
		 */
		protected $_api_version = '';

		/**
		 * collectiv user share
		 * @DE Sammelbenutzerfreigabe
		 * @var string
		 */
		protected $_collectiv_user_share = '';

		/**
		 * collectiv group share
		 * @DE Sammelgruppenfreigabe
		 * @var string
		 */
		protected $_collectiv_group_share = '';

		/**
		 * auto add users by group
		 * @DE Automatische anlegen der Benutzer von der Gruppe x
		 * @var string
		 */
		protected $_autoadd_users_group = '';

		/**
		 * last update
		 * @var int
		 */
		protected $_last_update = 0;

		/**
		 * last task update
		 * @var last task update
		 */
		protected $_last_task_update = 0;

		/**
		 * device info
		 * @var array
		 */
		protected $_device_info = array();

		/**
		 * connection time out
		 * important to set for hang up effect
		 * @var int
		 */
		protected $_cto = 300;

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
		 * @param string $id
         */
        public function __construct($id=null) {
            $this->_id = $id;

            $data = self::read($id);

            if( $data ) {
                $this->_provider_name			= $data['el_provider_name'];
                $this->_account_server			= $data['el_account_server'];
                $this->_account_port			= intval($data['el_account_port']);
                $this->_account_user			= $data['el_account_user'];
                $this->_account_password		= $data['el_account_password'];
                $this->_mount_address			= $data['el_mount_address'];
				$this->_activ					= ($data['el_activ'] == '1' ? true : false);
				$this->_protocol				= $data['el_protocol'];
				$this->_api_version				= $data['el_api_version'];
				$this->_description				= $data['el_description'];
				$this->_collectiv_user_share	= $data['el_collectiv_user_share'];
				$this->_collectiv_group_share	= $data['el_collectiv_group_share'];
				$this->_autoadd_users_group		= $data['el_autoadd_users_group'];
				$this->_last_update				= $data['el_last_update'];
				$this->_last_task_update		= $data['el_last_task_update'];
				$this->_device_info				= json_decode($data['el_device_info'], true);
				$this->_cto						= intval($data['el_cto']);
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
                $provider->_id						= $this->_id;
                $provider->_provider_name			= $this->_provider_name;
                $provider->_account_server			= $this->_account_server;
                $provider->_account_port			= $this->_account_port;
                $provider->_account_user			= $this->_account_user;
                $provider->_account_password		= $this->_account_password;
                $provider->_username				= $this->_username;
                $provider->_mount_address			= $this->_mount_address;
				$provider->_activ					= $this->_activ;
				$provider->_protocol				= $this->_protocol;
				$provider->_api_version				= $this->_api_version;
				$provider->_description				= $this->_description;
				$provider->_collectiv_user_share	= $this->_collectiv_user_share;
				$provider->_collectiv_group_share	= $this->_collectiv_group_share;
				$provider->_autoadd_users_group		= $this->_autoadd_users_group;
				$provider->_last_update				= $this->_last_update;
				$provider->_last_task_update		= $this->_last_task_update;
				$provider->_device_info				= $this->_device_info;
				$provider->_cto						= $this->_cto;

                $provider->_construct2();

                return $provider;
            }

            return null;
        }

		/**
		 * cast
		 * @param elogin_shareprovider_bo $rawobject
		 * @return elogin_shareprovider_bo
		 */
		static public function cast($rawobject) {
			if( !($rawobject instanceof elogin_shareprovider_bo) ) {
				return null;
			}

			return $rawobject->_cast();
		}

		/**
		 * getInstanceProviderName
		 * @return string
		 */
		public function getInstanceProviderName() {
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
		 * getDescription
		 * @return string
		 */
		public function getDescription() {
			return $this->_description;
		}

		/**
		 * setDescription
		 * @param string $description
		 */
		public function setDescription($description) {
			$this->_description = $description;
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
		 * getProtocol
		 * @return string
		 */
		public function getProtocol() {
			return $this->_protocol;
		}

		/**
		 * setProtocol
		 * @param string $protocol
		 */
		public function setProtocol($protocol) {
			$this->_protocol = $protocol;
		}

		/**
		 * getApiVersion
		 * @return string
		 */
		public function getApiVersion() {
			return $this->_api_version;
		}

		/**
		 * setApiVersion
		 * @param string $version
		 */
		public function setApiVersion($version) {
			$this->_api_version = $version;
		}

		/**
		 * getCollectiveUserShare
		 * @return string
		 */
		public function getCollectiveUserShare() {
			return $this->_collectiv_user_share;
		}

		/**
		 * setCollectiveUserShare
		 * @param string $share
		 */
		public function setCollectiveUserShare($share) {
			$this->_collectiv_user_share = $share;
		}

		/**
		 * getCollectiveGroupShare
		 * @return string
		 */
		public function getCollectiveGroupShare() {
			return $this->_collectiv_group_share;
		}

		/**
		 * setCollectiveGroupShare
		 * @param string $share
		 */
		public function setCollectiveGroupShare($share) {
			$this->_collectiv_group_share = $share;
		}

		/**
		 * getLastUpdate
		 * @return int
		 */
		public function getLastUpdate() {
			return $this->_last_update;
		}

		/**
		 * getLastTaskUpdate
		 * @return int
		 */
		public function getLastTaskUpdate() {
			return $this->_last_task_update;
		}

		/**
		 * getDeviceInfo
		 * @param boolean $update
		 * @return array
		 */
		public function getDeviceInfo($update=false) {
			return $this->_device_info;
		}

		/**
		 * setCto
		 * @param int $cto
		 */
		public function setCto($cto=300) {
			$this->_cto = $cto;
		}

		/**
		 * getCto
		 * @return int
		 */
		public function getCto() {
			return $this->_cto;
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
         * isUserSharesDisabled
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
		 * getProtocolNames
		 * @return array|null
		 */
		public function getProtocolNames() {
			return null;
		}

		/**
		 * getApiVersions
		 * @return array|null
		 */
		public function getApiVersions() {
			return null;
		}

        /**
         * save
         */
        public function save() {
            $data = array();

            if( $this->_id ) {
                $data['el_unid'] = $this->_id;
            }

			$this->_last_update = time();

            $data['el_provider_name']       = $this->_provider_name;
            $data['el_account_server']      = $this->_account_server;
            $data['el_account_port']        = $this->_account_port;
            $data['el_account_user']        = $this->_account_user;
            $data['el_account_password']    = $this->_account_password;
            $data['el_mount_address']       = $this->_mount_address;
			$data['el_activ']				= ($this->_activ == true ? '1' : '0');
			$data['el_description']			= $this->_description;
			$data['el_protocol']			= $this->_protocol;
			$data['el_api_version']			= $this->_api_version;
			$data['el_collectiv_share']		= $this->_collectiv_share;
			$data['el_last_update']			= $this->_last_update;
			$data['el_last_task_update']	= $this->_last_task_update;
			$data['el_device_info']			= json_encode($this->_device_info);
			$data['el_cto']					= $this->_cto;

            $return = self::_write($data);

            if( $return ) {
                if( !($this->_id) ) {
                    $this->_id = $return;
                }
            }
        }

		/**
		 * updateLastTaskDate
		 * @return boolean
		 */
		public function updateLastTaskDate() {
			if( $this->_id ) {
                $data['el_unid']				= $this->_id;
				$data['el_last_task_update']	= time();

				$return = self::_write($data);

				if( $return ) {
					return true;
				}
            }

			return false;
		}

		/**
		 * updateDeviceInfo
		 * @return boolean
		 */
		public function updateDeviceInfo() {
			if( $this->_id ) {
				if( $this->isLogin() ) {
					$this->getDeviceInfo(true);

					if( is_array($this->_device_info) && (count($this->_device_info) > 0) ) {
						$this->_last_update = time();

						$data['el_unid']				= $this->_id;
						$data['el_last_update']			= $this->_last_update;
						$data['el_device_info']			= json_encode($this->_device_info);


						$return = self::_write($data);

						if( $return ) {
							return true;
						}
					}
				}
			}

			return false;
		}

		/**
		 * getDeviceSizeTotal
		 * @param boolean $update
		 * @return boolean
		 */
		public function getDeviceSizeTotal($update=false) {
			return false;
		}

		/**
		 * getDeviceSizeUsed
		 * @param boolean $update
		 * @return boolean
		 */
		public function getDeviceSizeUsed($update=false) {
			return false;
		}

		/**
		 * setUseCacheLogging
		 * @param boolean $logging
		 */
		public function setUseCacheLogging($logging=false) {}

		/**
		 * getCacheLogs
		 * @return array
		 */
		public function getCacheLogs() {
			return array();
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
			$bdir = __DIR__ . '/plugins/shares/';

            if( !($dirs=@dir($bdir)) ) {
				return array();
			}

			$names = array();

			while( ($entry=($dirs->read())) ) {
				if( ($entry == '.') || ($entry == '..') ) {
                	continue;
            	}

				if( (is_dir($bdir . $entry)) ) {
					$file = $bdir . $entry .
						'/class.elogin_' . $entry . '_shareprovider_bo.inc.php';

					if( file_exists($file) ) {
						require_once($file);

						$class = 'elogin_' . $entry . '_shareprovider_bo';

						if( class_exists($class, false) ) {
							$instance = new $class();

							if( $instance instanceof elogin_shareprovider_bo ) {
								$names[$entry] = $instance->getInstanceProviderName();
							}
						}
					}
				}
				else {
					continue;
				}
			}

			return $names;
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